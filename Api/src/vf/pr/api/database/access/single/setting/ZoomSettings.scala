package vf.pr.api.database.access.single.setting

import utopia.flow.time.TimeExtensions._

/**
 * Used for accessing Zoom-related settings
 * @author Mikko Hilpinen
 * @since 16.6.2021, v0.1
 */
object ZoomSettings extends SettingsAccess("zoom",
	Map("auth-uri" -> 15.minutes, "client-id" -> 12.hours, "client-secret" -> 12.hours,
		"authentication-timeout-hours" -> 1.hours))
{
	/**
	 * @return Address on Zoom server that provides authentication
	 */
	def authenticationUri = requiredString("auth-uri")
	
	/**
	 * @return Uri that will receive the authentication result from Zoom
	 */
	def redirectUri = requiredString("redirect-uri")
	
	/**
	 * @return Application-specific client id
	 */
	def clientId = requiredString("client-id")
	/**
	 * @return Application-specific client secret
	 */
	def clientSecret = requiredString("client-secret")
	
	/**
	 * @return Timeout applied for authentication attempts
	 */
	def authTimeout = apply("authentication-timeout-hours").doubleOr(22.0).hours
}
