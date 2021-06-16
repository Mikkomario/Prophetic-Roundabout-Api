package vf.pr.api.database.model.zoom

import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.model.template.DataInserter
import vf.pr.api.database.Tables
import vf.pr.api.database.factory.zoom.ZoomAuthTokenFactory
import vf.pr.api.model.enumeration.TokenType
import vf.pr.api.model.partial.zoom.ZoomAuthTokenData
import vf.pr.api.model.stored.zoom.ZoomAuthToken

import java.time.Instant

object ZoomAuthTokenModel extends DataInserter[ZoomAuthTokenModel, ZoomAuthToken, ZoomAuthTokenData]
{
	// COMPUTED ----------------------------------
	
	/**
	 * @return The factory used by this model
	 */
	def factory = ZoomAuthTokenFactory
	
	
	// IMPLEMENTED  ------------------------------
	
	override def table = Tables.zoomAuthToken
	
	override def apply(data: ZoomAuthTokenData) = apply(None, Some(data.userId), Some(data.value),
		Some(data.tokenType), Some(data.created), Some(data.expiration))
	
	override protected def complete(id: Value, data: ZoomAuthTokenData) = ZoomAuthToken(id.getInt, data)
}

/**
 * Used for interacting with Zoom authentication tokens in the DB
 * @author Mikko Hilpinen
 * @since 16.6.2021, v0.1
 */
case class ZoomAuthTokenModel(id: Option[Int] = None, userId: Option[Int] = None, value: Option[String] = None,
                              tokenType: Option[TokenType] = None, created: Option[Instant] = None,
                              expires: Option[Instant] = None)
	extends StorableWithFactory[ZoomAuthToken]
{
	override def factory = ZoomAuthTokenModel.factory
	
	override def valueProperties = Vector("id" -> id, "userId" -> userId, "token" -> value,
		"tokenType" -> tokenType.map { _.id }, "created" -> created, "expiration" -> expires)
}