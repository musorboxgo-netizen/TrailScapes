package org.trail.scapes.controllers

import io.jsonwebtoken.JwtException
import jakarta.validation.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.net.URI

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArg(ex: IllegalArgumentException): ProblemDetail =
        problem(HttpStatus.BAD_REQUEST, "Bad request", ex.message)

    @ExceptionHandler(IllegalStateException::class, DataIntegrityViolationException::class)
    fun handleConflict(ex: Exception): ProblemDetail =
        problem(HttpStatus.CONFLICT, "Conflict", ex.message)

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCreds(ex: BadCredentialsException): ProblemDetail =
        problem(HttpStatus.UNAUTHORIZED, "Unauthorized", "Invalid credentials")

    @ExceptionHandler(AccessDeniedException::class)
    fun handleDenied(ex: AccessDeniedException): ProblemDetail =
        problem(HttpStatus.FORBIDDEN, "Forbidden", ex.message)

    @ExceptionHandler(JwtException::class)
    fun handleJwt(ex: JwtException): ProblemDetail =
        problem(HttpStatus.UNAUTHORIZED, "Invalid token", ex.message)

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(ex: NoSuchElementException): ProblemDetail =
        problem(HttpStatus.NOT_FOUND, "Not found", ex.message)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ProblemDetail {
        val pd = problem(HttpStatus.BAD_REQUEST, "Validation failed", "One or more fields are invalid")
        val errors = ex.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "invalid") }
        pd.setProperty("errors", errors)
        return pd
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraint(ex: ConstraintViolationException): ProblemDetail {
        val pd = problem(HttpStatus.BAD_REQUEST, "Constraint violation", ex.message)
        val errors = ex.constraintViolations.associate { v ->
            val field = v.propertyPath.toString()
            field to (v.message ?: "invalid")
        }
        pd.setProperty("errors", errors)
        return pd
    }

    @ExceptionHandler(Exception::class)
    fun handleOther(ex: Exception): ProblemDetail =
        problem(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", ex.message)
}

private fun problem(status: HttpStatus, title: String, detail: String?): ProblemDetail =
    ProblemDetail.forStatusAndDetail(status, detail ?: title).apply {
        this.title = title
        this.type = URI.create("about:blank")
    }