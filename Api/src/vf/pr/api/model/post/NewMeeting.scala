package vf.pr.api.model.post

import utopia.flow.datastructure.immutable.{Constant, Model, ModelDeclaration}
import utopia.flow.generic.{FromModelFactoryWithSchema, LocalDateTimeType, StringType}
import utopia.flow.generic.ValueUnwraps._
import utopia.flow.time.TimeExtensions._

import java.time.LocalDateTime
import scala.concurrent.duration.FiniteDuration

object NewMeeting extends FromModelFactoryWithSchema[NewMeeting]
{
	override val schema = ModelDeclaration("start_time_local" -> LocalDateTimeType, "name" -> StringType)
	
	override protected def fromValidatedModel(model: Model[Constant]) = NewMeeting(model("name"),
		model("start_time_local"), model("duration_minutes").int.filter { _ > 0 }.map { _.minutes },
		model("password"), model("time_zone_id"))
}

/**
 * Data sent from the client side when posting / scheduling new meetings
 * @author Mikko Hilpinen
 * @since 24.6.2021, v0.1
 * @param name Meeting name / topic
 * @param localStartTime Start time of this meeting in the host's local time zone (or the specified time zone)
 * @param estimatedDuration Estimated meeting duration (optional)
 * @param password Meeting password. None if the password should be generated automatically.
 * @param timeZoneId Id of the time zone used in the start time. None if the host's local time zone should be used.
 */
case class NewMeeting(name: String, localStartTime: LocalDateTime, estimatedDuration: Option[FiniteDuration] = None,
                      password: Option[String] = None, timeZoneId: Option[String] = None)
