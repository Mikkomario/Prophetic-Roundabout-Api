package vf.pr.api.model.post

import utopia.flow.datastructure.immutable.ModelDeclaration
import utopia.flow.datastructure.template
import utopia.flow.datastructure.template.Property
import utopia.flow.generic.{FromModelFactory, StringType}
import utopia.flow.generic.ValueUnwraps._
import utopia.flow.time.TimeExtensions._
import utopia.metropolis.model.error.IllegalPostModelException

import java.time.{Instant, LocalDateTime}
import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}

object NewMeeting extends FromModelFactory[NewMeeting]
{
	private val schema = ModelDeclaration("name" -> StringType)
	
	override def apply(model: template.Model[Property]) = schema.validate(model).toTry
		.flatMap { valid =>
			valid("start_time").instant.map { Right(_) }
				.orElse { valid("start_time_local").localDateTime.map { Left(_) } } match
			{
				case Some(time) =>
					Success(NewMeeting(model("name"), time,
						model("duration_minutes").int.filter { _ > 0 }.map { _.minutes },
						model("password"), model("time_zone_id")))
				case None => Failure(new IllegalPostModelException(
					"Either 'start_time' or 'start_time_local' is required"))
			}
		}
}

/**
 * Data sent from the client side when posting / scheduling new meetings
 * @author Mikko Hilpinen
 * @since 24.6.2021, v0.1
 * @param name Meeting name / topic
 * @param startTime Either Left) local meeting start time or Right) UTC meeting start time (instant)
 * @param estimatedDuration Estimated meeting duration (optional)
 * @param password Meeting password. None if the password should be generated automatically.
 * @param timeZoneId Id of the time zone used in the start time. None if the host's local time zone should be used.
 */
case class NewMeeting(name: String, startTime: Either[LocalDateTime, Instant],
                      estimatedDuration: Option[FiniteDuration] = None,
                      password: Option[String] = None, timeZoneId: Option[String] = None)
