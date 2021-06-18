package vf.pr.api.database

import utopia.exodus.database.Tables
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
	def zoomAuthAttempt = apply("zoom_authentication_attempt")
	/**
	 * @return Table that records Zoom refresh tokens (long-term)
	 */
	def zoomRefreshToken = apply("zoom_refresh_token")
	/**
	 * @return Table that records Zoom session tokens (short-term)
	 */
	def zoomSessionToken = apply("zoom_session_token")
	
	
	// OTHER    ---------------------------------
	
	private def apply(tableName: String): Table = Tables(tableName)
}