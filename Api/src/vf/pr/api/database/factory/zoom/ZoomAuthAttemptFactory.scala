package vf.pr.api.database.factory.zoom

import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.flow.generic.ValueUnwraps._
import utopia.vault.nosql.factory.FromValidatedRowModelFactory
import vf.pr.api.database.Tables
import vf.pr.api.model.partial.zoom.ZoomAuthAttemptData
import vf.pr.api.model.stored.zoom.ZoomAuthAttempt

/**
 * Used for reading Zoom authentication attempt data from DB
 * @author Mikko Hilpinen
 * @since 16.6.2021, v0.1
 */
object ZoomAuthAttemptFactory extends FromValidatedRowModelFactory[ZoomAuthAttempt]
{
	override def table = Tables.zoomAuthAttempt
	
	override protected def fromValidatedModel(model: Model[Constant]) = ZoomAuthAttempt(model("id"),
		ZoomAuthAttemptData(model("userId"), model("token"), model("created"), model("closed")))
}
