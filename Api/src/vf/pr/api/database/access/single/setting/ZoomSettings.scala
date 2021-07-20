package vf.pr.api.database.access.single.setting

import utopia.flow.time.TimeExtensions._

/**
 * Used for accessing Zoom-related settings
 * @author Mikko Hilpinen
 * @since 16.6.2021, v0.1
 */
object ZoomSettings extends SettingsAccess("zoom", Map("api-base-uri" -> 15.minutes))
{
	/**
	 * @return Address on Zoom server that provides authentication
	 */
	@deprecated("Replaced with the Ambassador dependency", "v0.2")
	def authenticationUri = requiredString("auth-uri")
	/**
	 * @return Address on Zoom server that returns Zoom access tokens
	 */
	@deprecated("Replaced with the Ambassador dependency", "v0.2")
	def tokenUri = requiredString("token-uri")
	/**
	 * @return Base address of the zoom api, which will be appended with the resource paths. Ends with /.
	 */
	def apiBaseUri = requiredString("api-base-uri")
	
	/**
	 * @return Uri that will receive the authentication result from Zoom
	 */
	@deprecated("Replaced with the Ambassador dependency", "v0.2")
	def redirectUri = requiredString("redirect-uri")
	/**
	 * @return Uri where the user will be redirected to when the initial Zoom authentication
	 *         process finishes (optional)
	 */
	@deprecated("Replaced with the Ambassador dependency", "v0.2")
	def resultPageUri = apply("auth-result-page-uri").string
	
	/**
	 * @return Application-specific client id
	 */
	@deprecated("Replaced with the Ambassador dependency", "v0.2")
	def clientId = requiredString("client-id")
	/**
	 * @return Application-specific client secret
	 */
	@deprecated("Replaced with the Ambassador dependency", "v0.2")
	def clientSecret = requiredString("client-secret")
	/**
	 * @return Application-specific client id and client secret as a pair
	 */
	@deprecated("Replaced with the Ambassador dependency", "v0.2")
	def clientIdAndSecret = clientId.flatMap { clientId => clientSecret.map { clientId -> _ } }
	
	/**
	 * @return Timeout applied for authentication attempts
	 */
	@deprecated("Replaced with the Ambassador dependency", "v0.2")
	def authTimeout = apply("authentication-timeout-hours").doubleOr(22.0).hours
	/**
	 * @return Timeout after which user is answered, regardless of whether a request finished or not
	 */
	@deprecated("Replaced with the Ambassador dependency", "v0.2")
	def maxUserWaitDuration = apply("max-user-wait-seconds").doubleOr(10.0).seconds
}
