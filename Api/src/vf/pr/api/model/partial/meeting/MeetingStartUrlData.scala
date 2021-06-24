package vf.pr.api.model.partial.meeting

import utopia.flow.time.Now
import utopia.flow.time.TimeExtensions._

import java.time.Instant

/**
 * Contains basic information about a meeting start url (for the host)
 * @author Mikko Hilpinen
 * @since 24.6.2021, v0.1
 * @param meetingId Id of the meeting this start url works for
 * @param url This start url
 * @param created Creation time of this url (default = Now)
 * @param expiration Expiration time of this url (default = after 2 hours)
 */
case class MeetingStartUrlData(meetingId: Int, url: String, created: Instant = Now, expiration: Instant = Now + 2.hours)
