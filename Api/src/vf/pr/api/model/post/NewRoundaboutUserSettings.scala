package vf.pr.api.model.post

import utopia.flow.datastructure.immutable
import utopia.flow.datastructure.immutable.{Constant, ModelDeclaration, PropertyDeclaration}
import utopia.flow.datastructure.template.{Model, Property}
import utopia.flow.generic.{BooleanType, FromModelFactory, FromModelFactoryWithSchema}
import utopia.flow.generic.ValueUnwraps._
import vf.pr.api.model.partial.user.RoundaboutUserSettingsData

import scala.util.Success

object NewRoundaboutUserSettings extends FromModelFactory[NewRoundaboutUserSettings]
{
	// ATTRIBUTES   -------------------------------
	
	/**
	 * A factory for parsing full updates (Put use case)
	 */
	val fullUpdateFactory: FromModelFactoryWithSchema[NewRoundaboutUserSettings] =
		new FromModelFactoryWithSchema[NewRoundaboutUserSettings]
		{
			override val schema = ModelDeclaration(PropertyDeclaration("owns_pro_zoom_account", BooleanType))
			
			override protected def fromValidatedModel(model: immutable.Model[Constant]) =
				_apply(model)
		}
	
	
	// IMPLEMENTED  -------------------------------
	
	override def apply(model: Model[Property]) = Success(_apply(model))
	
	
	// OTHER    -----------------------------------
	
	private def _apply(model: Model[Property]): NewRoundaboutUserSettings =
		apply(model("time_zone_id"), model("owns_pro_zoom_account"))
}

/**
 * Used for posting new roundabout user settings (Put or Patch)
 * @author Mikko Hilpinen
 * @since 18.6.2021, v0.1
 */
case class NewRoundaboutUserSettings(timeZoneId: Option[String] = None, ownsProZoomAccount: Option[Boolean] = None)
{
	/**
	 * @param existingSettings Existing settings instance
	 * @return Whether this settings update would modify that settings instance
	 */
	def wouldUpdate(existingSettings: RoundaboutUserSettingsData) =
		timeZoneId.exists { newTimeZone => !existingSettings.timeZoneId.contains(newTimeZone) } ||
			ownsProZoomAccount.exists { existingSettings.ownsProZoomAccount != _ }
}
