package org.pofo.api.security

import org.pofo.api.security.exception.handler.CommonAccessDeniedHandler
import org.pofo.api.security.exception.handler.CommonAuthenticationEntryPoint
import org.pofo.api.security.jwt.JwtAuthenticationFilter
import org.pofo.api.security.oauth2.OAuth2AuthenticationFailureHandler
import org.pofo.api.security.oauth2.OAuth2AuthenticationService
import org.pofo.api.security.oauth2.OAuth2AuthenticationSuccessHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val environment: Environment,
) {
    @Value("\${pofo.domain}")
    private lateinit var domain: String

    @Bean
    fun filterChain(
        http: HttpSecurity,
        jwtAuthenticationFilter: JwtAuthenticationFilter,
        oAuth2AuthenticationService: OAuth2AuthenticationService,
        oAuth2AuthenticationSuccessHandler: OAuth2AuthenticationSuccessHandler,
        oAuth2AuthenticationFailureHandler: OAuth2AuthenticationFailureHandler,
    ): SecurityFilterChain {
        val isDevOrLocal = environment.activeProfiles.contains("local") || environment.activeProfiles.contains("dev")

        http {
            cors {
                configurationSource = corsConfigurationSource()
            }
            csrf { disable() }
            headers { frameOptions { sameOrigin = true } }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            formLogin { disable() }
            httpBasic { disable() }
            authorizeHttpRequests {
                if (isDevOrLocal) {
                    authorize(AntPathRequestMatcher("/h2-console/**"), permitAll)
                    authorize("/swagger-ui/**", permitAll)
                    authorize("/v3/api-docs/**", permitAll)
                    authorize("/graphiql", permitAll)
                }
                authorize(AntPathRequestMatcher("/graphql"), permitAll)
                authorize(HttpMethod.POST, "/user", permitAll)
                authorize(HttpMethod.POST, "/user/login", permitAll)
                authorize(HttpMethod.POST, "/user/logout", permitAll)
                authorize("/tech-stack/**", permitAll)
                authorize(anyRequest, authenticated)
            }
            exceptionHandling {
                authenticationEntryPoint = CommonAuthenticationEntryPoint()
                accessDeniedHandler = CommonAccessDeniedHandler()
            }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtAuthenticationFilter)
            oauth2Login {
                authorizationEndpoint { baseUri = "/user/oauth2-login" }
                redirectionEndpoint { baseUri = "/user/oauth2-login/callback/**" }
                userInfoEndpoint { userService = oAuth2AuthenticationService }
                authenticationSuccessHandler = oAuth2AuthenticationSuccessHandler
                authenticationFailureHandler = oAuth2AuthenticationFailureHandler
                permitAll = true
            }
        }
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    fun corsConfigurationSource(): CorsConfigurationSource {
        val corsConfiguration = CorsConfiguration()
        corsConfiguration.allowedOrigins = listOf(domain)
        corsConfiguration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        corsConfiguration.allowedHeaders = listOf("*")
        corsConfiguration.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfiguration)
        return source
    }
}
