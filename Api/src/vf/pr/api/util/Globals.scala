package vf.pr.api.util

import utopia.bunnymunch.jawn.JsonBunny
import utopia.disciple.apache.Gateway
import utopia.disciple.http.request.Timeout
import utopia.flow.async.ThreadPool
import utopia.flow.parse.JSONReader
import utopia.flow.time.TimeExtensions._
import utopia.vault.database.ConnectionPool

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
	val connectionPool = new ConnectionPool()
	/**
	 * Gateway used for making requests to Zoom servers
	 */
	val zoomGateway = new Gateway(Vector(JsonBunny, JSONReader), maximumTimeout = Timeout(30.seconds, 30.seconds),
		allowBodyParameters = false, allowJsonInUriParameters = false)
	
	
	// COMPUTED ------------------------------------
	
	/**
	 * @return The execution context based on the thread pool used
	 */
	implicit def executionContext: ExecutionContext = threadPool.executionContext
}
