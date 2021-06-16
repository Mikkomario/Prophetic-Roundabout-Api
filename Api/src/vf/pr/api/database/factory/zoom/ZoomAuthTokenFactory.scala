package vf.pr.api.database.factory.zoom

import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.flow.generic.ValueUnwraps._
import utopia.flow.time.Now
import utopia.vault.nosql.factory.{Deprecatable, FromValidatedRowModelFactory}
import utopia.vault.sql.Extensions._
import vf.pr.api.database.Tables
import vf.pr.api.model.enumeration.TokenType
import vf.pr.api.model.enumeration.TokenType.SessionToken
import vf.pr.api.model.partial.zoom.ZoomAuthTokenData
import vf.pr.api.model.stored.zoom.ZoomAuthToken

/**
 * Used for reading Zoom authentication tokens from the database
 * @author Mikko Hilpinen
 * @since 16.6.2021, v0.1
 */
object ZoomAuthTokenFactory extends FromValidatedRowModelFactory[ZoomAuthToken] with Deprecatable
{
	// ATTRIBUTES   ------------------------------
	
	private val expirationAttName = "expiration"
	
	
	// IMPLEMENTED  ------------------------------
	
	override def table = Tables.zoomAuthToken
	
	override def nonDeprecatedCondition = table(expirationAttName) > Now.toValue
	
	override protected def fromValidatedModel(model: Model[Constant]) = ZoomAuthToken(model("id"),
		ZoomAuthTokenData(model("userId"), model("token"), TokenType.forId(model("tokenType")).getOrElse(SessionToken),
			model("created"), model(expirationAttName)))
}
