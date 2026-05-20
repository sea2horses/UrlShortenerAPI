package com.lemonpie.services

import at.favre.lib.crypto.bcrypt.BCrypt
import com.lemonpie.models.buildBackendError
import com.lemonpie.repositories.UserRepository
import com.lemonpie.repositories.auth.TokenRepository
import com.lemonpie.services.dto.LoginResponse
import com.lemonpie.services.dto.RefreshRequest
import com.lemonpie.services.dto.RefreshResponse
import com.lemonpie.services.dto.RegisterRequest
import com.lemonpie.services.dto.UserResponse
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import java.util.UUID

interface AuthService {
    val userRepository: UserRepository
    val tokenRepository: TokenRepository
    suspend fun register(request: RegisterRequest): LoginResponse?
    suspend fun login(email: String, password: String): LoginResponse?
    suspend fun refresh(request: RefreshRequest): RefreshResponse?
}

class DatabaseAuthService(override val userRepository: UserRepository,
                          override val tokenRepository: TokenRepository) : AuthService {
    fun makePassword(password: String): ByteArray = BCrypt.withDefaults().hash(12, password.toCharArray())
    fun checkPassword(password: String, hash: ByteArray): Boolean = BCrypt.verifyer().verify(password.toCharArray(), hash).verified

    suspend fun makeTokens(userId: UUID): Pair<String, String>? {
        val accessToken = tokenRepository.newAccess(userId)
        val refreshToken = tokenRepository.newRefresh(userId)

        println("Access token created: $accessToken")
        println("Refresh token created: $refreshToken")
        if (accessToken == null || refreshToken == null) return null
        return Pair(accessToken, refreshToken)
    }

    override suspend fun refresh(request: RefreshRequest): RefreshResponse? = suspendTransaction {
        val pair = tokenRepository.doRefresh(request.refresh, request.userId) ?: return@suspendTransaction null
        RefreshResponse(pair.first, pair.second)
    }

    override suspend fun register(request: RegisterRequest): LoginResponse? = suspendTransaction {
        println("Register action, checking email...")
        val exists = userRepository.findByEmail(request.email) != null
        if (exists) {
            throw buildBackendError(
                code = "existing_email",
                detail = "There was an error creating the user"
            ) {
                error("email", "A user with this email already exists")
            }
        }

        println("Register action, creating user...")
        val newUser = userRepository.add(
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            passwordHash = makePassword(request.password)
        )

        // Create new access token
        println("Register action, making tokens...")
        val tokens = makeTokens(newUser.id)

        if (tokens == null) {
            TransactionManager.current().rollback()
            return@suspendTransaction null
        }

        println("All was done!")
        return@suspendTransaction LoginResponse(tokens.first, tokens.second, UserResponse.fromModel(newUser))
    }

    override suspend fun login(email: String, password: String): LoginResponse? = suspendTransaction {
        val user = userRepository.findByEmail(email) ?:
            throw buildBackendError("account_not_found", "The email is not associated to any account")

        if(!checkPassword(password, user.passwordHash))
            throw buildBackendError("incorrect_password", "The provided password is incorrect")

        // Create new access token
        val tokens = makeTokens(user.id) ?: return@suspendTransaction null
        return@suspendTransaction LoginResponse(tokens.first, tokens.second, UserResponse.fromModel(user))
    }

    suspend fun logout(refresh: String): Boolean = tokenRepository.removeRefresh(refresh)
}
