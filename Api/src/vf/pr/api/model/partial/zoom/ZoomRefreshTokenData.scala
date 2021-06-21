package vf.pr.api.model.partial.zoom

import utopia.flow.time.{Now, Today}
import utopia.flow.time.TimeExtensions._

import java.time.{Instant, LocalDate}

/**
 * Contains basic information about a zoom refresh token
 * @author Mikko Hilpinen
 * @since 16.6.2021, v0.1
 * @param userId Id of the user that owns this token
 * @param value Authentication token (string)
 * @param scope The scope of this token, which determines the access rights (default = empty)
 * @param created Creation / acquisition time of this token (default = Now)
 * @param expiration Expiration date of this token (default = after 15 years)
 */
case class ZoomRefreshTokenData(userId: Int, value: String, scope: String = "",
                                created: Instant = Now, expiration: LocalDate = Today + 15.years)
