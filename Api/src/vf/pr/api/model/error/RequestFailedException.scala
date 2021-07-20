package vf.pr.api.model.error

/**
 * Exceptions thrown when external requests fail
 * @author Mikko Hilpinen
 * @since 17.6.2021, v0.1
 */
@deprecated("Replaced with another exception with the same name in Disciple", "v0.2")
class RequestFailedException(message: String) extends Exception(message)
