package com.plgdhd.userservice.dto.request

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UpdateUserRequest(

    @field:Size(min = 3, max = 30, message = "Username must be between 30 characters")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9_-]+\$",
        message = "В username только буквы, цифры, _ и -"
    )
    val username: String,

    @field:Size(max = 1000, message = "URL слишком длинный")
    val avatarUrl: String? = null,

    @field:Size(max = 500, message = "Bio не может быть длиннее 500 символов")
    val bio: String? = null,

    @field:Pattern(
        regexp = "^[A-Z]+$",
    )
    val role: String
)