package vf.pr.api.util

import scala.language.implicitConversions

import utopia.flow.util.CollectionExtensions._
import utopia.vault.database.Connection
import vf.pr.api.database.access.single.DbProblem
import vf.pr.api.model.enumeration.Severity
import vf.pr.api.model.enumeration.Severity.{Critical, Debug, Error, Warning}
import Globals._

/**
 * Used for logging errors
 * @author Mikko Hilpinen
 * @since 6.6.2021, v0.1
 */
object Log
{
	// ATTRIBUTES   -------------------------
	
	private val logs = Severity.values.map { s => s -> new Log(s) }.toMap
	
	
	// IMPLICIT -----------------------------
	
	// Implicitly accesses the default logger
	implicit def defaultInstance(log: Log.type): Log = log.default
	
	
	// COMPUTED -----------------------------
	
	/**
	 * @return The log for debugging
	 */
	def debug = logs(Debug)
	/**
	 * @return The log for posting warnings
	 */
	def warning = logs(Warning)
	/**
	 * @return The log for recording problems
	 */
	def default = logs(Severity.Problem)
	/**
	 * @return The log for recording errors
	 */
	def error = logs(Error)
	/**
	 * @return The log for recording critical failures
	 */
	def critical = logs(Critical)
	
	
	// OTHER    -----------------------------
	
	/**
	 * @param severity Problem severity
	 * @return A logger for that severity problems
	 */
	def apply(severity: Severity): Log = logs(severity)
}

case class Log private(severity: Severity)
{
	/**
	 * Writes a new log entry
	 * @param context Logging context
	 * @param message Logged message (optional)
	 * @param error Logged error (optional)
	 * @param connection DB Connection (implicit)
	 */
	def apply(context: String, message: String = "", error: Option[Throwable] = None)
	         (implicit connection: Connection): Unit =
		DbProblem(context, severity).recordOccurrence(message, error)
	
	/**
	 * Writes a new log entry
	 * @param context Logging context
	 * @param message Logged message (optional)
	 * @param error Logged error (optional)
	 * @param connection DB Connection (implicit)
	 */
	def apply(context: String, message: String, error: Throwable)(implicit connection: Connection): Unit =
		apply(context, message, Some(error))
	
	/**
	 * Writes a new log entry
	 * @param context Logging context
	 * @param error Logged error (optional)
	 * @param connection DB Connection (implicit)
	 */
	def apply(context: String, error: Throwable)(implicit connection: Connection): Unit =
		apply(context, "", error)
	
	/**
	 * Acquires a database connection and writes a new log entry
	 * @param context Logging context
	 * @param message Logged message (optional)
	 * @param error Logged error (optional)
	 */
	def withoutConnection(context: String, message: String = "", error: Option[Throwable] = None) =
		connectionPool.tryWith { implicit connection =>
			apply(context, message, error)
		}.failure.foreach { writeError =>
			// If primary logging fails, prints error to the console
			println(s"Failed to write new log $context ($severity): $message")
			writeError.printStackTrace()
			error.foreach { _.printStackTrace() }
		}
}