package vf.pr.api.database.factory.logging

import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.flow.generic.ValueUnwraps._
import utopia.vault.nosql.factory.row.FromRowFactoryWithTimestamps
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.pr.api.database.RoundaboutTables
import vf.pr.api.model.partial.logging.ProblemOccurrenceData
import vf.pr.api.model.stored.logging.ProblemOccurrence

/**
 * Used for reading problem occurrence data from the DB
 * @author Mikko Hilpinen
 * @since 6.6.2021, v0.1
 */
object ProblemOccurrenceFactory extends FromValidatedRowModelFactory[ProblemOccurrence]
	with FromRowFactoryWithTimestamps[ProblemOccurrence]
{
	// ATTRIBUTES   ---------------------------
	
	override val creationTimePropertyName = "created"
	
	
	// IMPLEMENTED  ---------------------------
	
	override def table = RoundaboutTables.problemOccurrence
	
	override protected def fromValidatedModel(model: Model[Constant]) = ProblemOccurrence(model("id"),
		ProblemOccurrenceData(model("problemId"), model("message"), model("stack"), model(creationTimePropertyName)))
}
