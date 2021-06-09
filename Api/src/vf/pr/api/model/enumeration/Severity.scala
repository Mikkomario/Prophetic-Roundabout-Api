package vf.pr.api.model.enumeration

/**
 * Severity levels used in errors & logging
 * @author Mikko Hilpinen
 * @since 6.6.2021, v0.1
 */
sealed trait Severity
{
	/**
	 * @return An integer that represents this severity level
	 */
	def id: Int
}

object Severity
{
	// ATTRIBUTES   -----------------------------
	
	/**
	 * All available severity values
	 */
	lazy val values = Vector[Severity](Debug, Warning, Problem, Error, Critical)
	
	
	// COMPUTED ---------------------------------
	
	/**
	 * @return Smallest known severity
	 */
	def min = Debug
	/**
	 * @return Largest known severity
	 */
	def max = Critical
	
	
	// OTHER    ---------------------------------
	
	/**
	 * @param severityId A severity id
	 * @return A severity matching (or closest to) that id
	 */
	def forId(severityId: Int) = values.find { _.id == severityId }.getOrElse {
		if (severityId < min.id)
			min
		else
			max
	}
	
	
	// NESTED   ---------------------------------
	
	/**
	 * Debugging logs that are purely informative and don't indicate a problem
	 */
	case object Debug extends Severity
	{
		override def id = 0
	}
	/**
	 * Warnings that may indicate a problem but are often not necessary to act upon
	 */
	case object Warning extends Severity
	{
		override def id = 1
	}
	/**
	 * A problem in the software that can be recovered from but which should be fixed when possible
	 */
	case object Problem extends Severity
	{
		override def id = 2
	}
	/**
	 * A problem in the software that renders a portion of the service unavailable (high priority fix)
	 */
	case object Error extends Severity
	{
		override def id = 3
	}
	/**
	 * A problem in the software that prevents the use thereof and must be fixed ASAP
	 */
	case object Critical extends Severity
	{
		override def id = 4
	}
}
