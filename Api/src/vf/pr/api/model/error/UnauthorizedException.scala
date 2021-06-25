package vf.pr.api.model.error

/**
 * Exceptions thrown when an action isn't authorized
 * @author Mikko Hilpinen
 * @since 25.6.2021, v0.1
 */
class UnauthorizedException(message: String) extends Exception(message)
