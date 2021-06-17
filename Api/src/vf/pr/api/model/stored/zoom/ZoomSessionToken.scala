package vf.pr.api.model.stored.zoom

import utopia.vault.model.template.Stored
import vf.pr.api.model.partial.zoom.ZoomSessionTokenData

/**
 * Represents a Zoom session token that has been stored to DB
 * @author Mikko Hilpinen
 * @since 17.6.2021, v0.1
 */
case class ZoomSessionToken(id: Int, data: ZoomSessionTokenData) extends Stored[ZoomSessionTokenData, Int]
