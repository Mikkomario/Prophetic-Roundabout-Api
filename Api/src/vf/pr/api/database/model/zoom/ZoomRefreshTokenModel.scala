package vf.pr.api.database.model.zoom

import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.model.template.DataInserter
import vf.pr.api.database.RoundaboutTables
import vf.pr.api.database.factory.zoom.ZoomRefreshTokenFactory
import vf.pr.api.model.partial.zoom.ZoomRefreshTokenData
import vf.pr.api.model.stored.zoom.ZoomRefreshToken

import java.time.{Instant, LocalDate}

object ZoomRefreshTokenModel extends DataInserter[ZoomRefreshTokenModel, ZoomRefreshToken, ZoomRefreshTokenData]
{
	// COMPUTED ----------------------------------
	
	/**
	 * @return The factory used by this model
	 */
	def factory = ZoomRefreshTokenFactory
	
	
	// IMPLEMENTED  ------------------------------
	
	override def table = RoundaboutTables.zoomRefreshToken
	
	override def apply(data: ZoomRefreshTokenData) = apply(None, Some(data.userId),
		Some(data.value), Some(data.scope.mkString(":")), Some(data.created), Some(data.expiration))
	
	override protected def complete(id: Value, data: ZoomRefreshTokenData) = ZoomRefreshToken(id.getInt, data)
	
	
	// OTHER    ----------------------------------
	
	/**
	 * @param userId A user id
	 * @return A model with that user id
	 */
	def withUserId(userId: Int) = apply(userId = Some(userId))
}

/**
 * Used for interacting with Zoom authentication tokens in the DB
 * @author Mikko Hilpinen
 * @since 16.6.2021, v0.1
 */
case class ZoomRefreshTokenModel(id: Option[Int] = None, userId: Option[Int] = None, value: Option[String] = None,
                                 scope: Option[String] = None, created: Option[Instant] = None,
                                 expiration: Option[LocalDate] = None)
	extends StorableWithFactory[ZoomRefreshToken]
{
	override def factory = ZoomRefreshTokenModel.factory
	
	override def valueProperties = Vector("id" -> id, "userId" -> userId, "token" -> value,
		"scope" -> scope, "created" -> created, "expiration" -> expiration)
}