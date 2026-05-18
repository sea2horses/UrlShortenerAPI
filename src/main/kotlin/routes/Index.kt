package com.lemonpie.routes

import com.lemonpie.routes.auth.configureAuth
import com.lemonpie.services.AuthService
import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting(authService: AuthService) {
    routing {
        get("/health") {
            call.respondText("OK")
        }
    }

    configureAuth(authService)
}