package vf.pr.api.database.model.user

import utopia.citadel.database.model.DeprecatableAfter
import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.model.template.DataInserter
import vf.pr.api.database.factory.user.SharedAuthFactory
import vf.pr.api.model.partial.user.SharedAuthData
import vf.pr.api.model.stored.user.SharedAuth

import java.time.Instant

object SharedAuthModel
	extends DataInserter[SharedAuthModel, SharedAuth, SharedAuthData] with DeprecatableAfter[SharedAuthModel]
{
	// COMPUTED ----------------------------
	
	/**
	 * @return The factory used by this model type
	 */
	def factory = SharedAuthFactory
	
	
	// IMPLEMENTED  -----------------------
	
	override def table = factory.table
	
	override def apply(data: SharedAuthData) =
		apply(None, Some(data.accountOwnerId), Some(data.serviceId), Some(data.organizationId), Some(data.created),
			data.deprecatedAfter)
	
	override protected def complete(id: Value, data: SharedAuthData) = SharedAuth(id.getInt, data)
	
	override def withDeprecatedAfter(deprecation: Instant) = apply(deprecatedAfter = Some(deprecation))
}

/**
 * Used for interacting with shared 3rd party authentication cases in the DB
 * @author Mikko Hilpinen
 * @since 29.7.2021, v0.2
 */
case class SharedAuthModel(id: Option[Int] = None, accountOwnerId: Option[Int] = None, serviceId: Option[Int] = None,
                           organizationId: Option[Int] = None, created: Option[Instant] = None,
                           deprecatedAfter: Option[Instant] = None)
	extends StorableWithFactory[SharedAuth]
{
	import SharedAuthModel._
	
	override def factory = SharedAuthModel.factory
	
	override def valueProperties = Vector("id" -> id, "accountOwnerId" -> accountOwnerId,
		"sharedServiceId" -> serviceId, "organizationId" -> organizationId, "created" -> created,
		deprecationAttName -> deprecatedAfter)
}