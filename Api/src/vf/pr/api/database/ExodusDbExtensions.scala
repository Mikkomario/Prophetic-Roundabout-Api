package vf.pr.api.database

import utopia.citadel.database.access.single.DbUser.DbSingleUser
import utopia.vault.database.Connection
import vf.pr.api.database.access.many.DbMeetings
import vf.pr.api.database.access.single.user.DbRoundaboutUserSettings
import vf.pr.api.database.access.single.zoom.{DbZoomRefreshToken, DbZoomSessionToken}

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
		/**
		 * @param connection Implicit DB connection
		 * @return An active zoom session token for that user
		 */
		def zoomSessionToken(implicit connection: Connection) =
			DbZoomSessionToken.forUserWithId(a.userId)
		/**
		 * @param connection Implicit DB connection
		 * @return Whether this user has been authorized via Zoom (= has a valid refresh token)
		 */
		def isZoomAuthorized(implicit connection: Connection) = zoomRefreshToken.nonEmpty
		
		/**
		 * @param connection Implicit DB connection
		 * @return Upcoming and recent meetings in the organizations this user belongs to
		 */
		def upcomingAndRecentMeetings(implicit connection: Connection) =
			DbMeetings.upcomingAndRecent.forUserWithId(a.userId)
	}
}
