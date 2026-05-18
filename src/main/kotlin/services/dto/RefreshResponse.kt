package com.lemonpie.services.dto

import kotlinx.serialization.Serializable

@Serializable
data class RefreshResponse(val access: String, val refresh: String)