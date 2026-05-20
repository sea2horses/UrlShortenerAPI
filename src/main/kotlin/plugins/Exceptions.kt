package com.lemonpie.plugins

import com.lemonpie.models.BackendErrorResponse
import com.lemonpie.models.BackendException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException

@OptIn(ExperimentalSerializationApi::class)
fun Application.configureExceptions() {
    install(StatusPages) {
        exception<BackendException> { call, cause ->
            call.respond(cause.status, cause.toResponse())
        }

        exception<BadRequestException> { call, cause ->

            val missingFieldException = cause.cause?.cause as? MissingFieldException

            if (missingFieldException != null) {
                val errors = missingFieldException.missingFields.associateWith { "Field is required" }

                call.respond(HttpStatusCode.BadRequest, BackendErrorResponse(
                    code = "validation_error",
                    detail = "There was an error validating your request",
                    fieldErrors = errors
                ))

                return@exception
            }

            call.respond(
                HttpStatusCode.BadRequest,
                BackendErrorResponse(
                    code = "bad_request",
                    detail = cause.message ?: "The request body is invalid"
                )
            )
        }
    }
}
