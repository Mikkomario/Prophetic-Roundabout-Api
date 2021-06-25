package vf.pr.api.database.model.meeting

import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.model.template.DataInserter
import utopia.vault.sql.OrderBy
import vf.pr.api.database.factory.meeting.MeetingFactory
import vf.pr.api.model.partial.meeting.MeetingData
import vf.pr.api.model.stored.meeting.Meeting

import java.time.Instant
import scala.concurrent.duration.FiniteDuration

object MeetingModel extends DataInserter[MeetingModel, Meeting, MeetingData]
{
	// ATTRIBUTES   ----------------------------
	
	/**
	 * Name of the property that contains meeting start time
	 */
	val startTimeAttName = "startTime"
	
	/**
	 * Default ordering to use in this table (based on meeting start time, from earliest to latest)
	 */
	lazy val defaultOrder = OrderBy.ascending(startTimeColumn)
	
	
	// COMPUTED --------------------------------
	
	/**
	 * @return The factory used by this model
	 */
	def factory = MeetingFactory
	
	/**
	 * @return Column that contains meeting start time
	 */
	def startTimeColumn = table(startTimeAttName)
	
	
	// IMPLEMENTED  ---------------------------
	
	override def table = factory.table
	
	override def apply(data: MeetingData) = apply(None, Some(data.zoomId), Some(data.zoomUuid),
		Some(data.hostId), Some(data.hostOrganizationId), Some(data.name), Some(data.startTime),
		Some(data.plannedDuration), Some(data.password), Some(data.joinUrl), Some(data.created))
	
	override protected def complete(id: Value, data: MeetingData) = Meeting(id.getInt, data)
}

/**
 * Used for interacting with Roundabout meetings in the DB
 * @author Mikko Hilpinen
 * @since 24.6.2021, v0.1
 */
case class MeetingModel(id: Option[Int] = None, zoomId: Option[Long] = None, zoomUuid: Option[String] = None,
                        hostId: Option[Int] = None, hostOrganizationId: Option[Int] = None,
                        name: Option[String] = None, startTime: Option[Instant] = None,
                        plannedDuration: Option[FiniteDuration] = None, password: Option[String] = None,
                        joinUrl: Option[String] = None, created: Option[Instant] = None)
	extends StorableWithFactory[Meeting]
{
	import MeetingModel._
	
	override def factory = MeetingModel.factory
	
	override def valueProperties = Vector("id" -> id, "zoomId" -> zoomId, "zoomUuid" -> zoomUuid, "hostId" -> hostId,
		"hostOrganizationId" -> hostOrganizationId, "name" -> name, startTimeAttName -> startTime,
		"plannedDurationMinutes" -> plannedDuration.map { _.toMinutes }, "password" -> password, "joinUrl" -> joinUrl,
		"created" -> created)
}
