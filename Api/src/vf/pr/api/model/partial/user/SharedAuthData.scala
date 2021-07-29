package vf.pr.api.model.partial.user

import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ModelConvertible
import utopia.flow.generic.ValueConversions._
import utopia.flow.time.Now

import java.time.Instant

/**
 * Contains information about a case where a user has provided their Zoom or Google account to be used by the
 * whole organization (leadership)
 * @author Mikko Hilpinen
 * @since 29.7.2021, v0.2
 * @param accountOwnerId Id of the user who owns the 3rd party account
 * @param serviceId Id of the service being shared
 * @param organizationId Id of the organization to which this account is provided
 * @param created Start time of this sharing (default = Now)
 * @param deprecatedAfter End time of this sharing, if ended (default = None)
 */
case class SharedAuthData(accountOwnerId: Int, serviceId: Int, organizationId: Int, created: Instant = Now,
                          deprecatedAfter: Option[Instant] = None)
	extends ModelConvertible
{
	override def toModel = Model(Vector("account_owner_id" -> accountOwnerId,
		"service_id" -> serviceId, "organization_id" -> organizationId, "created" -> created,
		"deprecated_after" -> deprecatedAfter))
}