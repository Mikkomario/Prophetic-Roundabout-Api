package vf.pr.api.model.partial.zoom

import utopia.flow.time.Now

import java.time.Instant

/**
 * Contains basic information about an attempted first time authentication to Zoom
 * @author Mikko Hilpinen
 * @since 16.6.2021, v0.1
 * @param userId Id of the user that attempts Zoom authentication
 * @param token A token representing this authentication attempt
 * @param created Creation time of this authentication attempt
 * @param closed Time when this authentication attempt was closed / finished (successfully)
 */
@deprecated("Replaced with the Ambassador dependency", "v0.2")
case class ZoomAuthAttemptData(userId: Int, token: String, created: Instant = Now, closed: Option[Instant] = None)
