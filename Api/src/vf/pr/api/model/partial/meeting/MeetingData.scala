package vf.pr.api.model.partial.meeting

import utopia.flow.time.Now

import java.time.Instant
import scala.concurrent.duration.FiniteDuration

/**
 * Contains basic information about a Prophetic Roundabout meeting
 * @author Mikko Hilpinen
 * @since 24.6.2021, v0.1
 * @param zoomId Zoom-originated id for this meeting
 * @param zoomUuid Zoom-originated UUID for this meeting
 * @param hostId Id of the host (user) of this meeting
 * @param hostOrganizationId Id of the organization (Roundabout) that "owns" this meeting
 * @param name Topic / Name of this meeting
 * @param startTime Time when this meeting is scheduled to start
 * @param plannedDuration Estimated duration of this meeting
 * @param password Password used for authenticating access to this meeting
 * @param joinUrl Url the participants use to join this meeting
 * @param created Creation time of this meeting instance (default = Now)
 */
case class MeetingData(zoomId: Long, zoomUuid: String, hostId: Int, hostOrganizationId: Int, name: String,
                       startTime: Instant, plannedDuration: FiniteDuration, password: String, joinUrl: String,
                       created: Instant = Now)