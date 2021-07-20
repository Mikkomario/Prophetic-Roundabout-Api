package vf.pr.api.rest.zoom

import utopia.exodus.rest.resource.NotImplementedResource
import utopia.exodus.rest.util.AuthorizedContext
import utopia.nexus.rest.ResourceWithChildren

/**
 * An access point to zoom-related nodes
 * @author Mikko Hilpinen
 * @since 16.6.2021, v0.1
 */
@deprecated("Replaced with the Ambassador dependency", "v0.2")
object ZoomNode extends ResourceWithChildren[AuthorizedContext] with NotImplementedResource[AuthorizedContext]
{
	override def children = Vector(ZoomLoginNode)
	
	override def name = "zoom"
}
