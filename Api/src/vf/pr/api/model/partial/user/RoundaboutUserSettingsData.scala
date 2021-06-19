package vf.pr.api.model.partial.user

import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ModelConvertible
import utopia.flow.generic.ValueConversions._
import utopia.flow.time.Now

import java.time.Instant

/**
 * Contains Roundabout-specific user settings
 * @author Mikko Hilpinen
 * @since 18.6.2021, v0.1
 * @param userId Id of the user whom these settings concern
 * @param timeZoneId Id (string) of the time zone this user uses (if known)
 * @param created Creation time of these settings (default = Now)
 * @param deprecatedAfter Timestamp when these settings were deprecated (None if not deprecated)
 * @param ownsProZoomAccount Whether this user has a pro/paid Zoom account
 */
case class RoundaboutUserSettingsData(userId: Int, timeZoneId: Option[String] = None, created: Instant = Now,
                                      deprecatedAfter: Option[Instant] = None, ownsProZoomAccount: Boolean = false)
	extends ModelConvertible
{
	override def toModel = Model(Vector("user_id" -> userId, "time_zone_id" -> timeZoneId,
		"owns_pro_zoom_account" -> ownsProZoomAccount, "created" -> created))
}
