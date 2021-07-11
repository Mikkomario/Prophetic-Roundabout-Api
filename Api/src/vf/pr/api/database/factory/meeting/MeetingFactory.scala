package vf.pr.api.database.factory.meeting

import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.flow.generic.ValueUnwraps._
import utopia.flow.time.TimeExtensions._
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.pr.api.database.RoundaboutTables
import vf.pr.api.model.partial.meeting.MeetingData
import vf.pr.api.model.stored.meeting.Meeting

/**
 * Used for reading Roundabout meeting data from the DB
 * @author Mikko Hilpinen
 * @since 24.6.2021, v0.1
 */
object MeetingFactory extends FromValidatedRowModelFactory[Meeting]
{
	override def table = RoundaboutTables.meeting
	
	override protected def fromValidatedModel(model: Model[Constant]) = Meeting(model("id"), MeetingData(
		model("zoomId"), model("zoomUuid"), model("hostId"), model("hostOrganizationId"), model("name"),
		model("startTime"), model("plannedDurationMinutes").getInt.minutes, model("password"), model("joinUrl"),
		model("created")))
}
