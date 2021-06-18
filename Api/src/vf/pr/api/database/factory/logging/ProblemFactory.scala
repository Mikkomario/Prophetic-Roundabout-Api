package vf.pr.api.database.factory.logging

import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.flow.generic.ValueUnwraps._
import utopia.vault.nosql.factory.FromValidatedRowModelFactory
import vf.pr.api.database.RoundaboutTables
import vf.pr.api.model.enumeration.Severity
import vf.pr.api.model.partial.logging
import vf.pr.api.model.stored
import vf.pr.api.model.stored.logging.Problem

/**
 * Used for reading problems from DB
 * @author Mikko Hilpinen
 * @since 6.6.2021, v0.1
 */
object ProblemFactory extends FromValidatedRowModelFactory[Problem]
{
	override def table = RoundaboutTables.problem
	
	override protected def fromValidatedModel(model: Model[Constant]) = stored.logging.Problem(model("id"),
		logging.ProblemData(model("context"), Severity.forId(model("severity")), model("created")))
}
