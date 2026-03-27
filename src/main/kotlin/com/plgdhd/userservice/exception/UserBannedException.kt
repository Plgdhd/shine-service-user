package com.plgdhd.userservice.exception

class UserBannedException(userId: String):
        RuntimeException("User $userId banned")