package vf.pr.api.model.stored

import utopia.vault.model.template.Stored
import vf.pr.api.model.partial.SettingData

/**
 * Represents a setting that has been recorded to the DB
 * @author Mikko Hilpinen
 * @since 6.6.2021, v0.1
 */
case class Setting(id: Int, data: SettingData) extends Stored[SettingData, Int]
