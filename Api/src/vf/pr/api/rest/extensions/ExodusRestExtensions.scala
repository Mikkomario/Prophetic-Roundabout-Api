package vf.pr.api.rest.extensions

import utopia.access.http.Method.Get
import utopia.exodus.database.access.single.DbUser
import utopia.exodus.rest.resource.organization.OrganizationNode
import utopia.exodus.rest.resource.scalable.SessionUseCaseImplementation
import utopia.exodus.rest.resource.user.{MeNode, MySettingsNode}
import utopia.flow.datastructure.immutable.Constant
import utopia.flow.generic.ValueConversions._
import utopia.nexus.rest.scalable.FollowImplementation
import utopia.nexus.result.Result
import utopia.vault.database.Connection
import vf.pr.api.database.ExodusDbExtensions._
import vf.pr.api.rest.extensions.organization.OrganizationMeetingsNode
import vf.pr.api.rest.extensions.user.{MyMeetingsNode, MyRoundaboutSettingsNode}

/**
 * Used for applying all extensions from this project to the Exodus resource set
 * @author Mikko Hilpinen
 * @since 18.6.2021, v0.1
 */
object ExodusRestExtensions
{
	private var applied = false
	
	/**
	 * Applies all available extensions to the existing exodus rest resources
	 */
	def applyAll() =
	{
		if (!applied)
		{
			applied = true
			// Adds /meetings to users/me
			MeNode.extendWith(FollowImplementation.withChild(MyMeetingsNode))
			// Adds /roundabout to users/me/settings
			MySettingsNode.extendWith(FollowImplementation.withChild(MyRoundaboutSettingsNode))
			// Adds roundabout settings (as "roundabout") to the successful GET users/me/settings response
			MySettingsNode.extendWith(SessionUseCaseImplementation(Get) { (session, connection, _, _, default) =>
				default.value match {
					case success: Result.Success =>
						implicit val c: Connection = connection
						val settings = DbUser(session.userId).roundaboutSettings.pullOrInsert()
						val isZoomAuthorized = DbUser(session.userId).zoomRefreshToken.nonEmpty
						val roundaboutSettingsModel = settings.toModel +
							Constant(MyRoundaboutSettingsNode.zoomLinkAttName, isZoomAuthorized)
						
						success.copy(data = success.data.getModel + Constant("roundabout", roundaboutSettingsModel))
					case failure: Result => failure
				}
			})
			// Adds /meetings to organizations/${id}
			OrganizationNode.addFollow { organizationId =>
				FollowImplementation.withChild(OrganizationMeetingsNode(organizationId)) }
		}
	}
}
