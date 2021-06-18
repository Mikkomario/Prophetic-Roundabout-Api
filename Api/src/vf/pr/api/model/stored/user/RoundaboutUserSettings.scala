package vf.pr.api.model.stored.user

import utopia.metropolis.model.stored.StoredModelConvertible
import vf.pr.api.model.partial.user.RoundaboutUserSettingsData

/**
 * Represents a stored Roundabout-specific set of user settings
 * @author Mikko Hilpinen
 * @since 18.6.2021, v0.1
 */
case class RoundaboutUserSettings(id: Int, data: RoundaboutUserSettingsData)
	extends StoredModelConvertible[RoundaboutUserSettingsData]
