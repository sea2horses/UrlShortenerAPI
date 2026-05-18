package com.lemonpie.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.csrf.*
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.lemonpie.services.JWTService
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.Date
import java.util.UUID

fun Application.configureSecurity(jwtService: JWTService) {
    /*
    install(CSRF) {
        // tests Origin is an expected value
        allowOrigin("http://localhost:8080")

        // tests Origin matches Host header
        originMatchesHost()

        // custom header checks
        checkHeader("X-CSRF-Token")
    }
     */
    authentication {
        jwt {
            realm = jwtService.getRealm()
            verifier(jwtService.verifier)
            validate {
                credential -> jwtService.validator(credential)
            }
        }
    }
}