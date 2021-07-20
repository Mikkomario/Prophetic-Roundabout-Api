package vf.pr.api.controller.zoom

import utopia.access.http.{Headers, Method}
import utopia.access.http.Method.{Get, Post}
import utopia.disciple.http.request.{Request, StringBody}
import utopia.disciple.model.error.RequestFailedException
import utopia.flow.async.AsyncExtensions.{FutureTry, _}
import utopia.flow.datastructure.immutable.ModelDeclaration
import utopia.flow.generic.StringType
import utopia.flow.parse.JsonConvertible
import utopia.flow.util.CollectionExtensions._
import utopia.vault.database.Connection
import vf.pr.api.database.access.single.setting.ZoomSettings
import vf.pr.api.model.enumeration.RoundaboutTask.HostMeeting
import vf.pr.api.model.enumeration.Service.Zoom
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
	private lazy val service = Zoom
	
	// Schema for get meeting responses
	private lazy val meetingSchema = ModelDeclaration("uuid" -> StringType, "start_url" -> StringType)
	
	/**
	 * Performs a get request to the zoom api
	 * @param path Path to the targeted resource
	 * @param userId Id of the authenticated user
	 * @param taskId Id of the task that is being performed
	 * @param responseSchema A schema the response must fulfill in order to be accepted (default = empty)
	 * @param connection DB Connection (implicit)
	 * @return Successful response body as a future. Contains failure if something
	 *         (settings read, authentication, response status or response parsing) failed.
	 */
	def get(path: String, userId: Int, taskId: Int, responseSchema: ModelDeclaration = ModelDeclaration.empty)
	       (implicit connection: Connection) =
		makeRequest(userId, path, taskId, responseSchema = responseSchema)
	
	/**
	 * Performs a post / put / patch request to the zoom api
	 * @param path Path to the targeted resource
	 * @param userId Id of the authenticated user
	 * @param taskId Id of the task that is being performed
	 * @param body Request post body
	 * @param responseSchema A schema the response must fulfill in order to be accepted (default = empty)
	 * @param method Method used (default = Post)
	 * @param connection DB Connection (implicit)
	 * @return Successful response body as a future. Contains failure if something
	 *         (settings read, authentication, response status or response parsing) failed.
	 */
	def push(path: String, userId: Int, taskId: Int, body: JsonConvertible,
	         responseSchema: ModelDeclaration = ModelDeclaration.empty,  method: Method = Post)
	        (implicit connection: Connection) =
		makeRequest(userId, path, taskId, method, Some(body), responseSchema)
	
	/**
	 * Performs a request to the zoom api
	 * @param userId Id of the authenticated user
	 * @param path Path to the targeted resource
	 * @param taskId Id of the task that is being performed
	 * @param method Method used (default = Get)
	 * @param body Request post body (optional)
	 * @param responseSchema A schema the response must fulfill in order to be accepted (default = empty)
	 * @param connection DB Connection (implicit)
	 * @return Successful response body as a future. Contains failure if something
	 *         (settings read, authentication, response status or response parsing) failed.
	 */
	def makeRequest(userId: Int, path: String, taskId: Int, method: Method = Get, body: Option[JsonConvertible] = None,
	                responseSchema: ModelDeclaration = ModelDeclaration.empty)
	               (implicit connection: Connection) =
	{
		// Makes sure required settings are found
		ZoomSettings.apiBaseUri.map { baseUri =>
			// Acquires a session token and converts it to an authentication header, if possible
			val authHeadersFuture = acquireTokens.forServiceTask(userId, service.id, taskId) match
			{
				// Case: Authentication required (should be) => Converts the token to a bearer header when/if possible
				case Some(tokenFuture) =>
					tokenFuture.mapIfSuccess { token => Headers.withBearerAuthorization(token.tokenString) }
				// Case: No authentication required (warning) => Skips the authentication header
				case None =>
					Log.warning("Zoom.api.makeRequest.auth", s"No authentication header is required for task $taskId?")
					Future.successful(Success(Headers.empty))
			}
			
			// Performs the actual request when the authentication preparation has completed
			authHeadersFuture.tryFlatMapIfSuccess { authHeaders =>
				service.gateway.valueResponseFor(Request(baseUri + path, method, headers = authHeaders,
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
		get(s"meetings/$meetingZoomId", userId, HostMeeting.id, meetingSchema).mapIfSuccess { response =>
			response("uuid").getString -> response("start_url").getString
		}
}
