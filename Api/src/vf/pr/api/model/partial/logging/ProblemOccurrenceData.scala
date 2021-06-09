package vf.pr.api.model.partial.logging

import utopia.flow.time.Now

import java.time.Instant

/**
 * Contains basic information about a case when a problem occurred
 * @author Mikko Hilpinen
 * @since 6.6.2021, v0.1
 * @param problemId Id of the occurred problem
 * @param message Logged message (optional)
 * @param stack Stack trace of the linked error (optional)
 * @param created Timestamp of this problem occurrence
 */
case class ProblemOccurrenceData(problemId: Int, message: Option[String] = None, stack: Option[String] = None,
                                 created: Instant = Now)
