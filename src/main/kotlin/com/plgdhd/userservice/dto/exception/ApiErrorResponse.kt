package com.plgdhd.userservice.dto.exception

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiErrorResponse(

    val status: Int,

    val error: String,

    val message: String,

    val path: String? = null,

    val timestamp: Instant = Instant.now(),

    val errors: Map<String, String>? = null
) {

    companion object {
        fun of(status: Int,
               error: String,
               message: String
        ) = ApiErrorResponse(status, error, message)

        fun of(status: Int,
               error: String,
               message: String,
               path: String
        ) = ApiErrorResponse(status, error, message, path)


        fun withErrors(status: Int,
                       error: String,
                       message: String,
                       path: String,
                       errors: Map<String, String>
        ) = ApiErrorResponse(status, error, message, path, errors = errors)
    }
}


