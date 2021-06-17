package vf.pr.api.database.model.zoom

import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.model.template.DataInserter
import vf.pr.api.database.factory.zoom.ZoomSessionTokenFactory
import vf.pr.api.model.partial.zoom.ZoomSessionTokenData
import vf.pr.api.model.stored.zoom.ZoomSessionToken

import java.time.Instant

object ZoomSessionTokenModel extends DataInserter[ZoomSessionTokenModel, ZoomSessionToken, ZoomSessionTokenData]
{
	// COMPUTED ---------------------------------
	
	/**
	 * @return The factory used by this model
	 */
	def factory = ZoomSessionTokenFactory
	
	
	// IMPLEMENTED  -----------------------------
	
	override def table = factory.table
	
	override def apply(data: ZoomSessionTokenData) = apply(None,
		Some(data.refreshTokenId), Some(data.value), Some(data.created), Some(data.expiration))
	
	override protected def complete(id: Value, data: ZoomSessionTokenData) = ZoomSessionToken(id.getInt, data)
}

/**
 * Used for interacting with Zoom session tokens in the DB
 * @author Mikko Hilpinen
 * @since 17.6.2021, v0.1
 */
case class ZoomSessionTokenModel(id: Option[Int] = None, refreshTokenId: Option[Int] = None,
                                 token: Option[String] = None, created: Option[Instant] = None,
                                 expiration: Option[Instant] = None)
	extends StorableWithFactory[ZoomSessionToken]
{
	override def factory = ZoomSessionTokenModel.factory
	
	override def valueProperties = Vector("id" -> id, "refreshTokenId" -> refreshTokenId, "token" -> token,
		"created" -> created, "expiration" -> expiration)
}