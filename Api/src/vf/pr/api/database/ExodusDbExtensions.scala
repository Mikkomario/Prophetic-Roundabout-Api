package vf.pr.api.database

import utopia.exodus.database.access.single.DbUser.DbSingleUser
import vf.pr.api.database.access.single.user.DbRoundaboutUserSettings
import vf.pr.api.database.access.single.zoom.DbZoomRefreshToken

/**
 * This object contains extensions to database accessors in the Exodus project
 * @author Mikko Hilpinen
 * @since 18.6.2021, v0.1
 */
object ExodusDbExtensions
{
	implicit class ExtendedSingleDbUser(val a: DbSingleUser) extends AnyVal
	{
		/**
		 * @return An access point to this user's Roundabout-specific settings
		 */
		def roundaboutSettings = DbRoundaboutUserSettings.forUserWithId(a.userId)
		
		/**
		 * @return An access point to this user's Zoom refresh token
		 */
		def zoomRefreshToken = DbZoomRefreshToken.forUserWithId(a.userId)
	}
}
