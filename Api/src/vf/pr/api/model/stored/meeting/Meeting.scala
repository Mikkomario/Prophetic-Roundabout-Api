package vf.pr.api.model.stored.meeting

import utopia.flow.datastructure.immutable.Constant
import utopia.flow.generic.ValueConversions._
import utopia.metropolis.model.stored.StoredModelConvertible
import vf.pr.api.model.partial.meeting.MeetingData

import java.time.ZoneId

/**
 * Represents a recorded Roundabout (zoom) meeting
 * @author Mikko Hilpinen
 * @since 24.6.2021, v0.1
 */
case class Meeting(id: Int, data: MeetingData) extends StoredModelConvertible[MeetingData]
{
	/**
	 * @param timeZoneId A time zone id
	 * @return A model based on this meeting, including start_time_local -property
	 */
	def toModelWithLocalTime(timeZoneId: ZoneId) = toModel +
		Constant("start_time_local", data.startTime.atZone(timeZoneId).toLocalDateTime.toString)
	
	/**
	 * @param timeZoneId A time zone id (optional)
	 * @return A model based on this meeting, including start_time_local -property if the time zone id was specified
	 */
	def toModelWith(timeZoneId: Option[ZoneId]) = timeZoneId match
	{
		case Some(zoneId) => toModelWithLocalTime(zoneId)
		case None => toModel
	}
}