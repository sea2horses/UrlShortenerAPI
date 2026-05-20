package com.lemonpie.services.dto

import com.lemonpie.models.BackendException
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive

interface Dto {
    @Throws(BackendException::class)
    fun validate()
}

suspend inline fun<reified T: Dto> ApplicationCall.receiveWithValidation(): T {
    val body = this.receive<T>()
    body.validate()
    return body
}