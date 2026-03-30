package com.plgdhd.userservice.controller

import com.plgdhd.userservice.common.UserRole
import com.plgdhd.userservice.dto.request.UpdateUserRequest
import com.plgdhd.userservice.dto.response.UserPrivateResponse
import com.plgdhd.userservice.dto.response.UserPublicResponse
import com.plgdhd.userservice.model.User
import com.plgdhd.userservice.service.UserService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.OAuth2Token
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/me")
    fun getCurrentUser(@AuthenticationPrincipal jwt: Jwt):
            ResponseEntity<UserPrivateResponse> {

        val response = userService.getCurrentProfile(jwt)

        return ResponseEntity.ok(response)
    }

    @PutMapping("/me")
    fun updateCurrentUser(
        @AuthenticationPrincipal jwt: Jwt,
        @Valid @RequestBody request: UpdateUserRequest
    ): ResponseEntity<UserPrivateResponse> {

        val response = userService.updateCurrentProfile(jwt, request);

        return ResponseEntity.ok(response)
    }

    @GetMapping("/{userId}/profile")
    fun getPublicUser(@PathVariable userId: String):
            ResponseEntity<UserPublicResponse> {

        val user = userService.getPublicProfile(userId);

        return ResponseEntity.ok(user)
    }

    @GetMapping("/{username}/profile")
    fun getPublicUserByUsername(@PathVariable username: String):
            ResponseEntity<UserPublicResponse> {

        val user = userService.getPublicProfileByUsername(username);
        return ResponseEntity.ok(user)
    }

    @GetMapping("{userId}/internal/role")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SERVICE')")
    fun getUserRole(@PathVariable userId: String):
            ResponseEntity<String> {

        val user = userService.getPublicProfile(userId)

        return ResponseEntity.ok(user.role)
    }

}