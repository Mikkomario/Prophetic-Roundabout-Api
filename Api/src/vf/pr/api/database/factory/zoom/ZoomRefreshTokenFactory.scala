package vf.pr.api.database.factory.zoom

import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.flow.generic.ValueUnwraps._
import utopia.flow.time.Today
import utopia.vault.nosql.factory.row.FromRowFactoryWithTimestamps
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import utopia.vault.nosql.template.Deprecatable
import utopia.vault.sql.SqlExtensions._
import vf.pr.api.database.RoundaboutTables
import vf.pr.api.model.partial.zoom.ZoomRefreshTokenData
import vf.pr.api.model.stored.zoom.ZoomRefreshToken

/**
 * Used for reading Zoom refresh tokens from the database
 * @author Mikko Hilpinen
 * @since 16.6.2021, v0.1
 */
@deprecated("Replaced with the Ambassador dependency", "v0.2")
object ZoomRefreshTokenFactory extends FromValidatedRowModelFactory[ZoomRefreshToken]
	with FromRowFactoryWithTimestamps[ZoomRefreshToken] with Deprecatable
{
	// ATTRIBUTES   ------------------------------
	
	private val expirationAttName = "expiration"
	
	
	// IMPLEMENTED  ------------------------------
	
	override def table = RoundaboutTables.zoomRefreshToken
	
	override def creationTimePropertyName = "created"
	
	override def nonDeprecatedCondition = table("deprecatedAfter").isNull && table(expirationAttName) > Today.toValue
	
	override protected def fromValidatedModel(model: Model[Constant]) = ZoomRefreshToken(model("id"),
		ZoomRefreshTokenData(model("userId"), model("token"), model("created"), model(expirationAttName)))
}
