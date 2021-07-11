package vf.pr.api.database.factory.user

import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.flow.generic.ValueUnwraps._
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import utopia.vault.nosql.template.Deprecatable
import vf.pr.api.database.RoundaboutTables
import vf.pr.api.model.partial.user.RoundaboutUserSettingsData
import vf.pr.api.model.stored.user.RoundaboutUserSettings

/**
 * Used for reading Roundabout-specific user settings from the DB
 * @author Mikko Hilpinen
 * @since 18.6.2021, v0.1
 */
object RoundaboutUserSettingsFactory extends FromValidatedRowModelFactory[RoundaboutUserSettings] with Deprecatable
{
	// ATTRIBUTES   ------------------------
	
	override lazy val nonDeprecatedCondition = table("deprecatedAfter").isNull
	
	
	// IMPLEMENTED  ------------------------
	
	override def table = RoundaboutTables.userSettings
	
	override protected def fromValidatedModel(model: Model[Constant]) = RoundaboutUserSettings(model("id"),
		RoundaboutUserSettingsData(model("userId"), model("timeZoneId"), model("created"), model("deprecatedAfter"),
			model("ownsProZoomAccount")))
}
