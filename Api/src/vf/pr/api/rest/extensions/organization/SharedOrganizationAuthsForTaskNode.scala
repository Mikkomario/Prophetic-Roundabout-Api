package vf.pr.api.rest.extensions.organization

import utopia.access.http.Method.Get
import utopia.access.http.Status.Unauthorized
import utopia.ambassador.database.AuthDbExtensions._
import utopia.ambassador.rest.util.AuthUtils
import utopia.citadel.database.access.many.DbUsers
import utopia.citadel.database.access.single.DbUser
import utopia.citadel.database.access.single.organization.DbTask
import utopia.exodus.rest.util.AuthorizedContext
import utopia.flow.caching.multi.Cache
import utopia.flow.datastructure.immutable.{Constant, Model, Value}
import utopia.flow.generic.ValueConversions._
import utopia.metropolis.model.enumeration.ModelStyle.{Full, Simple}
import utopia.nexus.http.Path
import utopia.nexus.rest.LeafResource
import utopia.nexus.result.Result
import utopia.vault.database.Connection
import vf.pr.api.database.access.many.user.DbSharedAuths

/**
 * Used for acquiring information about 3rd authentications that have been shared to be used across the organization.
 * This node targets a specific task.
 * @author Mikko Hilpinen
 * @since 29.7.2021, v0.2
 */
case class SharedOrganizationAuthsForTaskNode(organizationId: Int, taskId: Int) extends LeafResource[AuthorizedContext]
{
	// IMPLEMENTED  -------------------------------
	
	override def name = taskId.toString
	
	override def allowedMethods = Vector(Get)
	
	override def toResponse(remainingPath: Option[Path])(implicit context: AuthorizedContext) =
	{
		context.sessionKeyAuthorized { (session, connection) =>
			implicit val c: Connection = connection
			val userId = session.userId
			
			// Makes sure the user is a member of the target organization
			if (DbUser(userId).isMemberInOrganizationWithId(organizationId))
			{
				// Reads shared authentications
				val auths = DbSharedAuths.forOrganizationWithId(organizationId).pull
				if (auths.isEmpty)
					Result.Success(Vector[Value]())
				else
				{
					// Reads the task scopes that require access
					val taskScopesByServiceId = DbTask(taskId).scopes.pull.groupBy { _.serviceId }
					// Retrieves user scopes only when necessary
					val userScopes = Cache { userId: Int => DbUser(userId).accessibleScopeIds }
					// Only returns authentications that provide access to the target scopes
					val applicableAuths = auths.filter { auth =>
						taskScopesByServiceId.get(auth.serviceId).forall { taskScopes =>
							AuthUtils.testTaskAccess(taskScopes, userScopes(auth.accountOwnerId))
						}
					}
					
					if (applicableAuths.nonEmpty)
					{
						// Includes account owner information in the responses
						val ownerSettings = DbUsers(auths.map { _.accountOwnerId }.toSet).settings
						val models = session.modelStyle match
						{
							case Simple => applicableAuths.map { auth =>
								val ownerModel = ownerSettings.find { _.userId == auth.accountOwnerId }
									.map { _.toSimpleModel }.getOrElse(Model.empty)
								Model(Vector("service_id" -> auth.serviceId, "owner" -> ownerModel))
							}
							case Full => applicableAuths.map { auth =>
								val ownerModel = ownerSettings
									.find { _.userId == auth.accountOwnerId }.map { _.toModel }.getOrElse(Model.empty)
								auth.toModel + Constant("owner_settings", ownerModel)
							}
						}
						Result.Success(models)
					}
					else
						Result.Success(Vector[Value]())
				}
			}
			else
				Result.Failure(Unauthorized, s"You're not a member of organization $organizationId")
		}
	}
}
