package vf.pr.api.model.error

/**
 * Thrown when a required setting is missing
 * @author Mikko Hilpinen
 * @since 6.6.2021, v0.1
 */
class MissingSettingException(category: String, field: String)
	extends Exception(s"Required setting $category/$field is missing")
