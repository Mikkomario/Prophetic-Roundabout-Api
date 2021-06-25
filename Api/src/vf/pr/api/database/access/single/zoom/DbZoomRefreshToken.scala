package vf.pr.api.database.access.single.zoom

import utopia.flow.generic.ValueConversions._
import utopia.flow.time.Now
import utopia.flow.time.TimeExtensions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.{LatestModelAccess, SingleIdModelAccess, SingleRowModelAccess}
import vf.pr.api.database.factory.zoom.ZoomRefreshTokenFactory
import vf.pr.api.database.model.zoom.{ZoomRefreshTokenModel, ZoomSessionTokenModel}
import vf.pr.api.model.partial.zoom.ZoomSessionTokenData
import vf.pr.api.model.stored.zoom.ZoomRefreshToken

import java.time.Instant

/**
 * Used for accessing individual Zoom refresh tokens in DB
 * @author Mikko Hilpinen
 * @since 17.6.2021, v0.1
 */
object DbZoomRefreshToken extends SingleRowModelAccess[ZoomRefreshToken]
{
	// COMPUTED ----------------------------------
	
	private def model = ZoomRefreshTokenModel
	
	private def sessionTokenModel = ZoomSessionTokenModel
	
	
	// IMPLEMENTED  ------------------------------
	
	override def factory = ZoomRefreshTokenFactory
	
	override def globalCondition = Some(factory.nonDeprecatedCondition)
	
	
	// OTHER    ----------------------------------
	
	/**
	 * @param tokenId Id of the targeted zoom refresh token
	 * @return An access point to that token
	 */
	def apply(tokenId: Int) = DbSingleZoomRefreshToken(tokenId)
	
	/**
	 * @param userId A user id
	 * @return An access point to that user's refresh token
	 */
	def forUserWithId(userId: Int) = DbZoomUserRefreshToken(userId)
	
	
	// NESTED   ----------------------------------
	
	case class DbSingleZoomRefreshToken(tokenId: Int)
		extends SingleIdModelAccess[ZoomRefreshToken](tokenId, DbZoomRefreshToken.factory)
	{
		/**
		 * Starts a new zoom session based on this refresh token
		 * @param sessionToken Acquired session token
		 * @param expiration Expiration time for the session token (default = within 55 minutes)
		 * @param connection DB Connection (implicit)
		 * @return Newly inserted session token
		 */
		def startSession(sessionToken: String, expiration: Instant = Now + 55.minutes)
		                (implicit connection: Connection) =
			sessionTokenModel.insert(ZoomSessionTokenData(tokenId, sessionToken, expiration = expiration))
	}
	
	case class DbZoomUserRefreshToken(userId: Int) extends LatestModelAccess[ZoomRefreshToken]
	{
		// COMPUTED   ----------------------------
		
		private def condition = DbZoomRefreshToken.mergeCondition(model.withUserId(userId))
		
		
		// IMPLEMENTED  --------------------------
		
		override def factory = DbZoomRefreshToken.factory
		
		override def globalCondition = Some(condition)
	}
}
