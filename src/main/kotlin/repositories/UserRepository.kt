package com.lemonpie.repositories

import com.lemonpie.mapping.auth.UserEntity
import com.lemonpie.mapping.auth.UserTable
import com.lemonpie.models.auth.User
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.UUID

interface UserRepository : RepositoryUUID<User> {
    suspend fun findByEmail(email: String): User?
    suspend fun add(firstName: String, lastName: String, email: String, passwordHash: ByteArray): User
    suspend fun update(id: UUID, firstName: String, lastName: String, email: String, passwordHash: ByteArray): User?
    suspend fun delete(id: UUID): Boolean
}

class DatabaseUserRepository : UserRepository {
    override suspend fun findAll(): List<User> = transaction {
        UserEntity.all().map { it.toModel() }
    }

    override suspend fun findById(id: UUID): User? = transaction {
        UserEntity.findById(id)?.toModel()
    }

    override suspend fun findByEmail(email: String): User? = transaction {
        UserEntity.find {
            (UserTable.email eq email)
        }.limit(1).map { it.toModel() }.firstOrNull()
    }

    override suspend fun add(firstName: String, lastName: String,
                             email: String, passwordHash: ByteArray): User
    = transaction {
        val user = UserEntity.new {
            this.fistName = firstName
            this.lastName = lastName
            this.email = email
            this.passwordHash = passwordHash
        }

        user.toModel()
    }

    override suspend fun delete(id: UUID): Boolean = transaction {
        UserEntity.findById(id)?.delete() != null
    }

    override suspend fun update(id: UUID, firstName: String, lastName: String,
                       email: String, passwordHash: ByteArray): User? = transaction {
        UserEntity.findById(id)?.apply {
            this.fistName = firstName
            this.lastName = lastName
            this.email = email
            this.passwordHash = passwordHash
        }?.toModel()
    }

}