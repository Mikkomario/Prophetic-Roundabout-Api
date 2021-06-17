package vf.pr.api.database.access.single.zoom

import utopia.vault.nosql.access.{LatestModelAccess, SingleRowModelAccess}
import vf.pr.api.database.factory.zoom.ZoomRefreshTokenFactory
import vf.pr.api.database.model.zoom.ZoomRefreshTokenModel
import vf.pr.api.model.stored.zoom.ZoomRefreshToken

/**
 * Used for accessing individual Zoom refresh tokens in DB
 * @author Mikko Hilpinen
 * @since 17.6.2021, v0.1
 */
object DbZoomRefreshToken extends SingleRowModelAccess[ZoomRefreshToken]
{
	// COMPUTED ----------------------------------
	
	private def model = ZoomRefreshTokenModel
	
	
	// IMPLEMENTED  ------------------------------
	
	override def factory = ZoomRefreshTokenFactory
	
	override def globalCondition = Some(factory.nonDeprecatedCondition)
	
	
	// OTHER    ----------------------------------
	
	/**
	 * @param userId A user id
	 * @return An access point to that user's refresh token
	 */
	def forUserWithId(userId: Int) = DbZoomUserRefreshToken(userId)
	
	
	// NESTED   ----------------------------------
	
	case class DbZoomUserRefreshToken(userId: Int) extends LatestModelAccess[ZoomRefreshToken]
	{
		// COMPUTED   ----------------------------
		
		private def condition = DbZoomRefreshToken.mergeCondition(model.withUserId(userId))
		
		
		// IMPLEMENTED  --------------------------
		
		override def factory = DbZoomRefreshToken.factory
		
		override def globalCondition = Some(condition)
	}
}
