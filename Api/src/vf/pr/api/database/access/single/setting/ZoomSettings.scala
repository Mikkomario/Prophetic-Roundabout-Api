package vf.pr.api.database.access.single.setting

import utopia.flow.time.TimeExtensions._

/**
 * Used for accessing Zoom-related settings
 * @author Mikko Hilpinen
 * @since 16.6.2021, v0.1
 */
object ZoomSettings extends SettingsAccess("zoom",
	Map("auth-uri" -> 15.minutes, "token-uri" -> 15.minutes,  "api-base-uri" -> 15.minutes,
		"client-id" -> 12.hours, "client-secret" -> 12.hours,
		"authentication-timeout-hours" -> 2.hours, "max-user-wait-seconds" -> 2.hours))
{
	/**
	 * @return Address on Zoom server that provides authentication
	 */
	def authenticationUri = requiredString("auth-uri")
	/**
	 * @return Address on Zoom server that returns Zoom access tokens
	 */
	def tokenUri = requiredString("token-uri")
	/**
	 * @return Base address of the zoom api, which will be appended with the resource paths. Ends with /.
	 */
	def apiBaseUri = requiredString("api-base-uri")
	
	/**
	 * @return Uri that will receive the authentication result from Zoom
	 */
	def redirectUri = requiredString("redirect-uri")
	/**
	 * @return Uri where the user will be redirected to when the initial Zoom authentication
	 *         process finishes (optional)
	 */
	def resultPageUri = apply("auth-result-page-uri").string
	
	/**
	 * @return Application-specific client id
	 */
	def clientId = requiredString("client-id")
	/**
	 * @return Application-specific client secret
	 */
	def clientSecret = requiredString("client-secret")
	/**
	 * @return Application-specific client id and client secret as a pair
	 */
	def clientIdAndSecret = clientId.flatMap { clientId => clientSecret.map { clientId -> _ } }
	
	/**
	 * @return Timeout applied for authentication attempts
	 */
	def authTimeout = apply("authentication-timeout-hours").doubleOr(22.0).hours
	/**
	 * @return Timeout after which user is answered, regardless of whether a request finished or not
	 */
	def maxUserWaitDuration = apply("max-user-wait-seconds").doubleOr(10.0).seconds
}
