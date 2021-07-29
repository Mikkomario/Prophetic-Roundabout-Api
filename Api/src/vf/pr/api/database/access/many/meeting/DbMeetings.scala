package vf.pr.api.database.access.many.meeting

import utopia.citadel.database.Tables
import utopia.citadel.database.model.organization.MembershipModel
import utopia.flow.generic.ValueConversions._
import utopia.flow.time.Now
import utopia.flow.time.TimeExtensions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.sql.{Condition, Select, Where}
import utopia.vault.sql.SqlExtensions._
import vf.pr.api.database.factory.meeting.MeetingFactory
import vf.pr.api.database.model.meeting.MeetingModel
import vf.pr.api.model.stored.meeting.Meeting

/**
 * Used for accessing multiple Roundabout meetings at a time
 * @author Mikko Hilpinen
 * @since 25.6.2021, v0.1
 */
object DbMeetings extends ManyRowModelAccess[Meeting]
{
	// COMPUTED   -----------------------------
	
	private def model = MeetingModel
	
	private def membershipModel = MembershipModel
	
	
	// IMPLEMENTED  ---------------------------
	
	override def factory = MeetingFactory
	
	override protected def defaultOrdering = Some(model.defaultOrder)
	
	override def globalCondition = None
	
	
	// OTHER    -------------------------------
	
	/**
	 * @return An access point to upcoming and recently (within 4 hours) started meetings
	 */
	def upcomingAndRecent = new DbMeetingsDuring(model.startTimeColumn > Now - 4.hours)
	
	
	// NESTED   -------------------------------
	
	class DbMeetingsDuring(timeCondition: Condition) extends ManyRowModelAccess[Meeting]
	{
		// IMPLEMENTED  -----------------------
		
		override def factory = DbMeetings.factory
		
		override protected def defaultOrdering = DbMeetings.defaultOrdering
		
		override def globalCondition = Some(timeCondition)
		
		
		// OTHER    ---------------------------
		
		/**
		 * @param userId     A user id
		 * @param connection Implicit DB connection
		 * @return All meetings in the organizations in which the user is a member
		 */
		def forUserWithId(userId: Int)(implicit connection: Connection) =
		{
			// Has to join to organization -> membership to acquire a link to user
			factory(connection(Select(target.join(Tables.organization).join(membershipModel.table), table) +
				Where(mergeCondition(membershipModel.withUserId(userId)))))
		}
	}
}
