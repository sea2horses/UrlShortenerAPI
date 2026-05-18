package com.lemonpie.models.auth

import com.lemonpie.serializers.UUIDField
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class RefreshToken(
    val token: String,
    val user: UUIDField,
)