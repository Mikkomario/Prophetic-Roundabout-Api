package vf.pr.api.model.stored.zoom

import utopia.vault.model.template.Stored
import vf.pr.api.model.partial.zoom.ZoomAuthAttemptData

/**
 * Represents a stored attempt to authenticate (first time) to Zoom
 * @author Mikko Hilpinen
 * @since 16.6.2021, v0.1
 */
case class ZoomAuthAttempt(id: Int, data: ZoomAuthAttemptData) extends Stored[ZoomAuthAttemptData, Int]
