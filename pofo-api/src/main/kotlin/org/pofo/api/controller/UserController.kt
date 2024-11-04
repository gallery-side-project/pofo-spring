package org.pofo.api.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.pofo.api.dto.RegisterRequest
import org.pofo.api.security.CurrentUser
import org.pofo.api.service.UserService
import org.pofo.domain.user.User
import org.springframework.http.ResponseEntity
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
        request: HttpServletRequest,
        response: HttpServletResponse,
        @RequestBody registerRequest: RegisterRequest,
    ): ResponseEntity<*> {
        val user = userService.createUser(registerRequest)
        return ResponseEntity.ok(user)
    }

    @GetMapping("/me")
    fun getMe(
        @CurrentUser user: User,
    ): ResponseEntity<*> = ResponseEntity.ok(user)
}
