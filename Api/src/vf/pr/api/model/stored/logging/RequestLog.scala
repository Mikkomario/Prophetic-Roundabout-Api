package vf.pr.api.model.stored.logging

import utopia.vault.model.template.Stored
import vf.pr.api.model.partial.logging.RequestLogData

/**
 * Represents a logged request
 * @author Mikko Hilpinen
 * @since 16.6.2021, v0.1
 */
case class RequestLog(id: Long, data: RequestLogData) extends Stored[RequestLogData, Long]
