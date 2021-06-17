package vf.pr.api.model.stored.zoom

import utopia.vault.model.template.Stored
import vf.pr.api.model.partial.zoom.ZoomRefreshTokenData

/**
 * Represents a user's Zoom authentication refresh token that has been stored to DB
 * @author Mikko Hilpinen
 * @since 16.6.2021, v0.1
 */
case class ZoomRefreshToken(id: Int, data: ZoomRefreshTokenData) extends Stored[ZoomRefreshTokenData, Int]
