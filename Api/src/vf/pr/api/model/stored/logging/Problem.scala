package vf.pr.api.model.stored.logging

import utopia.vault.model.template.Stored
import vf.pr.api.model.partial.logging.ProblemData

/**
 * Represents a recorded problem
 * @author Mikko Hilpinen
 * @since 6.6.2021, v0.1
 */
case class Problem(id: Int, data: ProblemData) extends Stored[ProblemData, Int]
