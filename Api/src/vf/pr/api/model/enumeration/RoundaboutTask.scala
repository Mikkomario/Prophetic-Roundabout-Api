package vf.pr.api.model.enumeration

/**
 * An enumeration for the Roundabout specific task types
 * @author Mikko Hilpinen
 * @since 24.6.2021, v0.1
 */
sealed trait RoundaboutTask
{
	/**
	 * Id of this task type
	 */
	val id: Int
}

object RoundaboutTask
{
	/**
	 * Task for hosting new Roundabout meetings
	 */
	case object HostMeeting extends RoundaboutTask
	{
		override val id = 7
	}
}
