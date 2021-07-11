package vf.pr.api.database.access.single.zoom

import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import vf.pr.api.database.factory.zoom.ZoomSessionTokenFactory
import vf.pr.api.database.model.zoom.ZoomRefreshTokenModel
import vf.pr.api.model.stored.zoom.ZoomSessionToken

/**
 * Used for accessing individual zoom session tokens in the DB
 * @author Mikko Hilpinen
 * @since 25.6.2021, v0.1
 */
object DbZoomSessionToken extends SingleRowModelAccess[ZoomSessionToken]
{
	// COMPUTED ----------------------------------
	
	private def refreshTokenModel = ZoomRefreshTokenModel
	
	
	// IMPLEMENTED  ------------------------------
	
	override def factory = ZoomSessionTokenFactory
	
	override def globalCondition = Some(factory.nonDeprecatedCondition)
	
	
	// OTHER    ----------------------------------
	
	/**
	 * @param userId A user id
	 * @param connection DB Connection (implicit)
	 * @return An active session token for that user id, if available
	 */
	def forUserWithId(userId: Int)(implicit connection: Connection) =
		factory.getWithJoin(refreshTokenModel.table, mergeCondition(refreshTokenModel.withUserId(userId).toCondition))
}
