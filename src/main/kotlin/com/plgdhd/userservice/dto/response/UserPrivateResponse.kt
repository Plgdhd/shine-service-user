package com.plgdhd.userservice.dto.response

import java.time.Instant
import java.util.UUID

data class UserPrivateResponse(

    val id: UUID,

    val username: String,

    val email: String,

    val avatarUrl: String? = null,

    val bio: String? = null,

    val role: String,

    val status: String,

    val createAt: Instant,

    val updatedAt: Instant
)