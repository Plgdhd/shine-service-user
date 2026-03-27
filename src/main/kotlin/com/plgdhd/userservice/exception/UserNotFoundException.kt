package com.plgdhd.userservice.exception

class UserNotFoundException(userId: String):
        RuntimeException("User $userId not found")
