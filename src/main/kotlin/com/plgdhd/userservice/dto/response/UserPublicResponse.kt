package com.plgdhd.userservice.dto.response

import java.util.UUID

data class UserPublicResponse(

    val id: UUID,

    val username: String,

    val avatarUrl: String,

    val bio: String?,

    val role: String,

    val createdAt: String
)