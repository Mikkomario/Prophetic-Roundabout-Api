package vf.pr.api.database.access.single.setting

import utopia.bunnymunch.jawn.JsonBunny
import utopia.flow.caching.multi.Cache
import utopia.flow.datastructure.immutable.Value
import utopia.flow.util.CollectionExtensions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.single.model.distinct.UniqueModelAccess
import vf.pr.api.database.factory.SettingFactory
import vf.pr.api.database.model.SettingModel
import vf.pr.api.model.error.MissingSettingException
import vf.pr.api.model.stored.Setting
import vf.pr.api.util.Globals._
import vf.pr.api.util.Log

import scala.concurrent.duration.Duration

/**
 * A common trait for database setting access points
 * @author Mikko Hilpinen
 * @since 6.6.2021, v0.1
 * @param category Common category of the settings read through this access
 * @param fieldCacheDurations Cache durations used for different fields. Fields that are not specified here are
 *                            not cached.
 */
class SettingsAccess(category: String, fieldCacheDurations: Map[String, Duration] = Map())
{
	// ATTRIBUTES -----------------------
	
	private val cache = Cache.expiring[String, Value] { field =>
		connectionPool.tryWith { implicit connection =>
			Access(field).value
		}.getOrMap { error =>
			Log.withoutConnection("SettingsAccess.cache", error = Some(error))
			Value.empty
		}
	} { (k, v) =>
		// Doesn't cache empty values
		if (v.isEmpty) Duration.Zero else fieldCacheDurations.getOrElse(k, Duration.Zero)
	}
	
	
	// OTHER    -------------------------
	
	/**
	 * @param field Field to read
	 * @return Value stored for that field
	 */
	def apply(field: String) = cache(field)
	
	/**
	 * Accesses a required field
	 * @param field Field to access
	 * @param convert Function to convert value to an item. Returns none if value doesn't contain an item of that type.
	 * @tparam A Type of item returned on success
	 * @return Specified item if available, otherwise a failure
	 */
	def required[A](field: String)(convert: Value => Option[A]) =
		convert(apply(field)).toTry { new MissingSettingException(category, field) }
	/**
	 * @param field Field to access
	 * @return Value of that field as a non-empty string. Failure if not defined.
	 */
	def requiredString(field: String) = required(field) { _.string.filter { _.nonEmpty } }
	
	
	// NESTED   -------------------------
	
	private object Access extends SingleRowModelAccess[Setting]
	{
		// ATTRIBUTES   -----------------
		
		override val globalCondition = Some(model.withCategory(category).toCondition)
		
		
		// COMPUTED ---------------------
		
		private def model = SettingModel
		
		
		// IMPLEMENTED  -----------------
		
		override def factory = SettingFactory
		
		
		// OTHER    ---------------------
		
		/**
		 * @param field A field name
		 * @return An access point to that field / setting
		 */
		def apply(field: String) = FieldAccess(field)
		
		
		// NESTED   ---------------------
		
		case class FieldAccess(field: String) extends UniqueModelAccess[Setting]
		{
			// COMPUTED -----------------
			
			override def condition = Access.mergeCondition(model.withField(field))
			
			override def factory = Access.factory
			
			/**
			 * @param connection DB connection (implicit)
			 * @return Value of this setting / field
			 */
			def value(implicit connection: Connection) = pullAttribute(model.valueAttName).string match
			{
				case Some(json) => JsonBunny.sureMunch(json)
				case None => Value.empty
			}
		}
	}
}
