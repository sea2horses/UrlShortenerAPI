package com.lemonpie.mapping.auth

import com.lemonpie.mapping.Dao
import com.lemonpie.models.auth.RefreshToken
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.datetime.datetime

object RefreshTokenTable : IntIdTable("refresh_tokens") {
    val token = varchar("token", 1000).uniqueIndex()  // the actual refresh token string
    val userId = reference("user_id", UserTable, ReferenceOption.CASCADE) // not optional
}

class RefreshTokenEntity(id: EntityID<Int>) : IntEntity(id), Dao<RefreshToken> {
    companion object : IntEntityClass<RefreshTokenEntity>(RefreshTokenTable)

    override fun toModel(): RefreshToken = RefreshToken(token, userId.value)

    var token by RefreshTokenTable.token
    var userId by RefreshTokenTable.userId
}