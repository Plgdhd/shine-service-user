package com.plgdhd.userservice.common

enum class UserRole {
    VIEWER,
    STREAMER,
    MODERATOR,
    ADMIN;

    companion object {
        fun fromString(value: String): UserRole =
            entries.find{ it.name == value.uppercase() } ?: UserRole.VIEWER
    }
}