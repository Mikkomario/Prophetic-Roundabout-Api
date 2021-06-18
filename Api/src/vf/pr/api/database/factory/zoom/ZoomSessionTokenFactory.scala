package vf.pr.api.database.factory.zoom

import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.flow.generic.ValueUnwraps._
import utopia.flow.time.Now
import utopia.vault.nosql.factory.{Deprecatable, FromValidatedRowModelFactory}
import utopia.vault.sql.SqlExtensions._
import vf.pr.api.database.RoundaboutTables
import vf.pr.api.model.partial.zoom.ZoomSessionTokenData
import vf.pr.api.model.stored.zoom.ZoomSessionToken

/**
 * Used for reading Zoom session tokens from the DB
 * @author Mikko Hilpinen
 * @since 17.6.2021, v0.1
 */
object ZoomSessionTokenFactory extends FromValidatedRowModelFactory[ZoomSessionToken] with Deprecatable
{
	// COMPUTED ----------------------------------
	
	private val expirationAttName = "expiration"
	
	
	// IMPLEMENTED  ------------------------------
	
	override def table = RoundaboutTables.zoomSessionToken
	
	override def nonDeprecatedCondition = table(expirationAttName) > Now.toValue
	
	override protected def fromValidatedModel(model: Model[Constant]) = ZoomSessionToken(model("id"),
		ZoomSessionTokenData(model("refreshTokenId"), model("token"), model("created"), model("expiration")))
}
