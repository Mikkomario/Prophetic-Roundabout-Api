package vf.pr.api.database.factory.user

import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.flow.generic.ValueUnwraps._
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import utopia.vault.nosql.template.Deprecatable
import vf.pr.api.database.RoundaboutTables
import vf.pr.api.database.model.user.SharedAuthModel
import vf.pr.api.model.partial.user.SharedAuthData
import vf.pr.api.model.stored.user.SharedAuth

/**
 * Used for reading shared 3rd party authentications from the DB
 * @author Mikko Hilpinen
 * @since 29.7.2021, v0.2
 */
object SharedAuthFactory extends FromValidatedRowModelFactory[SharedAuth] with Deprecatable
{
	// COMPUTED -------------------------------
	
	private def model = SharedAuthModel
	
	
	// IMPLEMENTED  ---------------------------
	
	override def table = RoundaboutTables.userAuthShare
	
	override def nonDeprecatedCondition = model.nonDeprecatedCondition
	
	override protected def fromValidatedModel(model: Model[Constant]) = SharedAuth(model("id"),
		SharedAuthData(model("accountOwnerId"), model("sharedServiceId"), model("organizationId"), model("created"),
			model("deprecatedAfter")))
}
