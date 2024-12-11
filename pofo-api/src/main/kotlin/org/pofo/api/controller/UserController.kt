package org.pofo.api.controller

import org.pofo.api.common.response.ApiResponse
import org.pofo.api.dto.LoginRequest
import org.pofo.api.dto.RegisterRequest
import org.pofo.api.dto.TokenResponse
import org.pofo.api.security.PrincipalDetails
import org.pofo.api.service.UserService
import org.pofo.domain.domain.user.User
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService,
) {
    @PostMapping("")
    fun register(
        @RequestBody registerRequest: RegisterRequest,
    ): ApiResponse<User> {
        val user = userService.createUser(registerRequest)
        return ApiResponse.success(user)
    }

    @GetMapping("/me")
    fun getMe(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
    ): ApiResponse<User> {
        val user = userService.getUserById(principalDetails.userId)
        return ApiResponse.success(user)
    }

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ApiResponse<TokenResponse> {
        val tokenResponse = userService.login(loginRequest)
        return ApiResponse.success(tokenResponse)
    }
}
