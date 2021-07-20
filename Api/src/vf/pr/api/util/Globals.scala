package vf.pr.api.util

import utopia.ambassador.controller.implementation.AcquireTokens
import utopia.ambassador.model.cached.TokenInterfaceConfiguration
import utopia.flow.async.ThreadPool
import utopia.flow.caching.multi.Cache
import utopia.vault.database.ConnectionPool
import vf.pr.api.model.enumeration.Service
import vf.pr.api.model.enumeration.Service.{Google, Zoom}

import scala.concurrent.ExecutionContext

/**
 * Contains globally used constants
 * @author Mikko Hilpinen
 * @since 6.6.2021, v0.1
 */
object Globals
{
	// ATTRIBUTES   --------------------------------
	
	/**
	 * The thread pool used in this project for asynchronous tasks
	 */
	val threadPool = new ThreadPool("Prophetic Roundabout Api")
	/**
	 * The connection pool used in this project for database interactions
	 */
	implicit val connectionPool: ConnectionPool = new ConnectionPool()
	
	/**
	 * Gateway used for making requests to Zoom servers
	 */
	@deprecated("Please use Zoom.gateway instead", "v0.2")
	def zoomGateway = Zoom.gateway
	/**
	 * Gateway used for making requests to Google servers
	 */
	@deprecated("Please use Google.gateway instead", "v0.2")
	def googleGateway = Google.gateway
	
	/**
	 * An interface for acquiring access tokens to different services (Zoom & Google)
	 */
	val acquireTokens = new AcquireTokens(Cache { serviceId: Int =>
		Service.forId(serviceId) match
		{
			case Some(service) => service.tokenConfig
			case None =>
				Log.warning.withoutConnection("AcquireTokens.init",
					s"Service id $serviceId is not recognized")
				TokenInterfaceConfiguration(Google.gateway)
		}
	})
	
	
	// COMPUTED ------------------------------------
	
	/**
	 * @return The execution context based on the thread pool used
	 */
	implicit def executionContext: ExecutionContext = threadPool.executionContext
}
