package com.lemonpie.services.dto

import com.lemonpie.models.auth.User
import com.lemonpie.serializers.UUIDField
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: UUIDField,
    val firstName: String,
    val lastName: String,
    val email: String,
) {
    companion object {
        fun fromModel(user: User): UserResponse =
            UserResponse(
                id=user.id,
                firstName=user.firstName,
                lastName=user.lastName,
                email=user.email
            )
    }
}