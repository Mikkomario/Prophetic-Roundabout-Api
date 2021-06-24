package vf.pr.api.model.stored.meeting

import utopia.vault.model.template.Stored
import vf.pr.api.model.partial.meeting.MeetingStartUrlData

/**
 * Represents a meeting start url that has been stored in the DB
 * @author Mikko Hilpinen
 * @since 24.6.2021, v0.1
 */
case class MeetingStartUrl(id: Int, data: MeetingStartUrlData) extends Stored[MeetingStartUrlData, Int]
