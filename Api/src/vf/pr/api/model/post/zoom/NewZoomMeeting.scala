package vf.pr.api.model.post.zoom

import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ModelConvertible
import utopia.flow.generic.ValueConversions._

import java.time.Instant
import scala.concurrent.duration.FiniteDuration

/**
 * Used for posting new meetings to the zoom api
 * @author Mikko Hilpinen
 * @since 25.6.2021, v0.1
 * @param name Meeting topic / name
 * @param startTime Meeting start time
 * @param duration Planned meeting duration
 * @param password Meeting password
 */
case class NewZoomMeeting(name: String, startTime: Instant, duration: FiniteDuration, password: String)
	extends ModelConvertible
{
	override def toModel = Model(Vector(
		"topic" -> name, "start_time" -> startTime.toString, "duration" -> duration.toMinutes,
		"password" -> password,
		"settings" -> Model(Vector(
			"host_video" -> true, "participant_video" -> true, "join_before_host" -> true,
			"audio" -> "voip", "waiting_room" -> false))))
}
