package vf.pr.api.database.access.single

import utopia.flow.time.Now
import utopia.flow.util.ErrorExtensions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.{SingleRowModelAccess, UniqueModelAccess}
import vf.pr.api.database.factory.logging.ProblemFactory
import vf.pr.api.database.model.logging.{ProblemModel, ProblemOccurrenceModel}
import vf.pr.api.model.enumeration.Severity
import vf.pr.api.model.partial.logging.{ProblemData, ProblemOccurrenceData}
import vf.pr.api.model.stored.logging.Problem

import java.time.Instant

/**
 * Used for accessing individual database logs
 * @author Mikko Hilpinen
 * @since 6.6.2021, v0.1
 */
object DbProblem extends SingleRowModelAccess[Problem]
{
	// COMPUTED -------------------------------
	
	private def model = ProblemModel
	private def occurrenceModel = ProblemOccurrenceModel
	
	
	// IMPLEMENTED  ---------------------------
	
	override def factory = ProblemFactory
	
	override def globalCondition = None
	
	
	// OTHER    -------------------------------
	
	/**
	 * @param context Problem context
	 * @param severity Problem severity
	 * @return An access point to that problem
	 */
	def apply(context: String, severity: Severity) = UniqueProblemAccess(context, severity)
	
	
	// NESTED   -------------------------------
	
	case class UniqueProblemAccess(context: String, severity: Severity) extends UniqueModelAccess[Problem]
	{
		// COMPUTED ---------------------------
		
		/**
		 * @param connection DB Connection (implicit)
		 * @return The id of this problem
		 */
		def id(implicit connection: Connection) = index.int
		
		
		// IMPLEMENTED  -----------------------
		
		override val condition = model.withSeverity(severity).withContext(context).toCondition
		
		override def factory = DbProblem.factory
		
		
		// OTHER    ---------------------------
		
		/**
		 * Records a new occurrence for this problem
		 * @param message Error message (optional)
		 * @param error Associated error (optional)
		 * @param time Occurrence timestamp (default = Now)
		 * @param connection DB Connection (implicit)
		 * @return Newly recorded occurrence
		 */
		def recordOccurrence(message: String = "", error: Option[Throwable] = None, time: Instant = Now)
		                    (implicit connection: Connection) =
		{
			// Inserts a new problem if necessary
			val targetId = id.getOrElse {
				model.insert(ProblemData(context, severity, time)).id
			}
			// Inserts a new occurrence
			occurrenceModel.insert(ProblemOccurrenceData(targetId, Some(message).filter { _.nonEmpty },
				error.map { _.stackTraceString }, time))
		}
	}
}
