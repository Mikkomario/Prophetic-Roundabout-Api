package vf.pr.api.database.access.single.setting

import utopia.nexus.http.Path

import scala.concurrent.duration.Duration

/**
 * Used for reading API-related settings from the database
 * @author Mikko Hilpinen
 * @since 6.6.2021, v0.1
 */
object ApiSettings extends SettingsAccess("api", Map(
	"address" -> Duration.Inf, "root-path" -> Duration.Inf))
{
	// COMPUTED -----------------------
	
	/**
	 * @return Server base address (E.g. "domain.com:8080/service/"). Failure if not defined.
	 */
	def address = requiredString("address")
	/**
	 * @return Server root address path
	 */
	def rootPath = apply("root-path").string.flatMap(Path.parse)
}
