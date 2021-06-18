package vf.pr.api.database.model.user

import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.model.template.DataInserter
import vf.pr.api.database.factory.user.RoundaboutUserSettingsFactory
import vf.pr.api.model.partial.user.RoundaboutUserSettingsData
import vf.pr.api.model.stored.user.RoundaboutUserSettings

import java.time.Instant

object RoundaboutUserSettingsModel
	extends DataInserter[RoundaboutUserSettingsModel, RoundaboutUserSettings, RoundaboutUserSettingsData]
{
	// ATTRIBUTES   ------------------------
	
	/**
	 * Name of the property that contains settings deprecation time
	 */
	val deprecationAttName = "deprecatedAfter"
	
	
	// COMPUTED ----------------------------
	
	/**
	 * @return Factory used by this model
	 */
	def factory = RoundaboutUserSettingsFactory
	
	
	// IMPLEMENTED  -------------------------
	
	override def table = factory.table
	
	override def apply(data: RoundaboutUserSettingsData) =
		apply(None, Some(data.userId), data.timeZoneId, Some(data.ownsProZoomAccount), Some(data.created),
			data.deprecatedAfter)
	
	override protected def complete(id: Value, data: RoundaboutUserSettingsData) =
		RoundaboutUserSettings(id.getInt, data)
	
	
	// OTHER    ------------------------------
	
	/**
	 * @param userId Settings owner user id
	 * @return A model with that user id
	 */
	def withUserId(userId: Int) = apply(userId = Some(userId))
}

/**
 * Used for interacting with Roundabout-specific user settings in the DB
 * @author Mikko Hilpinen
 * @since 18.6.2021, v0.1
 */
case class RoundaboutUserSettingsModel(id: Option[Int] = None, userId: Option[Int] = None,
                                       timeZoneId: Option[String] = None, ownsProZoomAccount: Option[Boolean] = None,
                                       created: Option[Instant] = None, deprecatedAfter: Option[Instant] = None)
	extends StorableWithFactory[RoundaboutUserSettings]
{
	import RoundaboutUserSettingsModel._
	
	override def factory = RoundaboutUserSettingsModel.factory
	
	override def valueProperties = Vector("id" -> id, "userId" -> userId, "timeZoneId" -> timeZoneId,
		"ownsProZoomAccount" -> ownsProZoomAccount, "created" -> created, deprecationAttName -> deprecatedAfter)
}