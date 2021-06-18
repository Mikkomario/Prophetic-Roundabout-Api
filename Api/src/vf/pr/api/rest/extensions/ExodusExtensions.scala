package vf.pr.api.rest.extensions

import utopia.access.http.Method.Get
import utopia.exodus.database.access.single.DbUser
import utopia.exodus.rest.resource.scalable.SessionUseCaseImplementation
import utopia.exodus.rest.resource.user.MySettingsNode
import utopia.flow.datastructure.immutable.Constant
import utopia.flow.generic.ValueConversions._
import utopia.nexus.rest.scalable.FollowImplementation
import utopia.nexus.result.Result
import utopia.vault.database.Connection
import vf.pr.api.database.ExodusDbExtensions._
import vf.pr.api.rest.extensions.user.MyRoundaboutSettingsNode

/**
 * Used for applying all extensions from this project to the Exodus resource set
 * @author Mikko Hilpinen
 * @since 18.6.2021, v0.1
 */
object ExodusExtensions
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
			// Adds /roundabout to users/me/settings
			MySettingsNode.extendWith(FollowImplementation.withChild(MyRoundaboutSettingsNode))
			// Adds roundabout settings as ("roundabout") to the successful GET users/me/settings response
			MySettingsNode.extendWith(SessionUseCaseImplementation(Get) { (session, connection, _, _, default) =>
				default.value match {
					case success: Result.Success =>
						implicit val c: Connection = connection
						DbUser(session.userId).roundaboutSettings.pull match
						{
							case Some(roundaboutSettings) =>
								success.copy(data = success.data.getModel +
									Constant("roundabout", roundaboutSettings.toModel))
							case None => success
						}
					case failure: Result => failure
				}
			})
		}
	}
}
