package vf.pr.api.model.enumeration

/**
 * An enumeration for access token types
 * @author Mikko Hilpinen
 * @since 16.6.2021, v0.1
 */
@deprecated("Refactored token models so that this enumeration is no more needed", "v0.1")
sealed trait TokenType
{
	/**
	 * Id representing this token type
	 */
	val id: Int
}

@deprecated("Refactored token models so that this enumeration is no more needed", "v0.1")
object TokenType
{
	// ATTRIBUTES   --------------------------------
	
	/**
	 * All values of this enumeration
	 */
	val values = Vector[TokenType](SessionToken, RefreshToken)
	
	
	// OTHER    ------------------------------------
	
	/**
	 * @param typeId A token type id
	 * @return A token type matching that id. None if none of the types matched that id.
	 */
	def forId(typeId: Int) = values.find { _.id == typeId }
	
	
	// NESTED   ------------------------------------
	
	/**
	 * Session tokens are temporary and used in the primary authentication
	 */
	case object SessionToken extends TokenType { override val id = 1 }
	/**
	 * Refresh tokens are used for acquiring new session tokens
	 */
	case object RefreshToken extends TokenType { override val id = 2 }
}
