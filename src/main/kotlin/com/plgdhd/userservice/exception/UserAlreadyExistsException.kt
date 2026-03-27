package com.plgdhd.userservice.exception

class UserAlreadyExistsException(username: String):
        RuntimeException("User $username already exists")