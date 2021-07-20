package vf.pr.api.database

import utopia.citadel.database.Tables
import utopia.vault.model.immutable.Table

/**
 * An access point to all database tables in this project
 * @author Mikko Hilpinen
 * @since 6.6.2021, v0.1
 */
object RoundaboutTables
{
	// COMPUTED ---------------------------------
	
	/**
	 * @return A table that contains settings
	 */
	def setting = apply("setting")
	/**
	 * @return A table that records server side problems
	 */
	def problem = apply("problem")
	/**
	 * @return A table that records server side problem occurrences
	 */
	def problemOccurrence = apply("problem_occurrence")
	/**
	 * @return A table that records incoming requests and their responses
	 */
	def request = apply("request")
	
	/**
	 * @return A table that contains Roundabout-specific user settings
	 */
	def userSettings = apply("user_roundabout_settings")
	
	/**
	 * @return Table that records (first time) Zoom authentication attempts
	 */
	@deprecated("Replaced with the Ambassador dependency", "v0.2")
	def zoomAuthAttempt = apply("zoom_authentication_attempt")
	/**
	 * @return Table that records Zoom refresh tokens (long-term)
	 */
	@deprecated("Replaced with the Ambassador dependency", "v0.2")
	def zoomRefreshToken = apply("zoom_refresh_token")
	/**
	 * @return Table that records Zoom session tokens (short-term)
	 */
	@deprecated("Replaced with the Ambassador dependency", "v0.2")
	def zoomSessionToken = apply("zoom_session_token")
	
	/**
	 * @return Table that contains scheduled Roundabout meetings
	 */
	def meeting = apply("meeting")
	/**
	 * @return Table that contains start urls for scheduled Roundabout meetings
	 */
	def meetingStartUrl = apply("meeting_start_url")
	
	
	// OTHER    ---------------------------------
	
	private def apply(tableName: String): Table = Tables(tableName)
}
