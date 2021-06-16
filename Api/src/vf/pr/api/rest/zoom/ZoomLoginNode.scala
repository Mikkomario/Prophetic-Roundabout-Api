package vf.pr.api.rest.zoom

import utopia.access.http.Method.Get
import utopia.access.http.Status.InternalServerError
import utopia.exodus.rest.util.AuthorizedContext
import utopia.exodus.util.ExodusContext.uuidGenerator
import utopia.flow.util.CollectionExtensions._
import utopia.nexus.http.Path
import utopia.nexus.rest.{Context, LeafResource, ResourceWithChildren}
import utopia.nexus.result.Result
import utopia.vault.database.Connection
import vf.pr.api.database.access.single.setting.ZoomSettings
import vf.pr.api.database.model.zoom.ZoomAuthAttemptModel
import vf.pr.api.model.partial.zoom.ZoomAuthAttemptData
import vf.pr.api.util.Log

/**
 * Used for performing the first time login to Zoom
 * @author Mikko Hilpinen
 * @since 16.6.2021, v0.1
 */
object ZoomLoginNode extends ResourceWithChildren[AuthorizedContext]
{
	// IMPLEMENTED  ---------------------------------
	
	// TODO: Add authentication redirect node here
	override def children = Vector()
	
	override def name = "login"
	
	override def allowedMethods = Vector(Get)
	
	override def toResponse(remainingPath: Option[Path])(implicit context: AuthorizedContext) =
	{
		// Uses session auth
		context.sessionKeyAuthorized { (session, connection) =>
			implicit val c: Connection = connection
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
	
	
	// NESTED   ----------------------------------
	
	object ZoomLoginResponseNode extends LeafResource[Context]
	{
		override def name = "response"
		
		override def allowedMethods = Vector(Get)
		
		override def toResponse(remainingPath: Option[Path])(implicit context: Context) =
		{
			// TODO: Check code & state, acquire session & refresh tokens, then redirect
			???
		}
	}
}
