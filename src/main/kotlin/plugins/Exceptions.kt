package com.lemonpie.plugins

import com.lemonpie.models.BackendErrorResponse
import com.lemonpie.models.BackendException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

fun Application.configureExceptions() {
    install(StatusPages) {
        exception<BackendException> { call, cause ->
            call.respond(cause.status, cause.toResponse())
        }

        exception<BadRequestException> { call, _ ->
            call.respond(
                HttpStatusCode.BadRequest,
                BackendErrorResponse(
                    code = "bad_request",
                    detail = "The request body is invalid"
                )
            )
        }
    }
}
