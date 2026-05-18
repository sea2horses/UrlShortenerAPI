package com.lemonpie.repositories.auth

import com.lemonpie.mapping.auth.RefreshTokenEntity
import com.lemonpie.mapping.auth.RefreshTokenTable
import com.lemonpie.mapping.auth.RefreshTokenTable.userId
import com.lemonpie.mapping.auth.UserEntity
import com.lemonpie.services.JWTService
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.UUID

interface TokenRepository {
    val jwtService: JWTService
    suspend fun newAccess(userId: UUID): String?
    suspend fun doRefresh(refresh: String, userId: UUID): Pair<String, String>?
    suspend fun newRefresh(userId: UUID): String?
    suspend fun removeRefresh(refresh: String): Boolean
}

class DatabaseTokenRepository(override val jwtService: JWTService) : TokenRepository {

    override suspend fun newAccess(userId: UUID): String? {
        UserEntity.findById(userId) ?: return null
        return jwtService.createAccessToken(userId)
    }

    override suspend fun doRefresh(refresh: String, userId: UUID): Pair<String, String>? = suspendTransaction {
        val refreshEntity = RefreshTokenEntity.find { RefreshTokenTable.userId eq userId }
            .limit(1).firstOrNull()
        val refreshToken = refreshEntity?.toModel() ?: return@suspendTransaction null

        val userEntity = UserEntity.findById(userId)
        // Refresh needs to be equal
        if (userEntity == null || refreshToken.token != refresh) return@suspendTransaction null

        refreshEntity.delete()
        val newToken = RefreshTokenEntity.new {
            this.token = jwtService.createRefreshToken(userId)
            this.userId = userEntity.id
        }
        val access = newAccess(userId)

        if (access == null) {
            TransactionManager.current().rollback()
            return@suspendTransaction null
        }

        Pair(access, newToken.toModel().token)
    }

    override suspend fun newRefresh(userId: UUID) = transaction {
        val refreshEntity = RefreshTokenEntity.find { RefreshTokenTable.userId eq userId }
            .limit(1).firstOrNull()
        val refreshToken = refreshEntity?.toModel()

        val userEntity = UserEntity.findById(userId)
        println("User Entity: $userEntity")
        // Delete refresh
        refreshEntity?.delete()
        // User !!!
        if (userEntity == null) return@transaction null

        val newToken = RefreshTokenEntity.new {
            this.token = jwtService.createRefreshToken(userId)
            this.userId = userEntity.id
        }
        return@transaction newToken.toModel().token
    }

    override suspend fun removeRefresh(refresh: String): Boolean {
        val refreshEntity = RefreshTokenEntity.find { RefreshTokenTable.token eq refresh }
            .limit(1).firstOrNull()
        refreshEntity?.delete()
        return refreshEntity != null
    }
}