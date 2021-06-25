package vf.pr.api.rest.extensions.user

import utopia.access.http.Method.Get
import utopia.exodus.rest.util.AuthorizedContext
import utopia.nexus.http.Path
import utopia.nexus.rest.LeafResource

/**
 * A rest node used for accessing the authorized user's upcoming and current meetings
 * @author Mikko Hilpinen
 * @since 25.6.2021, v0.1
 */
object MyMeetingsNode extends LeafResource[AuthorizedContext]
{
	// ATTRIBUTES   ---------------------------
	
	override val name = "meetings"
	
	override val allowedMethods = Vector(Get)
	
	
	// IMPLEMENTED  ---------------------------
	
	// TODO: Implement
	override def toResponse(remainingPath: Option[Path])(implicit context: AuthorizedContext) = ???
}
