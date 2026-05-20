package com.lemonpie.models.exceptions

import java.util.UUID

fun ValidationObject<String>.required(): ValidationObject<String> {
    return useValidation {
        if (value.isBlank())
            "Field is required"
        else null
    }
}

fun ValidationObject<String>.email(): ValidationObject<String> {
    val regex = Regex(EMAIL_REGEX)

    return useValidation {
        if (!regex.matches(value))
            "Must be a valid email"
        else null
    }
}

fun ValidationObject<String>.min(minimum: Int): ValidationObject<String> {
    return useValidation {
        if (value.length < minimum)
            "Must be at least $minimum characters long"
        else null
    }
}

fun ValidationObject<String>.max(maximum: Int): ValidationObject<String> {
    return useValidation {
        if (value.length > maximum)
            "Must be at most $maximum characters long"
        else null
    }
}

fun ValidationObject<String>.uuid(): ValidationObject<String> {
    return useValidation {
        try {
            UUID.fromString(value)
            null
        } catch (_: IllegalArgumentException) {
            "Must be a valid UUID"
        }
    }
}
