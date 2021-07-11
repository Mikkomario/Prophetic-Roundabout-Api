package vf.pr.api.database.access.single.zoom

import utopia.flow.generic.ValueConversions._
import utopia.flow.time.Now
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.single.model.distinct.SingleIdModelAccess
import vf.pr.api.database.access.single.setting.ZoomSettings
import vf.pr.api.database.factory.zoom.ZoomAuthAttemptFactory
import vf.pr.api.database.model.zoom.ZoomAuthAttemptModel
import vf.pr.api.model.stored.zoom.ZoomAuthAttempt

/**
 * Used for accessing individual Zoom (first time) authentication attempts
 * @author Mikko Hilpinen
 * @since 17.6.2021, v0.1
 */
object DbZoomAuthAttempt extends SingleRowModelAccess[ZoomAuthAttempt]
{
	// COMPUTED ---------------------------------
	
	private def model = ZoomAuthAttemptModel
	
	/**
	 * @return An access point to open zoom authentication attempts
	 */
	def open = DbOpenZoomAuthAttempt
	
	
	// IMPLEMENTED  -----------------------------
	
	override def factory = ZoomAuthAttemptFactory
	
	override def globalCondition = None
	
	
	// OTHER    ---------------------------------
	
	/**
	 * @param id An attempt id
	 * @return An access point to that attempt's data
	 */
	def apply(id: Int) = DbSingleZoomAuthAttempt(id)
	
	
	// NESTED   ---------------------------------
	
	object DbOpenZoomAuthAttempt extends SingleRowModelAccess[ZoomAuthAttempt]
	{
		// IMPLEMENTED  -------------------------
		
		override def factory = DbZoomAuthAttempt.factory
		
		override def globalCondition =
			Some(factory.createdAfterCondition(Now - ZoomSettings.authTimeout) && model.openCondition)
		
		
		// OTHER    -----------------------------
		
		/**
		 * @param token An authentication token
		 * @param connection Implicit DB connection
		 * @return An open zoom authentication attempt for that token, if one is still open (and not deprecated)
		 */
		def forToken(token: String)(implicit connection: Connection) =
			find(model.withToken(token).toCondition)
	}
	
	case class DbSingleZoomAuthAttempt(attemptId: Int)
		extends SingleIdModelAccess[ZoomAuthAttempt](attemptId, DbZoomAuthAttempt.factory)
	{
		// OTHER    ------------------------------
		
		/**
		 * Closes this authentication attempt, if not closed already
		 * @param connection Implicit DB Connection
		 * @return Whether an attempt was updated
		 */
		def close()(implicit connection: Connection) =
			putAttribute(model.closingTimeAttName, Now, Some(model.openCondition))
	}
}
