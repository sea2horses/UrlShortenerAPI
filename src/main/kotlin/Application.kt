package com.lemonpie

import com.lemonpie.plugins.configureDatabases
import com.lemonpie.plugins.configureHttp
import com.lemonpie.plugins.configureSecurity
import com.lemonpie.plugins.configureSerialization
import com.lemonpie.repositories.DatabaseUserRepository
import com.lemonpie.repositories.auth.DatabaseTokenRepository
import com.lemonpie.routes.configureRouting
import com.lemonpie.services.DatabaseAuthService
import com.lemonpie.services.JWTConfig
import com.lemonpie.services.JWTService
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.Application

val dotenv = dotenv()

fun Application.module() {
    val userRepository = DatabaseUserRepository()

    val jwtService = JWTService(
        JWTConfig(
            secret = dotenv["SECRET_KEY"],
            issuer = dotenv["API_URL"],
            realm = "URL Shortener API",
            audience = "url-shortener-api",
            accessExpiration = 15 * 60 * 1000,
            refreshExpiration = 6 * 60 * 60 * 1000,
        ),
        userRepository
    )

    val tokenRepository = DatabaseTokenRepository(jwtService)
    val authService = DatabaseAuthService(userRepository, tokenRepository)

    configureDatabases()
    configureRouting(authService)
    configureSerialization()
    configureHttp()
    configureSecurity(jwtService)
}