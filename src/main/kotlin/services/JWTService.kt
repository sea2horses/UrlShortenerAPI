package com.lemonpie.services

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.lemonpie.repositories.UserRepository
import io.ktor.server.auth.jwt.JWTCredential
import io.ktor.server.auth.jwt.JWTPrincipal
import java.util.Date
import java.util.UUID

data class JWTConfig(
    val secret: String,
    val issuer: String,
    val realm: String,
    val audience: String,
    val accessExpiration: Int,
    val refreshExpiration: Int
)

class JWTService(config: JWTConfig, val userRepository: UserRepository) {
    private val secret = config.secret
    private val issuer = config.issuer
    private val realm = config.realm
    private val audience = config.audience
    private val accessExpiration = config.accessExpiration
    private val refreshExpiration = config.refreshExpiration

    private val algorithm = Algorithm.HMAC256(secret)

    fun getRealm() = realm

    fun createAccessToken(userId: UUID) = generateToken(userId, accessExpiration)
    fun createRefreshToken(userId: UUID) = generateToken(userId, refreshExpiration)

    private fun generateToken(userId: UUID, expireIn: Int): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("userId", userId.toString())
            .withExpiresAt(
                Date(System.currentTimeMillis() + expireIn)
            )
            .sign(algorithm)
    }

    val verifier: JWTVerifier =
        JWT.require(algorithm).withAudience(audience).withIssuer(issuer).withClaimPresence("userId").build()

    suspend fun validator(credential: JWTCredential): JWTPrincipal? {
        try {
            val userId = credential.payload.getClaim("userId").asString() ?: return null
            val id = UUID.fromString(userId) ?: return null
            userRepository.findById(id) ?: return null

            return if (credential.payload.audience.contains(audience)) {
                JWTPrincipal(credential.payload)
            } else {
                null
            }
        } catch(_: IllegalArgumentException) {
            return null
        }
    }
}