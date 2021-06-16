package vf.pr.api.model.stored.zoom

import utopia.vault.model.template.Stored
import vf.pr.api.model.partial.zoom.ZoomAuthTokenData

/**
 * Represents a user's authentication token that has been stored to DB
 * @author Mikko Hilpinen
 * @since 16.6.2021, v0.1
 */
case class ZoomAuthToken(id: Int, data: ZoomAuthTokenData) extends Stored[ZoomAuthTokenData, Int]
