package vf.pr.api.model.stored.meeting

import utopia.vault.model.template.Stored
import vf.pr.api.model.partial.meeting.MeetingData

/**
 * Represents a recorded Roundabout (zoom) meeting
 * @author Mikko Hilpinen
 * @since 24.6.2021, v0.1
 */
case class Meeting(id: Int, data: MeetingData) extends Stored[MeetingData, Int]
