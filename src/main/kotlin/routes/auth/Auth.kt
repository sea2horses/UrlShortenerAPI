package com.lemonpie.routes.auth

import com.lemonpie.services.AuthService
import com.lemonpie.services.dto.LoginRequest
import com.lemonpie.services.dto.RefreshRequest
import com.lemonpie.services.dto.RegisterRequest
import com.lemonpie.services.dto.receiveWithValidation
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureAuth(authService: AuthService) {
    routing {
        route("/auth") {
            post("/register") {
                val request = call.receiveWithValidation<RegisterRequest>()
                val response = authService.register(request)

                if (response == null) call.respond(HttpStatusCode.BadRequest)
                else call.respond(status=HttpStatusCode.OK, message=response)
            }

            post("/login") {
                val request = call.receiveWithValidation<LoginRequest>()
                val response = authService.login(request.email, request.password)

                if (response == null) call.respond(HttpStatusCode.BadRequest)
                else call.respond(status=HttpStatusCode.OK, message=response)
            }

            post("/refresh") {
                val request = call.receive<RefreshRequest>()
                val response = authService.refresh(request)

                if (response == null) call.respond(HttpStatusCode.BadRequest)
                else call.respond(status = HttpStatusCode.OK, message=response)
            }
        }
    }
}