package vf.pr.api.database.model.zoom

import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.model.template.DataInserter
import vf.pr.api.database.factory.zoom.ZoomAuthAttemptFactory
import vf.pr.api.model.partial.zoom.ZoomAuthAttemptData
import vf.pr.api.model.stored.zoom.ZoomAuthAttempt

import java.time.Instant

object ZoomAuthAttemptModel extends DataInserter[ZoomAuthAttemptModel, ZoomAuthAttempt, ZoomAuthAttemptData]
{
	// ATTRIBUTES   -------------------------------
	
	/**
	 * Name of the property that contains authentication closing / finishing time stamp, if specified
	 */
	val closingTimeAttName = "closed"
	
	
	// COMPUTED -----------------------------------
	
	/**
	 * @return The factory used by this model
	 */
	def factory = ZoomAuthAttemptFactory
	
	/**
	 * @return Column that contains authentication closing / finishing time stamp
	 */
	def closingTimeColumn = table(closingTimeAttName)
	
	/**
	 * @return A condition that only yields open authentication attempts
	 */
	def openCondition = closingTimeColumn.isNull
	
	
	// IMPLEMENTED  ------------------------------
	
	override def table = factory.table
	
	override def apply(data: ZoomAuthAttemptData) =
		apply(None, Some(data.userId), Some(data.token), Some(data.created), data.closed)
	
	override protected def complete(id: Value, data: ZoomAuthAttemptData) = ZoomAuthAttempt(id.getInt, data)
	
	
	// OTHER    ---------------------------------
	
	/**
	 * @param token An authentication token
	 * @return A model with that token
	 */
	def withToken(token: String) = apply(token = Some(token))
}

/**
 * Used for interacting with Zoom authentication (first time) attempts in DB
 * @author Mikko Hilpinen
 * @since 16.6.2021, v0.1
 */
case class ZoomAuthAttemptModel(id: Option[Int] = None, userId: Option[Int] = None, token: Option[String] = None,
                                created: Option[Instant] = None, closed: Option[Instant] = None)
	extends StorableWithFactory[ZoomAuthAttempt]
{
	import ZoomAuthAttemptModel._
	
	override def factory = ZoomAuthAttemptModel.factory
	
	override def valueProperties = Vector("id" -> id, "userId" -> userId, "token" -> token,
		"created" -> created, closingTimeAttName -> closed)
}
