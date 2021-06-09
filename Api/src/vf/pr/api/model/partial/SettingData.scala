package vf.pr.api.model.partial

import utopia.flow.datastructure.immutable.Value

/**
 * Contains basic information about a setting
 * @author Mikko Hilpinen
 * @since 6.6.2021, v0.1
 * @param category Name of this setting's category / group
 * @param field Name of this setting's field / key
 * @param value Value assigned to this setting
 */
case class SettingData(category: String, field: String, value: Value)
