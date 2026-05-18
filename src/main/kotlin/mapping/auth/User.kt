package com.lemonpie.mapping.auth

import com.lemonpie.mapping.Dao
import com.lemonpie.models.auth.User
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.UUID


object UserTable : UUIDTable("users") {
    val first_name = varchar("first_name", 20)
    val last_name = varchar("last_name", 50)
    val email = varchar("email", 50).uniqueIndex()
    val password_hash = binary("password_hash")
}

class UserEntity(id: EntityID<UUID>) : UUIDEntity(id), Dao<User> {
    companion object : UUIDEntityClass<UserEntity>(UserTable)

    override fun toModel(): User = User(id.value, fistName, lastName, email, passwordHash)

    var fistName by UserTable.first_name
    var lastName by UserTable.last_name
    var email by UserTable.email
    var passwordHash by UserTable.password_hash
}
