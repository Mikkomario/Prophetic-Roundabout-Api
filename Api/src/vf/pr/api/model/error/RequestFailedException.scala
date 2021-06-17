package vf.pr.api.model.error

/**
 * Exceptions thrown when external requests fail
 * @author Mikko Hilpinen
 * @since 17.6.2021, v0.1
 */
class RequestFailedException(message: String) extends Exception(message)
