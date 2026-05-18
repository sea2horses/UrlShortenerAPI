package com.lemonpie.services.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val access: String,
    val refresh: String,
    val user: UserResponse
)