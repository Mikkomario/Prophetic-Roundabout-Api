package vf.pr.api.rest.data

import utopia.access.http.Method.Get
import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ValueConversions._
import utopia.flow.util.CollectionExtensions._
import utopia.flow.util.StringExtensions._
import utopia.nexus.http.Path
import utopia.nexus.rest.{Context, LeafResource}
import utopia.nexus.result.Result

import java.util.TimeZone

/**
 * Used for informing the client of all available time zones
 * @author Mikko Hilpinen
 * @since 19.6.2021, v0.1
 */
object TimeZonesNode extends LeafResource[Context]
{
	override val name = "time-zones"
	
	override val allowedMethods = Vector(Get)
	
	// Simply returns an array of the available time zone ids (strings)
	override def toResponse(remainingPath: Option[Path])(implicit context: Context) =
	{
		// Groups the time zones based on the first portion
		// (E.g. Europe/Helsinki first portion is Europe and second is Helsinki)
		// Time zones without two parts (E.g. UTC) are handled separately
		val (singleParts, multiParts) = TimeZone.getAvailableIDs.toVector.dividedWith { timeZone =>
			if (timeZone.contains('/'))
				Right(timeZone.splitAtFirst("/"))
			else
				Left(timeZone)
		}
		val multiPartsByOrigin = multiParts.asMultiMap
		
		// Converts the resulting time zones into a model with two properties:
		// - regional: [{ region: String, zones: [String] }]
		// - other: [String]
		val regionalValues = multiPartsByOrigin.map { case (origin, zones) =>
			Model(Vector("region" -> origin, "zones" -> zones))
		}.toVector
		Result.Success(Model(Vector("regional" -> regionalValues, "other" -> singleParts))).toResponse
	}
}
