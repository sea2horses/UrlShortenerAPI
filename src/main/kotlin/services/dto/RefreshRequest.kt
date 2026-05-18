package com.lemonpie.services.dto

import com.lemonpie.serializers.UUIDField
import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequest(val userId: UUIDField, val refresh: String)