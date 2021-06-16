package vf.pr.api.database.model.logging

import utopia.access.http.{Method, Status}
import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.nexus.http.Path
import utopia.vault.model.immutable.Storable
import utopia.vault.model.template.DataInserter
import vf.pr.api.database.Tables
import vf.pr.api.model.partial.logging.RequestLogData
import vf.pr.api.model.stored.logging.RequestLog

import java.time.Instant
import scala.concurrent.duration.Duration

object RequestLogModel extends DataInserter[RequestLogModel, RequestLog, RequestLogData]
{
	override def table = Tables.request
	
	override def apply(data: RequestLogData): RequestLogModel = apply(None, Some(data.method), data.path,
		Some(data.status), Some(data.created), Some(data.duration))
	
	override protected def complete(id: Value, data: RequestLogData) = RequestLog(id.getLong, data)
}

/**
 * Used for interacting with request logs in the DB
 * @author Mikko Hilpinen
 * @since 16.6.2021, v0.1
 */
case class RequestLogModel(id: Option[Long] = None, method: Option[Method], path: Option[Path] = None,
                           status: Option[Status] = None, created: Option[Instant] = None,
                           duration: Option[Duration] = None) extends Storable
{
	override def table = RequestLogModel.table
	
	override def valueProperties = Vector("id" -> id, "method" -> method.map { _.toString },
		"path" -> path.map { _.toString }, "status" -> status.map { _.code }, "created" -> created,
		"durationMicroSeconds" -> duration.map { _.toMicros })
}
