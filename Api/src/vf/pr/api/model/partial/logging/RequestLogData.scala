package vf.pr.api.model.partial.logging

import utopia.access.http.Method.Get
import utopia.access.http.Status.OK
import utopia.access.http.{Method, Status}
import utopia.flow.time.Now
import utopia.nexus.http.Path

import java.time.Instant
import scala.concurrent.duration.Duration

/**
 * Contains basic information about a request and its response
 * @author Mikko Hilpinen
 * @since 16.6.2021, v0.1
 * @param method Method used in the request (default = GET)
 * @param path Path targeted by the request (default = root)
 * @param status Status returned to client (default = 200 OK)
 * @param created Time when the request was received (default = Now)
 * @param duration Duration how long it took to process the request (default = 0s)
 */
case class RequestLogData(method: Method = Get, path: Option[Path] = None, status: Status = OK, created: Instant = Now,
                          duration: Duration = Duration.Zero)
