package vf.pr.api.database.access.single.meeting

import utopia.vault.database.Connection
import utopia.vault.nosql.access.SingleRowModelAccess
import vf.pr.api.database.factory.meeting.MeetingFactory
import vf.pr.api.database.model.meeting.{MeetingModel, MeetingStartUrlModel}
import vf.pr.api.model.partial.meeting.{MeetingData, MeetingStartUrlData}
import vf.pr.api.model.stored.meeting.Meeting

/**
 * Used for accessing individual Roundabout meetings in the DB
 * @author Mikko Hilpinen
 * @since 25.6.2021, v0.1
 */
object DbMeeting extends SingleRowModelAccess[Meeting]
{
	// COMPUTED -----------------------------------
	
	private def model = MeetingModel
	
	private def startUrlModel = MeetingStartUrlModel
	
	
	// IMPLEMENTED  -------------------------------
	
	override def factory = MeetingFactory
	
	override def globalCondition = None
	
	
	// OTHER    -----------------------------------
	
	/**
	 * Schedules a new meeting
	 * @param data Meeting data
	 * @param startUrl Meeting start url
	 * @param connection DB Connection (implicit)
	 * @return Newly inserted meeting
	 */
	def schedule(data: MeetingData, startUrl: String)(implicit connection: Connection) =
	{
		val meeting = model.insert(data)
		startUrlModel.insert(MeetingStartUrlData(meeting.id, startUrl))
		meeting
	}
}
