package com.lemonpie.models

import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class BackendErrorResponse(
    val code: String,
    val detail: String,
    @SerialName("field_errors")
    val fieldErrors: Map<String, String> = emptyMap()
)

class BackendException(
    val status: HttpStatusCode = HttpStatusCode.BadRequest,
    val code: String,
    val detail: String,
    val fieldErrors: Map<String, String> = emptyMap()
) : RuntimeException(detail) {
    fun toResponse() = BackendErrorResponse(code, detail, fieldErrors)
}

class BackendErrorBuilder internal constructor(
    private val fieldErrors: MutableMap<String, String>
) {
    fun error(fieldName: String, message: String) {
        fieldErrors[fieldName] = message
    }
}

fun buildBackendError(
    code: String,
    detail: String,
    status: HttpStatusCode = HttpStatusCode.BadRequest,
    action: BackendErrorBuilder.() -> Unit = {}
): BackendException {
    val fieldErrors = mutableMapOf<String, String>()
    val builder = BackendErrorBuilder(fieldErrors)
    builder.action()
    return BackendException(status, code, detail, fieldErrors)
}
