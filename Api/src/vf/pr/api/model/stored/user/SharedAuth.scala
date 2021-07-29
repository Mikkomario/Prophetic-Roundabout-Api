package vf.pr.api.model.stored.user

import utopia.vault.model.template.Stored
import vf.pr.api.model.partial.user.SharedAuthData

/**
 * Represents an 3rd party account sharing that has been stored to the DB
 * @author Mikko Hilpinen
 * @since 29.7.2021, v0.2
 */
case class SharedAuth(id: Int, data: SharedAuthData) extends Stored[SharedAuthData, Int]
