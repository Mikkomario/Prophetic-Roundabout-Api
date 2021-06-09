package vf.pr.api.database.model.logging

import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.model.template.DataInserter
import vf.pr.api.database.factory.logging.ProblemFactory
import vf.pr.api.model.enumeration.Severity
import vf.pr.api.model.partial.logging.ProblemData
import vf.pr.api.model.stored.logging
import vf.pr.api.model.stored.logging.Problem

import java.time.Instant

object ProblemModel extends DataInserter[ProblemModel, Problem, ProblemData]
{
	// COMPUTED -----------------------------
	
	/**
	 * @return The factory used by this model
	 */
	def factory = ProblemFactory
	
	
	// IMPLEMENTED  -------------------------
	
	override def table = factory.table
	
	override def apply(data: ProblemData): ProblemModel = apply(None, Some(data.context), Some(data.severity),
		Some(data.created))
	
	override protected def complete(id: Value, data: ProblemData) = logging.Problem(id.getInt, data)
	
	
	// OTHER    -----------------------------
	
	/**
	 * @param severity A problem severity
	 * @return A model with that severity
	 */
	def withSeverity(severity: Severity) = apply(severity = Some(severity))
	/**
	 * @param context A problem context
	 * @return A model with that context
	 */
	def withContext(context: String) = apply(context = Some(context))
}

/**
 * Used for interacting with problem cases in the DB
 * @author Mikko Hilpinen
 * @since 6.6.2021, v0.1
 */
case class ProblemModel(id: Option[Int] = None, context: Option[String] = None, severity: Option[Severity] = None,
                        created: Option[Instant] = None)
	extends StorableWithFactory[Problem]
{
	// IMPLEMENTED  ----------------------------
	
	override def factory = ProblemModel.factory
	
	override def valueProperties = Vector("id" -> id, "context" -> context, "severity" -> severity.map { _.id },
		"created" -> created)
	
	
	// OTHER    --------------------------------
	
	/**
	 * @param context A problem context
	 * @return A copy of this model with that context
	 */
	def withContext(context: String) = copy(context = Some(context))
}
