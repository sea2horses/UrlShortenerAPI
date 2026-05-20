package com.lemonpie.services.dto

import com.lemonpie.models.exceptions.ValidationService.*
import com.lemonpie.models.exceptions.buildValidationService
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
) : Dto {

    override fun validate() {
        buildValidationService {
            field("email", email) {
                required()
                email()
                max(32)
            }

            field("password", password) {
                required()
                min(8)
                max(32)
            }
        }.validate()
    }
}