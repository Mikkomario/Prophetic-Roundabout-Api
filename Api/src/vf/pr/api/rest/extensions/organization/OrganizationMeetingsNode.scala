package vf.pr.api.rest.extensions.organization

import utopia.access.http.Method.Post
import utopia.access.http.Status.BadRequest
import utopia.exodus.database.access.single.DbUser
import utopia.exodus.rest.util.AuthorizedContext
import utopia.nexus.http.Path
import utopia.nexus.rest.LeafResource
import utopia.nexus.result.Result
import utopia.vault.database.Connection
import vf.pr.api.model.enumeration.RoundaboutTask.HostMeeting
import vf.pr.api.model.post.NewMeeting
import vf.pr.api.database.ExodusDbExtensions._

import java.time.ZoneId
import scala.util.Try

/**
 * Used for accessing an organization's scheduled meetings
 * @author Mikko Hilpinen
 * @since 24.6.2021, v0.1
 */
case class OrganizationMeetingsNode(organizationId: Int) extends LeafResource[AuthorizedContext]
{
	// IMPLEMENTED  ------------------------------
	
	override def name = "meetings"
	
	override def allowedMethods = Vector(Post)
	
	override def toResponse(remainingPath: Option[Path])(implicit context: AuthorizedContext) =
	{
		context.authorizedForTask(organizationId, HostMeeting.id) { (session, _, connection) =>
			context.handlePost(NewMeeting) { newMeeting =>
				implicit val c: Connection = connection
				val userId = session.userId
				
				// Parses the meeting time
				newMeeting.timeZoneId.flatMap { id => Try { ZoneId.of(id) }.toOption }
					.orElse { DbUser(userId).roundaboutSettings.timeZoneId } match
				{
					case Some(timeZoneId) =>
						val meetingTime = newMeeting.localStartTime.atZone(timeZoneId).toInstant
						
						// TODO: Schedules a meeting in the Zoom
						???
					case None => Result.Failure(BadRequest,
						"A valid time_zone_id must be provided in this request or in the user settings")
				}
			}
		}
	}
}
