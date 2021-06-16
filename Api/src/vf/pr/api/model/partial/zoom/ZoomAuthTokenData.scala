package vf.pr.api.model.partial.zoom

import utopia.flow.time.Now
import utopia.flow.time.TimeExtensions._
import vf.pr.api.model.enumeration.TokenType
import vf.pr.api.model.enumeration.TokenType.SessionToken

import java.time.Instant

/**
 * Contains basic information about a zoom authentication token
 * @author Mikko Hilpinen
 * @since 16.6.2021, v0.1
 * @param userId Id of the user that owns this token
 * @param value Authentication token (string)
 * @param tokenType Type of this token (session / refresh) (default = session)
 * @param created Creation / acquisition time of this token (default = Now)
 * @param expiration Expiration time of this token (default = after 1 hour)
 */
case class ZoomAuthTokenData(userId: Int, value: String, tokenType: TokenType = SessionToken,
                             created: Instant = Now, expiration: Instant = Now + 1.hours)
