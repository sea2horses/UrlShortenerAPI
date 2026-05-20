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
                min(2)
                max(32)
            }

            field("lastName", lastName) {
                min(2)
                max(32)
            }

            field("email", email) {
                email()
                max(32)
            }

            field("password", password) {
                min(8)
            }

            field("confirmPassword", confirmPassword) {
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
