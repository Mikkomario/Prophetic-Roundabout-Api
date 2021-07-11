package vf.pr.api.database.factory.meeting

import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.flow.generic.ValueUnwraps._
import utopia.flow.time.Now
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import utopia.vault.nosql.template.Deprecatable
import utopia.vault.sql.SqlExtensions._
import vf.pr.api.database.RoundaboutTables
import vf.pr.api.model.partial.meeting.MeetingStartUrlData
import vf.pr.api.model.stored.meeting.MeetingStartUrl

/**
 * Used for reading meeting start urls from the DB
 * @author Mikko Hilpinen
 * @since 24.6.2021, v0.1
 */
object MeetingStartUrlFactory extends FromValidatedRowModelFactory[MeetingStartUrl] with Deprecatable
{
	// ATTRIBUTES   -----------------------------
	
	private lazy val expirationColumn = table("expiration")
	
	
	// IMPLEMENTED  -----------------------------
	
	override def table = RoundaboutTables.meetingStartUrl
	
	override def nonDeprecatedCondition = expirationColumn > Now.toValue
	
	override protected def fromValidatedModel(model: Model[Constant]) = MeetingStartUrl(model("id"),
		MeetingStartUrlData(model("meetingId"), model("url"), model("created"), model("expiration")))
}
