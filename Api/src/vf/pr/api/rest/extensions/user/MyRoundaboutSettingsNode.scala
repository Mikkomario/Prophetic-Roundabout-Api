package vf.pr.api.rest.extensions.user

import utopia.access.http.Method.{Get, Patch, Put}
import utopia.access.http.Status.NotFound
import utopia.exodus.database.access.single.DbUser
import utopia.exodus.rest.util.AuthorizedContext
import utopia.flow.generic.ValueConversions._
import utopia.nexus.http.Path
import utopia.nexus.rest.LeafResource
import utopia.nexus.result.Result
import utopia.vault.database.Connection
import vf.pr.api.database.ExodusDbExtensions._
import vf.pr.api.model.post.NewRoundaboutUserSettings

/**
 * Used for accessing the authorized user's Roundabout-specific settings
 * @author Mikko Hilpinen
 * @since 18.6.2021, v0.1
 */
object MyRoundaboutSettingsNode extends LeafResource[AuthorizedContext]
{
	override val name = "roundabout"
	
	override val allowedMethods = Vector(Get, Put, Patch)
	
	override def toResponse(remainingPath: Option[Path])(implicit context: AuthorizedContext) =
	{
		context.sessionKeyAuthorized { (session, connection) =>
			implicit val c: Connection = connection
			val userId = session.userId
			val method = context.request.method
			val settingsAccess = DbUser(userId).roundaboutSettings
			
			// Case: Get => Retrieves settings as a model
			if (method == Get)
				settingsAccess.pull match
				{
					case Some(settings) => Result.Success(settings.toModel)
					case None => Result.Failure(NotFound, "No settings have been specified at this time")
				}
			// Case: Put => Overwrites settings
			else if (method == Put)
				context.handlePost(NewRoundaboutUserSettings.fullUpdateFactory) { settings =>
					val inserted = settingsAccess.update(settings.timeZoneId,
						settings.ownsProZoomAccount.getOrElse(false))
					Result.Success(inserted.toModel)
				}
			// Case: Patch => Updates settings where appropriate
			else
				context.handlePost(NewRoundaboutUserSettings) { update =>
					settingsAccess.pull match
					{
						case Some(previous) =>
							// Case: Changes to existing settings => Applies changes and returns updated version
							if (update.wouldUpdate(previous))
							{
								val inserted = settingsAccess.update(
									update.timeZoneId.orElse(previous.timeZoneId),
									update.ownsProZoomAccount.getOrElse(previous.ownsProZoomAccount))
								Result.Success(inserted.toModel)
							}
							// Case: No changes => returns existing version
							else
								Result.Success(previous.toModel)
						// Case: No existing settings => creates a new instance based on update
						case None =>
							val inserted = settingsAccess.update(update.timeZoneId,
								update.ownsProZoomAccount.getOrElse(false))
							Result.Success(inserted.toModel)
					}
				}
		}
	}
}
