package com.lemonpie.services.dto

import com.lemonpie.models.exceptions.buildValidationService
import com.lemonpie.models.exceptions.*
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val confirmPassword: String
) : Dto {

    override fun validate() {
        buildValidationService {
            field("firstName", firstName) {
                required()
                min(2)
                max(32)
            }

            field("lastName", lastName) {
                required()
                min(2)
                max(32)
            }

            field("email", email) {
                required()
                email()
                max(32)
            }

            field("password", password) {
                required()
                min(8)
            }

            field("confirmPassword", confirmPassword) {
                required()
                min(8)
            }

            refine {
                if (password != confirmPassword)
                    "confirmPassword" to "Passwords do not match"
                else null
            }
        }.validate()
    }
}
