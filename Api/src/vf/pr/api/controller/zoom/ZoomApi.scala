package vf.pr.api.controller.zoom

import utopia.access.http.{Headers, Method}
import utopia.access.http.Method.{Get, Post}
import utopia.citadel.database.access.single.DbUser
import utopia.disciple.http.request.{Request, StringBody}
import utopia.flow.async.AsyncExtensions.{FutureTry, _}
import utopia.flow.datastructure.immutable.{Model, ModelDeclaration}
import utopia.flow.generic.StringType
import utopia.flow.generic.ValueConversions._
import utopia.flow.parse.JsonConvertible
import utopia.flow.time.Now
import utopia.flow.time.TimeExtensions._
import utopia.flow.util.CollectionExtensions._
import utopia.vault.database.Connection
import vf.pr.api.database.access.single.setting.ZoomSettings
import vf.pr.api.database.ExodusDbExtensions._
import vf.pr.api.database.access.single.zoom.DbZoomRefreshToken
import vf.pr.api.database.model.zoom.ZoomRefreshTokenModel
import vf.pr.api.model.error.{RequestFailedException, UnauthorizedException}
import vf.pr.api.model.partial.zoom.ZoomRefreshTokenData
import vf.pr.api.model.stored.zoom.ZoomRefreshToken
import vf.pr.api.util.Globals._
import vf.pr.api.util.Log

import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
 * Used for making requests to the Zoom API
 * @author Mikko Hilpinen
 * @since 25.6.2021, v0.1
 */
object ZoomApi
{
	// Schema for get meeting responses
	private lazy val meetingSchema = ModelDeclaration("uuid" -> StringType, "start_url" -> StringType)
	
	/**
	 * Performs a get request to the zoom api
	 * @param path Path to the targeted resource
	 * @param userId Id of the authenticated user
	 * @param responseSchema A schema the response must fulfill in order to be accepted (default = empty)
	 * @param connection DB Connection (implicit)
	 * @return Successful response body as a future. Contains failure if something
	 *         (settings read, authentication, response status or response parsing) failed.
	 */
	def get(path: String, userId: Int, responseSchema: ModelDeclaration = ModelDeclaration.empty)
	       (implicit connection: Connection) =
		makeRequest(userId, path, responseSchema = responseSchema)
	
	/**
	 * Performs a post / put / patch request to the zoom api
	 * @param path Path to the targeted resource
	 * @param userId Id of the authenticated user
	 * @param body Request post body
	 * @param responseSchema A schema the response must fulfill in order to be accepted (default = empty)
	 * @param method Method used (default = Post)
	 * @param connection DB Connection (implicit)
	 * @return Successful response body as a future. Contains failure if something
	 *         (settings read, authentication, response status or response parsing) failed.
	 */
	def push(path: String, userId: Int, body: JsonConvertible,
	         responseSchema: ModelDeclaration = ModelDeclaration.empty,  method: Method = Post)
	        (implicit connection: Connection) =
		makeRequest(userId, path, method, Some(body), responseSchema)
	
	/**
	 * Performs a request to the zoom api
	 * @param userId Id of the authenticated user
	 * @param path Path to the targeted resource
	 * @param method Method used (default = Get)
	 * @param body Request post body (optional)
	 * @param responseSchema A schema the response must fulfill in order to be accepted (default = empty)
	 * @param connection DB Connection (implicit)
	 * @return Successful response body as a future. Contains failure if something
	 *         (settings read, authentication, response status or response parsing) failed.
	 */
	def makeRequest(userId: Int, path: String, method: Method = Get, body: Option[JsonConvertible] = None,
	                responseSchema: ModelDeclaration = ModelDeclaration.empty)
	               (implicit connection: Connection) =
	{
		// Makes sure required settings are found
		ZoomSettings.apiBaseUri.map { baseUri =>
			// Acquires a session token
			sessionTokenForUserWithId(userId).tryFlatMapIfSuccess { sessionToken =>
				// Performs the actual request
				zoomGateway.valueResponseFor(Request(baseUri + path, method,
					headers = Headers().withBearerAuthorization(sessionToken),
					body = body.map { b => StringBody.json(b.toJson) }))
					// Converts the response into a Try[Model]
					.tryMapIfSuccess { response =>
						if (response.isSuccess)
							response.body.model
								.toTry { new RequestFailedException(
									s"Expected a json object body, received: ${response.body.getString}") }
								// Validates the response model using the specified schema
								.flatMap { responseSchema.validate(_).toTry }
						else
							Failure(new RequestFailedException(s"Zoom api responded to $method /$path with ${
								response.status}: ${response.body.getString}"))
					}
			}
		}.flattenToFuture
	}
	
	/**
	 * Retrieves meeting UUID and start url
	 * @param userId Authenticated user id
	 * @param meetingZoomId Zoom meeting id
	 * @param connection DB Connection (implicit)
	 * @return Future containing meeting uuid and start url. May contain a failure.
	 */
	def getMeeting(userId: Int, meetingZoomId: Long)(implicit connection: Connection) =
		get(s"meetings/$meetingZoomId", userId, meetingSchema).mapIfSuccess { response =>
			response("uuid").getString -> response("start_url").getString
		}
	
	private def sessionTokenForUserWithId(userId: Int)(implicit connection: Connection) =
	{
		DbUser(userId).zoomSessionToken match
		{
			// Case: Active session token found from DB => Uses that
			case Some(sessionToken) => Future.successful(Success(sessionToken.value))
			case None =>
				DbUser(userId).zoomRefreshToken.pull match
				{
					// Case: No session token available, but refresh token is => acquires a new session token
					case Some(refreshToken) => requestSessionToken(refreshToken)
					// Case: No zoom authorization enabled => fails
					case None => Future.successful(Failure(
						new UnauthorizedException("Zoom features haven't been authorized / enabled yet")))
				}
		}
	}
	
	private def requestSessionToken(refreshToken: ZoomRefreshToken) =
	{
		ZoomSettings.tokenUri.flatMap { tokenUri =>
			ZoomSettings.clientIdAndSecret.map { case (clientId, clientSecret) =>
				val requestTime = Now.toInstant
				zoomGateway.valueResponseFor(Request(tokenUri, Post,
					headers = Headers.empty.withBasicAuthorization(clientId, clientSecret),
					body = Some(StringBody.urlEncodedForm(Model(Vector(
						"grant_type" -> "refresh_token", "refresh_token" -> refreshToken.value))))))
					.tryMapIfSuccess { response =>
						if (response.isSuccess)
						{
							val body = response.body.getModel
							connectionPool.tryWith { implicit connection =>
								// Checks whether a new refresh token should be updated to the db
								val newRefreshToken = body("refresh_token").string match
								{
									case Some(token) =>
										// Case: Refresh token didn't change
										if (token == refreshToken.value)
											refreshToken
										// Case: Refresh token changed => inserts a new token to DB
										else
										{
											DbZoomRefreshToken(refreshToken.id).deprecate()
											ZoomRefreshTokenModel.insert(
												ZoomRefreshTokenData(refreshToken.userId, token))
										}
									case None =>
										Log.warning(s"No 'refresh_token' property in Zoom (refresh) auth response. Available properties: [${
											body.attributeNames.mkString(", ")}]")
										refreshToken
								}
								
								// Handles the access token next
								body("access_token").string match
								{
									// Case: Successful response with a token
									case Some(token) =>
										// Saves the token to the DB and then returns it
										val expiration = requestTime + (response.body("expires_in").int match
										{
											case Some(expirationSeconds) => expirationSeconds.seconds - 5.minutes
											case None => 55.minutes
										})
										Success(DbZoomRefreshToken(newRefreshToken.id)
											.startSession(token, expiration).value)
									// Case: Successful response without a token => failure
									case None => Failure(new RequestFailedException(
										s"Couldn't find session token from the successful (${
											response.status}) Zoom auth response body: ${
											response.body.getString}"))
								}
							}.flatten
						}
						else
							Failure(new RequestFailedException(s"Zoom authentication request was met with ${
								response.status} response: ${response.body.getString}"))
					}
			}
		}.flattenToFuture
	}
}
