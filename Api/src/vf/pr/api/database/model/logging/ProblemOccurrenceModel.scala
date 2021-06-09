package vf.pr.api.database.model.logging

import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.model.template.DataInserter
import vf.pr.api.database.factory.logging.ProblemOccurrenceFactory
import vf.pr.api.model.partial.logging.ProblemOccurrenceData
import vf.pr.api.model.stored.logging.ProblemOccurrence

import java.time.Instant

object ProblemOccurrenceModel extends DataInserter[ProblemOccurrenceModel, ProblemOccurrence, ProblemOccurrenceData]
{
	// ATTRIBUTES   -------------------------
	
	/**
	 * The length of characters being indexed in a message
	 */
	val messageIndexLength = 32
	
	
	// COMPUTED ------------------------------
	
	/**
	 * @return The factory used by this model
	 */
	def factory = ProblemOccurrenceFactory
	
	
	// IMPLEMENTED  --------------------------
	
	override def table = factory.table
	
	override def apply(data: ProblemOccurrenceData): ProblemOccurrenceModel =
		apply(None, Some(data.problemId), data.message, data.stack, Some(data.created))
	
	override protected def complete(id: Value, data: ProblemOccurrenceData) = ProblemOccurrence(id.getLong, data)
}

/**
 * Used for interacting with problem occurrences in the DB
 * @author Mikko Hilpinen
 * @since 6.6.2021, v0.1
 */
case class ProblemOccurrenceModel(id: Option[Long] = None, problemId: Option[Int] = None,
                                  message: Option[String] = None, stack: Option[String] = None,
                                  created: Option[Instant] = None)
	extends StorableWithFactory[ProblemOccurrence]
{
	override def factory = ProblemOccurrenceModel.factory
	
	override def valueProperties = Vector("id" -> id, "problemId" -> problemId, "message" -> message, "stack" -> stack,
		"created" -> created)
}