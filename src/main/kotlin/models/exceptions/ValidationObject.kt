package com.lemonpie.models.exceptions

import com.lemonpie.models.buildBackendError
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable

const val EMAIL_REGEX = """^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$"""

typealias ValidationError = String

typealias ValidationFunction<T> = ValidationObject<T>.() -> ValidationError?
typealias FormValidationFunction = () -> Pair<String, ValidationError>?

class ValidationObject<T>(val name: String, val value: T) {
    val validationList: MutableSet<ValidationFunction<T>> = mutableSetOf()

    fun useValidation(validation: ValidationFunction<T>): ValidationObject<T> {
        validationList.add(validation)
        return this
    }

    fun validate(): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()
        validationList.forEach {
            val error = this.it()
            if (error != null) errors.add(error)
        }
        return errors
    }
}

class ValidationService(
    val objects: List<ValidationObject<Any>>,
    val formValidations: List<FormValidationFunction> = listOf(),
    val message: String
) {
    fun validate() {
        val errors: MutableMap<String, MutableList<ValidationError>> = mutableMapOf()

        objects.forEach {
            val validationErrors = it.validate()
            if (validationErrors.isNotEmpty())
                errors[it.name] = validationErrors.toMutableList()
        }

        formValidations.forEach {
            val validationError = it()
            if (validationError != null)
                if(errors[validationError.first] == null)
                    errors[validationError.first] = mutableListOf(validationError.second)
                else errors[validationError.first]?.add(validationError.second)
        }

        throw buildBackendError(status = HttpStatusCode.BadRequest, code = "validation_error", detail = message) {
            errors.forEach {
                error(it.key, it.value.joinToString())
            }
        }
    }
}

class ValidationServiceBuilder {
    private val objects: MutableList<ValidationObject<Any>> = mutableListOf()
    private val formValidations: MutableList<FormValidationFunction> = mutableListOf()

    @Suppress("UNCHECKED_CAST")
    fun <T> field(name: String, value: T, validation: ValidationObject<T>.() -> Unit) {
        val validationObject = ValidationObject(name, value)
        validationObject.validation()
        objects.add(validationObject as ValidationObject<Any>)
    }

    fun refine(validation: FormValidationFunction) {
        formValidations.add(validation)
    }


    fun build(message: String) = ValidationService(objects, formValidations, message = message)
}

fun buildValidationService(
    message: String =
        "There was an error validating your request", validation: ValidationServiceBuilder.() -> Unit
): ValidationService {
    val builder = ValidationServiceBuilder()
    builder.validation()
    return builder.build(message)
}