package vf.pr.api.rest.extensions.user

import utopia.access.http.Method.Get
import utopia.exodus.database.access.single.DbUser
import utopia.exodus.rest.util.AuthorizedContext
import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ValueConversions._
import utopia.flow.util.CollectionExtensions._
import utopia.nexus.http.Path
import utopia.nexus.rest.LeafResource
import utopia.nexus.result.Result
import utopia.vault.database.Connection
import vf.pr.api.database.ExodusDbExtensions._

/**
 * A rest node used for accessing the authorized user's upcoming and current meetings
 * @author Mikko Hilpinen
 * @since 25.6.2021, v0.1
 */
object MyMeetingsNode extends LeafResource[AuthorizedContext]
{
	// ATTRIBUTES   ---------------------------
	
	override val name = "meetings"
	
	override val allowedMethods = Vector(Get)
	
	
	// IMPLEMENTED  ---------------------------
	
	override def toResponse(remainingPath: Option[Path])(implicit context: AuthorizedContext) =
	{
		context.sessionKeyAuthorized { (session, connection) =>
			implicit val c: Connection = connection
			val userId = session.userId
			// Reads the upcoming meetings and groups them based on whether the user is hosting them or not
			val (otherMeetings, hostedMeetings) = DbUser(userId).upcomingAndRecentMeetings
				.divideBy { _.hostId == userId }
			// Forms a response based on the meetings
			// Adds local meeting start time to the results
			val timeZoneId = DbUser(userId).roundaboutSettings.timeZoneId
			Result.Success(Model(Vector(
				"hosting" -> hostedMeetings.map { _.toModelWith(timeZoneId) },
				"other" -> otherMeetings.map { _.toModelWith(timeZoneId) })))
		}
	}
}
