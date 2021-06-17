package vf.pr.api.model.partial.zoom

import utopia.flow.time.Now
import utopia.flow.time.TimeExtensions._

import java.time.Instant

/**
 * Contains basic information about a Zoom session token, which is a temporary token used for
 * authenticating user sessions
 * @author Mikko Hilpinen
 * @since 17.6.2021, v0.1
 * @param refreshTokenId Id of the refresh token used when acquiring this session token
 * @param value String value of this token
 * @param created Creation time of this token (default = Now)
 * @param expiration Expiration time of this token (default = within 1 hour)
 */
case class ZoomSessionTokenData(refreshTokenId: Int, value: String, created: Instant = Now,
                                expiration: Instant = Now + 1.hours)
