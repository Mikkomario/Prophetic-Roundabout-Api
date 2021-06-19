package vf.pr.api.rest.zoom

import utopia.access.http.Headers
import utopia.access.http.Method.{Get, Post}
import utopia.access.http.Status.{Accepted, BadRequest, InternalServerError, Unauthorized}
import utopia.disciple.http.request.{Request, StringBody}
import utopia.exodus.database.access.single.DbUser
import utopia.exodus.rest.util.AuthorizedContext
import utopia.exodus.util.ExodusContext.uuidGenerator
import utopia.flow.async.AsyncExtensions._
import utopia.flow.datastructure.immutable.{Model, Value}
import utopia.flow.generic.ValueConversions._
import utopia.flow.time.Now
import utopia.flow.time.TimeExtensions._
import utopia.flow.util.CollectionExtensions._
import utopia.nexus.http.Path
import utopia.nexus.rest.{Context, LeafResource, ResourceWithChildren}
import utopia.nexus.result.Result
import utopia.vault.database.Connection
import vf.pr.api.database.access.single.setting.ZoomSettings
import vf.pr.api.database.access.single.zoom.DbZoomAuthAttempt
import vf.pr.api.database.ExodusDbExtensions._
import vf.pr.api.database.model.zoom.{ZoomAuthAttemptModel, ZoomRefreshTokenModel, ZoomSessionTokenModel}
import vf.pr.api.model.error.RequestFailedException
import vf.pr.api.model.partial.zoom.{ZoomAuthAttemptData, ZoomRefreshTokenData, ZoomSessionTokenData}
import vf.pr.api.util.Log
import vf.pr.api.util.Globals._

import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
 * Used for performing the first time login to Zoom
 * @author Mikko Hilpinen
 * @since 16.6.2021, v0.1
 */
object ZoomLoginNode extends ResourceWithChildren[AuthorizedContext]
{
	// IMPLEMENTED  ---------------------------------
	
	override def children = Vector(ZoomLoginResponseNode)
	
	override def name = "login"
	
	override def allowedMethods = Vector(Get)
	
	override def toResponse(remainingPath: Option[Path])(implicit context: AuthorizedContext) =
	{
		// Uses session auth
		context.sessionKeyAuthorized { (session, connection) =>
			implicit val c: Connection = connection
			// Checks whether the user has already been authenticated in Zoom
			// Case: Already authorized => redirects to success result page or returns with status
			if (DbUser(session.userId).isZoomAuthorized)
				ZoomSettings.resultPageUri match
				{
					case Some(resultUri) => Result.Redirect(resultUri + "/success")
					case None => Result.Success(Value.empty, description = Some("Already authorized"))
				}
			// Case: Not yet authorized => redirects the client to Zoom authorization
			else
			{
				// Reads required settings
				ZoomSettings.authenticationUri.flatMap { authenticationUri =>
					ZoomSettings.redirectUri.flatMap { redirectUri =>
						ZoomSettings.clientId.map { clientId =>
							// Records a new authentication attempt to the DB
							val token = uuidGenerator.next()
							ZoomAuthAttemptModel.insert(ZoomAuthAttemptData(session.userId, token))
							// Redirects the client to the correct url
							Result.Redirect(s"$authenticationUri?response_type=code&redirect_uri=$redirectUri&client_id=$clientId&state=$token")
						}
					}
				}.getOrMap { error =>
					Log.error("Zoom.login", error)
					Result.Failure(InternalServerError, "Required server side specifications are missing")
				}
			}
		}
	}
	
	
	// NESTED   ----------------------------------
	
	object ZoomLoginResponseNode extends LeafResource[Context]
	{
		override def name = "response"
		
		override def allowedMethods = Vector(Get)
		
		override def toResponse(remainingPath: Option[Path])(implicit context: Context) =
		{
			// Makes sure the request has code and state parameters
			val parameters = context.request.parameters
			val result = parameters("code").string match
			{
				case Some(code) =>
					parameters("state").string match
					{
						case Some(token) =>
							connectionPool.tryWith { implicit connection =>
								// Finds the user id matching that token and makes sure that token is still valid
								DbZoomAuthAttempt.open.forToken(token) match
								{
									case Some(attempt) =>
										// Uses the code that was received to authenticate the user
										val authenticationFuture = consumeCode(attempt.userId, code)
										// Handles authentication result
										authenticationFuture.foreachResult {
											// Case: Authentication succeeded => closes the attempt
											case Success(_) => closeAuthAttempt(attempt.id)
												// TODO: Update user information based on Zoom account data
											case Failure(error) => Log.error.withoutConnection(
												"Zoom.login.response", error = Some(error))
										}
										// Waits for the result for some time and serves a response based on the result
										// Either redirects or returns a status code
										ZoomSettings.resultPageUri match
										{
											// Case: Redirect supported
											case Some(resultPageUri) =>
												val resultPath = authenticationFuture.waitFor(
													ZoomSettings.maxUserWaitDuration) match
												{
													// Case: Operation finished before timeout, adds the result to
													// the redirect uri
													case Success(result) =>
														s"/${if (result.isSuccess) "success" else "failure"}"
													// Case: Timeout
													case Failure(_) => ""
												}
												Result.Redirect(resultPageUri + resultPath)
											// Case: No redirect supported => Returns a direct response
											case None =>
												authenticationFuture.waitFor(ZoomSettings.maxUserWaitDuration) match
												{
													case Success(result) =>
														result match
														{
															// Case: Auth success
															case Success(_) => Result.Success(Value.empty,
																description = Some("Zoom authentication succeeded"))
															// Case: Auth failure
															case Failure(error) =>
																Result.Failure(InternalServerError, error.getMessage)
														}
													// Case: Timeout
													case Failure(_) => Result.Success(Value.empty, Accepted,
														Some("Zoom authentication in progress"))
												}
										}
									case None =>
										Result.Failure(Unauthorized, "Invalid, expired or closed token (state)")
								}
							}.getOrMap { error =>
								Log.error.withoutConnection("Zoom.login.db", error = Some(error))
								Result.Failure(InternalServerError, error.getMessage)
							}
						case None => Result.Failure(BadRequest, "Query parameter 'state' required")
					}
				case None => Result.Failure(BadRequest, "Query parameter 'code' required")
			}
			
			result.toResponse
		}
	}
	
	private def consumeCode(userId: Int, code: String) =
	{
		// Makes sure the required settings have been initialized
		ZoomSettings.clientIdAndSecret.flatMap { case (clientId, clientSecret) =>
			ZoomSettings.redirectUri.flatMap { redirectUri =>
				ZoomSettings.tokenUri.map { tokenUri =>
					// Acquires the session and refresh tokens from Zoom,
					// using the specified code
					val requestTime = Now.toInstant
					zoomGateway.modelResponseFor(Request(tokenUri, Post,
						headers = Headers.empty.withBasicAuthorization(clientId, clientSecret),
						body = Some(StringBody.urlEncodedForm(Model(Vector(
							"grant_type" -> "authorization_code", "code" -> code, "redirect_uri" -> redirectUri))))))
						// Checks the response status and attempts to parse / process tokens
						.tryMapIfSuccess { response =>
							if (response.isSuccess)
							{
								response.body("refresh_token").string match
								{
									case Some(refreshToken) =>
										connectionPool.tryWith { implicit connection =>
											val insertedRefreshToken = ZoomRefreshTokenModel.insert(
												ZoomRefreshTokenData(userId, refreshToken,
													response.body("scope").getString.split(':').toVector))
											response.body("access_token").string.foreach { sessionToken =>
												val sessionExpiration = response.body("expires_id").int match
												{
													case Some(durationSeconds) =>
														requestTime + (durationSeconds - 10).seconds
													case None => requestTime + 1.hours - 10.seconds
												}
												ZoomSessionTokenModel.insert(ZoomSessionTokenData(
													insertedRefreshToken.id, sessionToken,
													expiration = sessionExpiration))
											}
										}
									case None => Failure(new NoSuchElementException(
										s"No 'refresh_token' parameter in zoom auth response body. Available keys: [${
											response.body.attributeNames.mkString(", ")}]"))
								}
							}
							else
								Failure(new RequestFailedException(s"Zoom auth server responded with status ${
									response.status} and body ${response.body.toJson}"))
						}
				}
			}
		}.getOrMap { error => Future.successful(Failure(error)) }
	}
	
	private def closeAuthAttempt(attemptId: Int) =
	{
		connectionPool.tryWith { implicit connection => DbZoomAuthAttempt(attemptId).close() }.failure
			.foreach { error =>
				Log.withoutConnection("Zoom.login.response.close", error = Some(error))
			}
	}
}
