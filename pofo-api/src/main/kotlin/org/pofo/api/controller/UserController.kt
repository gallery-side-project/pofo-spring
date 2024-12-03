package org.pofo.api.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.pofo.api.common.response.ApiResponse
import org.pofo.api.dto.RegisterRequest
import org.pofo.api.security.annotation.CurrentUser
import org.pofo.api.service.UserService
import org.pofo.common.exception.CustomException
import org.pofo.common.exception.ErrorCode
import org.pofo.domain.domain.user.User
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
        request: HttpServletRequest,
        response: HttpServletResponse,
        @RequestBody registerRequest: RegisterRequest,
    ): ApiResponse<User> {
        val user = userService.createUser(registerRequest)
        return ApiResponse.success(user)
    }

    @GetMapping("/me")
    fun getMe(
        @CurrentUser user: User,
    ): ApiResponse<User> {
        return ApiResponse.success(user)
    }
}
