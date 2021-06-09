package vf.pr.api.model.stored.logging

import utopia.vault.model.template.Stored
import vf.pr.api.model.partial.logging.ProblemOccurrenceData

/**
 * Represents a stored case where a problem occurred
 * @author Mikko Hilpinen
 * @since 6.6.2021, v0.1
 */
case class ProblemOccurrence(id: Long, data: ProblemOccurrenceData) extends Stored[ProblemOccurrenceData, Long]
