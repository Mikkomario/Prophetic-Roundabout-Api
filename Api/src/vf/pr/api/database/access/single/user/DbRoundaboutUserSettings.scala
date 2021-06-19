package vf.pr.api.database.access.single.user

import utopia.flow.time.Now
import utopia.vault.database.Connection
import utopia.vault.nosql.access.{SingleRowModelAccess, UniqueModelAccess}
import vf.pr.api.database.factory.user.RoundaboutUserSettingsFactory
import vf.pr.api.database.model.user.RoundaboutUserSettingsModel
import vf.pr.api.model.partial.user.RoundaboutUserSettingsData
import vf.pr.api.model.stored.user.RoundaboutUserSettings

/**
 * Used for accessing individual Roundabout-specific user settings instances
 * @author Mikko Hilpinen
 * @since 18.6.2021, v0.1
 */
object DbRoundaboutUserSettings extends SingleRowModelAccess[RoundaboutUserSettings]
{
	// COMPUTED -----------------------------------
	
	private def model = RoundaboutUserSettingsModel
	
	
	// IMPLEMENTED  -------------------------------
	
	override def factory = RoundaboutUserSettingsFactory
	
	override def globalCondition = Some(factory.nonDeprecatedCondition)
	
	
	// OTHER    -----------------------------------
	
	/**
	 * @param userId A user id
	 * @return An access point to that user's Roundabout-specific settings
	 */
	def forUserWithId(userId: Int) = DbSingleUserRoundaboutSettings(userId)
	
	
	// NESTED   -----------------------------------
	
	case class DbSingleUserRoundaboutSettings(userId: Int) extends UniqueModelAccess[RoundaboutUserSettings]
	{
		// IMPLEMENTED  ---------------------------
		
		override def factory = DbRoundaboutUserSettings.factory
		
		override def condition = DbRoundaboutUserSettings.mergeCondition(model.withUserId(userId))
		
		
		// OTHER    -------------------------------
		
		/**
		 * @param connection Implicit DB connection
		 * @return Roundabout-specific settings for this user (inserted if necessary)
		 */
		def pullOrInsert()(implicit connection: Connection) = pull.getOrElse {
			model.insert(RoundaboutUserSettingsData(userId))
		}
		
		/**
		 * Updates this user's settings
		 * @param timeZoneId This user's new time zone
		 * @param ownsProZoomAccount Whether this user owns a pro Zoom account
		 * @param connection DB Connection (implicit)
		 * @return Newly inserted settings instance
		 */
		def update(timeZoneId: Option[String] = None, ownsProZoomAccount: Boolean = false)
		          (implicit connection: Connection) =
		{
			// Deprecates the existing settings
			putAttribute(model.deprecationAttName, Now.toValue)
			// Inserts the new ones
			model.insert(RoundaboutUserSettingsData(userId, timeZoneId, ownsProZoomAccount = ownsProZoomAccount))
		}
	}
}
