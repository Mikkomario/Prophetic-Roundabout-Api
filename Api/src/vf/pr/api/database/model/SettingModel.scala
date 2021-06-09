package vf.pr.api.database.model

import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import vf.pr.api.database.factory.SettingFactory
import vf.pr.api.model.stored.Setting

object SettingModel
{
	// ATTRIBUTES   ---------------------------
	
	/**
	 * Name of the property that contains setting value (as json)
	 */
	val valueAttName = "jsonValue"
	
	
	// COMPUTED -------------------------------
	
	/**
	 * @return The factory used by this model
	 */
	def factory = SettingFactory
	
	
	// OTHER    -------------------------------
	
	/**
	 * @param category Category
	 * @return A model with that category
	 */
	def withCategory(category: String) = apply(category = Some(category))
	/**
	 * @param field A field name
	 * @return A model with that field name
	 */
	def withField(field: String) = apply(field = Some(field))
}

/**
 * Used for interacting with settings in DB
 * @author Mikko Hilpinen
 * @since 6.6.2021, v0.1
 */
case class SettingModel(id: Option[Int] = None, category: Option[String] = None, field: Option[String] = None,
                        jsonValue: Option[String] = None) extends StorableWithFactory[Setting]
{
	import SettingModel._
	
	override def factory = SettingModel.factory
	
	override def valueProperties = Vector("id" -> id, "category" -> category, "field" -> field,
		valueAttName-> jsonValue)
}
