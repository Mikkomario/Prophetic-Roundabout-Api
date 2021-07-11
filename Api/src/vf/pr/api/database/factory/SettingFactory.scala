package vf.pr.api.database.factory

import utopia.bunnymunch.jawn.JsonBunny
import utopia.flow.datastructure.immutable.{Constant, Model, Value}
import utopia.flow.generic.ValueUnwraps._
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.pr.api.database.RoundaboutTables
import vf.pr.api.model.partial.SettingData
import vf.pr.api.model.stored.Setting

/**
 * Used for reading settings from DB
 * @author Mikko Hilpinen
 * @since 6.6.2021, v0.1
 */
object SettingFactory extends FromValidatedRowModelFactory[Setting]
{
	override def table = RoundaboutTables.setting
	
	override protected def fromValidatedModel(model: Model[Constant]) = Setting(model("id"),
		SettingData(model("category"), model("field"), model("jsonValue").string match
		{
			case Some(json) => JsonBunny.sureMunch(json)
			case None => Value.empty
		}))
}
