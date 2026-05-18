package com.lemonpie.repositories

import java.util.UUID

interface Repository<T, ID> {
    suspend fun findAll(): List<T>
    suspend fun findById(id: ID): T?
}

interface RepositoryUUID<T> : Repository<T, UUID>
