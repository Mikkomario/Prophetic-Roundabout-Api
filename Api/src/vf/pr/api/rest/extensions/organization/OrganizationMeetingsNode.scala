package vf.pr.api.rest.extensions.organization

import utopia.access.http.Method.Post
import utopia.access.http.Status.{Created, Forbidden, InternalServerError, Unauthorized}
import utopia.ambassador.database.AuthDbExtensions._
import utopia.citadel.database.access.single.DbUser
import utopia.exodus.rest.util.AuthorizedContext
import utopia.flow.async.AsyncExtensions._
import utopia.flow.datastructure.immutable.{Constant, Model, ModelDeclaration}
import utopia.flow.generic.{LongType, StringType}
import utopia.flow.generic.ValueConversions._
import utopia.flow.time.TimeExtensions._
import utopia.nexus.http.Path
import utopia.nexus.rest.LeafResource
import utopia.nexus.result.Result
import utopia.vault.database.Connection
import vf.pr.api.controller.zoom.ZoomApi
import vf.pr.api.database.ExodusDbExtensions._
import vf.pr.api.database.access.single.meeting.DbMeeting
import vf.pr.api.database.access.single.setting.ZoomSettings
import vf.pr.api.model.enumeration.RoundaboutTask.HostMeeting
import vf.pr.api.model.error.UnauthorizedException
import vf.pr.api.model.partial.meeting.MeetingData
import vf.pr.api.model.post.NewMeeting
import vf.pr.api.model.post.zoom.NewZoomMeeting
import vf.pr.api.util.Globals._
import vf.pr.api.util.Log

import java.time.ZoneId
import scala.util.{Failure, Random, Success, Try}

object OrganizationMeetingsNode
{
	// Schema used for validating post meeting responses
	private lazy val postMeetingResponseSchema = ModelDeclaration(
		"id" -> LongType, "join_url" -> StringType
	)
}

/**
 * Used for accessing an organization's scheduled meetings
 * @author Mikko Hilpinen
 * @since 24.6.2021, v0.1
 */
case class OrganizationMeetingsNode(organizationId: Int) extends LeafResource[AuthorizedContext]
{
	import OrganizationMeetingsNode._
	
	// COMPUTED ----------------------------------
	
	private def randomPassword = Iterator.continually { Random.nextInt(10) }.take(5).mkString
	
	
	// IMPLEMENTED  ------------------------------
	
	override def name = "meetings"
	
	override def allowedMethods = Vector(Post)
	
	override def toResponse(remainingPath: Option[Path])(implicit context: AuthorizedContext) =
	{
		context.authorizedForTask(organizationId, HostMeeting.id) { (session, _, connection) =>
			implicit val c: Connection = connection
			val userId = session.userId
			
			// Makes sure the user is also authorized to perform the task when it comes to Google and Zoom
			if (DbUser(userId).isAuthorizedForTaskWithId(HostMeeting.id))
				context.handlePost(NewMeeting) { newMeeting =>
					// Parses the meeting time
					val timeZoneId = newMeeting.timeZoneId.flatMap { id => Try { ZoneId.of(id) }.toOption }
						.orElse { DbUser(userId).roundaboutSettings.timeZoneId }
					val meetingTime = newMeeting.startTime match
					{
						case Right(time) => time
						case Left(localTime) => localTime.atZone(timeZoneId.getOrElse(ZoneId.of("Z"))).toInstant
					}
					val password = newMeeting.password.filter { _.nonEmpty }.getOrElse { randomPassword }
					val duration = newMeeting.estimatedDuration.getOrElse { 3.hours + 30.minutes }
					
					// Schedules a meeting in the Zoom
					val zoomMeeting = NewZoomMeeting(newMeeting.name, meetingTime, duration, password)
					ZoomApi.push("users/me/meetings", userId, HostMeeting.id, zoomMeeting,
						postMeetingResponseSchema)
						.tryFlatMapIfSuccess { processZoomResponse(_, zoomMeeting, userId, organizationId) }
						// The maximum wait time (for user) is limited
						.waitForResult() match
					{
						case Success(meeting) => Result.Success(meeting.toModelWith(timeZoneId), Created)
						case Failure(error) =>
							// Logs failures
							Log.error("Rest.meetings.post", error)
							error match {
								case authError: UnauthorizedException =>
									Result.Failure(Unauthorized, authError.getMessage)
								case e: Throwable => Result.Failure(InternalServerError, e.getMessage)
							}
					}
				}
			else
				Result.Failure(Forbidden, "3rd party authentication is required first")
		}
	}
	
	
	// OTHER    -----------------------------------
	
	private def processZoomResponse(response: Model[Constant], meeting: NewZoomMeeting, userId: Int,
	                                organizationId: Int) =
	{
		// Reads required data from the response
		val zoomId = response("id").getLong
		val joinUrl = response("join_url").getString
		
		// Acquires additional data
		connectionPool.tryWith { implicit connection =>
			ZoomApi.getMeeting(userId, zoomId)
		}.flattenToFuture.tryMapIfSuccess { case (uuid, startUrl) =>
			// Saves the meeting to DB
			connectionPool.tryWith { implicit connection =>
				DbMeeting.schedule(MeetingData(zoomId, uuid, userId, organizationId, meeting.name,
					meeting.startTime, meeting.duration, meeting.password, joinUrl), startUrl)
			}
		}
	}
}
