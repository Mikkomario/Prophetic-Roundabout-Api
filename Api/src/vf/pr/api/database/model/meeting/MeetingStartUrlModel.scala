package vf.pr.api.database.model.meeting

import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.model.template.DataInserter
import vf.pr.api.database.factory.meeting.MeetingStartUrlFactory
import vf.pr.api.model.partial.meeting.MeetingStartUrlData
import vf.pr.api.model.stored.meeting.MeetingStartUrl

import java.time.Instant

object MeetingStartUrlModel extends DataInserter[MeetingStartUrlModel, MeetingStartUrl, MeetingStartUrlData]
{
	/**
	 * @return The factory used by this model
	 */
	def factory = MeetingStartUrlFactory
	
	override def table = factory.table
	
	override def apply(data: MeetingStartUrlData) = apply(None, Some(data.meetingId),
		Some(data.url), Some(data.created), Some(data.expiration))
	
	override protected def complete(id: Value, data: MeetingStartUrlData) = MeetingStartUrl(id.getInt, data)
}

/**
 * Used for interacting with meeting start urls in the DB
 * @author Mikko Hilpinen
 * @since 24.6.2021, v0.1
 */
case class MeetingStartUrlModel(id: Option[Int] = None, meetingId: Option[Int] = None, url: Option[String] = None,
                                created: Option[Instant] = None, expiration: Option[Instant] = None)
	extends StorableWithFactory[MeetingStartUrl]
{
	override def factory = MeetingStartUrlModel.factory
	
	override def valueProperties = Vector("id" -> id, "meetingId" -> meetingId, "url" -> url, "created" -> created,
		"expiration" -> expiration)
}
