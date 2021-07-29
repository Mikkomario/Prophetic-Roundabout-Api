package vf.pr.api.rest.extensions.organization

import utopia.exodus.rest.resource.NotImplementedResource
import utopia.exodus.rest.util.AuthorizedContext
import utopia.flow.generic.ValueConversions._
import utopia.nexus.http.Path
import utopia.nexus.rest.ResourceSearchResult.{Error, Follow}

/**
 * Used for accessing authentications that are shared between organization members
 * @author Mikko Hilpinen
 * @since 29.7.2021, v0.2
 */
case class SharedOrganizationAuthsNode(organizationId: Int) extends NotImplementedResource[AuthorizedContext]
{
	override def name = "shared-auths"
	
	override def follow(path: Path)(implicit context: AuthorizedContext) =
		path.head.int match
		{
			case Some(taskId) => Follow(SharedOrganizationAuthsForTaskNode(organizationId, taskId), path.tail)
			case None => Error(message = Some(s"${path.head} is not a valid task id"))
		}
}
