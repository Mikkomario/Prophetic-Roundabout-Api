package vf.pr.api.database

import utopia.vault.model.immutable.Table
import vf.pr.api.util.Globals
import vf.pr.api.util.Globals.executionContext

/**
 * An access point to all database tables in this project
 * @author Mikko Hilpinen
 * @since 6.6.2021, v0.1
 */
object Tables extends utopia.vault.database.Tables(Globals.connectionPool)
{
	// ATTRIBUTES   -----------------------------
	
	/**
	 * Name of the database used in this project
	 */
	val databaseName = "prophetic_roundabout_db"
	
	
	// COMPUTED ---------------------------------
	
	/**
	 * @return A table that contains settings
	 */
	def setting = apply("setting")
	/**
	 * @return A table that records server side problems
	 */
	def problem = apply("problem")
	/**
	 * @return A table that records server side problem occurrences
	 */
	def problemOccurrence = apply("problem_occurrence")
	
	
	// OTHER    ---------------------------------
	
	private def apply(tableName: String): Table = apply(databaseName, tableName)
}
