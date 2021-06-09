package vf.pr.api.model.partial.logging

import utopia.flow.time.Now
import vf.pr.api.model.enumeration.Severity
import vf.pr.api.model.enumeration.Severity.Problem

import java.time.Instant

/**
 * Contains basic information about an error log
 * @author Mikko Hilpinen
 * @since 6.6.2021, v0.1
 * @param context  Context in which this problem occurred
 * @param severity Severity of this problem (default = Problem)
 * @param created  Time when this problem first occurred / was logged
 */
case class ProblemData(context: String, severity: Severity = Problem, created: Instant = Now)
