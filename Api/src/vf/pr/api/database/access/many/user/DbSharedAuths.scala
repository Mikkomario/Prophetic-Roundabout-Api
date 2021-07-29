package vf.pr.api.database.access.many.user

import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.{NonDeprecatedView, SubView}
import vf.pr.api.database.factory.user.SharedAuthFactory
import vf.pr.api.database.model.user.SharedAuthModel
import vf.pr.api.model.stored.user.SharedAuth

/**
 * Used for accessing multiple authentication shares at a time
 * @author Mikko Hilpinen
 * @since 29.7.2021, v0.2
 */
object DbSharedAuths extends ManyRowModelAccess[SharedAuth] with NonDeprecatedView[SharedAuth]
{
	// COMPUTED ---------------------------------
	
	private def model = SharedAuthModel
	
	
	// IMPLEMENTED  -----------------------------
	
	override def factory = SharedAuthFactory
	override protected def defaultOrdering = None
	
	
	// OTHER    ---------------------------------
	
	/**
	 * @param organizationId Id of the target organization
	 * @return An access point to that organization's
	 */
	def forOrganizationWithId(organizationId: Int) = new DbOrganizationSharedAuths(organizationId)
	
	
	// NESTED   ---------------------------------
	
	class DbOrganizationSharedAuths(val organizationId: Int) extends ManyRowModelAccess[SharedAuth] with SubView
	{
		// IMPLEMENTED  -------------------------
		
		override protected def parent = DbSharedAuths
		
		override def filterCondition = model.withOrganizationId(organizationId).toCondition
		
		override protected def defaultOrdering = parent.defaultOrdering
		override def factory = parent.factory
	}
}
