package vf.pr.api.model.enumeration

import utopia.ambassador.controller.implementation.{DefaultRedirector, GoogleRedirector}
import utopia.ambassador.controller.template.AuthRedirector
import utopia.ambassador.model.cached.TokenInterfaceConfiguration
import utopia.bunnymunch.jawn.JsonBunny
import utopia.disciple.apache.Gateway
import utopia.disciple.http.request.Timeout
import utopia.flow.parse.JSONReader
import utopia.flow.time.TimeExtensions._

import scala.io.Codec

/**
 * An enumeration for 3rd party services used by this api
 * @author Mikko Hilpinen
 * @since 20.7.2021, v0.2
 */
sealed trait Service
{
	/**
	 * Id of this service in the database
	 */
	val id: Int
	/**
	 * @return The gateway used by this service
	 */
	def gateway: Gateway
	/**
	 * @return Configuration used when acquiring access tokens for this service
	 */
	def tokenConfig: TokenInterfaceConfiguration
	/**
	 * @return Redirector used when redirecting the user from/to this service
	 */
	def redirector: AuthRedirector
}

object Service
{
	// ATTRIBUTES   -------------------------
	
	/**
	 * All currently available values of this enumeration
	 */
	lazy val values = Vector[Service](Zoom, Google)
	
	
	// OTHER    -----------------------------
	
	/**
	 * @param serviceId A service id
	 * @return A service matching that id. None if no such service was found.
	 */
	def forId(serviceId: Int) = values.find { _.id == serviceId }
	
	
	// NESTED   -----------------------------
	
	/**
	 * Zoom service, used for hosting and joining video meetings
	 */
	case object Zoom extends Service
	{
		override val id = 1
		
		// TODO: See if same gateway instance can be used for both targets (depends on parameter encoding)
		override lazy val gateway = new Gateway(Vector(JsonBunny, JSONReader),
			maximumTimeout = Timeout(30.seconds, 30.seconds),
			allowBodyParameters = false, allowJsonInUriParameters = false)
		override lazy val tokenConfig = TokenInterfaceConfiguration(gateway, 15.years.toApproximateDuration,
			useAuthorizationHeader = true)
		
		override def redirector = DefaultRedirector
	}
	
	/**
	 * Google (gmail) service, used for sending an receiving emails, among other things
	 */
	case object Google extends Service
	{
		override val id = 2
		override val redirector = GoogleRedirector(shouldUserSelectAccount = true)
		
		override lazy val gateway = new Gateway(Vector(JsonBunny, JSONReader),
			maximumTimeout = Timeout(30.seconds, 30.seconds), parameterEncoding = Some(Codec.UTF8),
			allowBodyParameters = false, allowJsonInUriParameters = false)
		override lazy val tokenConfig = TokenInterfaceConfiguration(gateway)
	}
}