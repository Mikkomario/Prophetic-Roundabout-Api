package vf.pr.api.database.model.zoom

import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.flow.time.Now
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
	
	/**
	 * @return A model that has just been marked as deprecated
	 */
	def nowDeprecated = withDeprecatedAfter(Now)
	
	
	// IMPLEMENTED  ------------------------------
	
	override def table = RoundaboutTables.zoomRefreshToken
	
	override def apply(data: ZoomRefreshTokenData) = apply(None, Some(data.userId),
		Some(data.value), Some(data.created), Some(data.expiration))
	
	override protected def complete(id: Value, data: ZoomRefreshTokenData) = ZoomRefreshToken(id.getInt, data)
	
	
	// OTHER    ----------------------------------
	
	/**
	 * @param tokenId A refresh token id
	 * @return A model with that id
	 */
	def withId(tokenId: Int) = apply(Some(tokenId))
	/**
	 * @param userId A user id
	 * @return A model with that user id
	 */
	def withUserId(userId: Int) = apply(userId = Some(userId))
	/**
	 * @param deprecation A token deprecation time
	 * @return A model with that deprecation time set
	 */
	def withDeprecatedAfter(deprecation: Instant) = apply(deprecatedAfter = Some(deprecation))
}

/**
 * Used for interacting with Zoom authentication tokens in the DB
 * @author Mikko Hilpinen
 * @since 16.6.2021, v0.1
 */
case class ZoomRefreshTokenModel(id: Option[Int] = None, userId: Option[Int] = None, value: Option[String] = None,
                                 created: Option[Instant] = None, expiration: Option[LocalDate] = None,
                                 deprecatedAfter: Option[Instant] = None)
	extends StorableWithFactory[ZoomRefreshToken]
{
	// COMPUTED -----------------------------------
	
	/**
	 * @return A copy of this model that has just been marked as deprecated
	 */
	def nowDeprecated = copy(deprecatedAfter = Some(Now))
	
	
	// IMPLEMENTED  -------------------------------
	
	override def factory = ZoomRefreshTokenModel.factory
	
	override def valueProperties = Vector("id" -> id, "userId" -> userId, "token" -> value,
		"created" -> created, "expiration" -> expiration, "deprecatedAfter" -> deprecatedAfter)
}