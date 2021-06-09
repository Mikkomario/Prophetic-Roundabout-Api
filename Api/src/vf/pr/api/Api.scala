package vf.pr.api

import utopia.access.http.Status.{BadRequest, InternalServerError}
import vf.pr.api.util.{Globals, Log}
import utopia.bunnymunch.jawn.JsonBunny
import utopia.exodus.rest.resource.ExodusResources
import utopia.exodus.rest.util.AuthorizedContext
import utopia.exodus.util.ExodusContext
import utopia.flow.parse.JsonParser
import utopia.nexus.http.{Path, ServerSettings}
import utopia.nexus.rest.RequestHandler
import utopia.nexus.servlet.HttpExtensions._
import utopia.vault.database.Connection
import utopia.vault.util.{ErrorHandling, ErrorHandlingPrinciple}
import vf.pr.api.database.Tables
import vf.pr.api.database.access.single.setting.ApiSettings

import javax.servlet.annotation.MultipartConfig
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import scala.util.{Failure, Success, Try}

/**
 * This servlet provides access to fuel api
 * @author Mikko Hilpinen
 * @since 6.6.2021, v0.1
 */
@MultipartConfig(
	fileSizeThreshold   = 1048576,  // 1 MB
	maxFileSize         = 10485760, // 10 MB
	maxRequestSize      = 20971520, // 20 MB
)
class Api extends HttpServlet
{
	// ATTRIBUTES	--------------------------
	
	private implicit val jsonParser: JsonParser = JsonBunny
	
	private var cachedSetup: Option[Try[(ServerSettings, RequestHandler[AuthorizedContext])]] = None
	
	
	// INITIAL CODE    -----------------------
	
	// Sets up Exodus
	println("Setting up Exodus context")
	ExodusContext.setup(Globals.executionContext, Globals.connectionPool, Tables.databaseName) { (error, message) =>
		Log.withoutConnection("Exodus.context", message, Some(error))
	}
	Connection.modifySettings { _.copy(driver = Some("org.mariadb.jdbc.Driver")) }
	ErrorHandling.defaultPrinciple = ErrorHandlingPrinciple.Custom { error =>
		Log.withoutConnection("Api.db", error = Some(error)) }
	
	
	// IMPLEMENTED METHODS    ----------------
	
	override def doGet(req: HttpServletRequest, res: HttpServletResponse) =
	{
		// Basic setup needs to succeed in order to process requests
		setup match
		{
			case Success(data) =>
				implicit val settings: ServerSettings = data._1
				val requestHandler = data._2
				
				// Request conversion may also fail
				req.toRequest match
				{
					case Some(request) => requestHandler(request).update(res)
					case None => res.setStatus(BadRequest.code)
				}
			case Failure(exception) =>
				res.setStatus(InternalServerError.code)
				Log.withoutConnection("Api.setup", error = Some(exception))
		}
	}
	
	override def doPost(request: HttpServletRequest, response: HttpServletResponse) = doGet(request, response)
	override def doPut(request: HttpServletRequest, response: HttpServletResponse) = doGet(request, response)
	override def doDelete(request: HttpServletRequest, response: HttpServletResponse) = doGet(request, response)
	override def doHead(request: HttpServletRequest, response: HttpServletResponse) = doGet(request, response)
	
	
	// OTHER	------------------------------
	
	// Uses cached settings or reads them
	private def setup = cachedSetup.getOrElse {
		import Globals.executionContext
		val readResult = Globals.connectionPool.tryWith { implicit connection =>
			ApiSettings.address.map { address =>
				implicit val settings: ServerSettings = ServerSettings(address)
				// TODO: When releasing api v2, add support for versioned resources in RequestHandler
				val path = ApiSettings.rootPath match
				{
					case Some(rootPath) => rootPath/"api"/"v1"
					case None => Path("api", "v1")
				}
				val requestHandler = new RequestHandler[AuthorizedContext](ExodusResources.default,
					Some(path), AuthorizedContext(_) { error => Log("Api.request.context", error) })
				
				settings -> requestHandler
			}
		}.flatten
		// Successful reads are cached
		if (readResult.isSuccess)
			cachedSetup = Some(readResult)
		readResult
	}
}
