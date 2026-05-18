package com.lemonpie.models.auth

import com.lemonpie.serializers.UUIDField
import kotlinx.serialization.Serializable

@Serializable
class User(
    val id: UUIDField,
    val firstName: String,
    val lastName: String,
    val email: String,
    val passwordHash: ByteArray
) {
}