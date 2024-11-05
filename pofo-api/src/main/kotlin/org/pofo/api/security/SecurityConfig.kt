package org.pofo.api.security

import org.pofo.api.security.authentication.local.LocalAuthenticationFailureHandler
import org.pofo.api.security.authentication.local.LocalAuthenticationFilter
import org.pofo.api.security.authentication.local.LocalAuthenticationService
import org.pofo.api.security.authentication.local.LocalAuthenticationSuccessHandler
import org.pofo.api.security.authentication.rememberMe.RememberMeAuthenticationService
import org.pofo.api.security.authentication.rememberMe.RememberMeCookieProperties
import org.pofo.domain.security.SessionPersistentRepository
import org.pofo.domain.user.UserRepository
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.RememberMeAuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutFilter
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(RememberMeCookieProperties::class)
class SecurityConfig(
    private val rememberMeCookieProperties: RememberMeCookieProperties,
) {
    @Bean
    fun filterChain(
        http: HttpSecurity,
        localAuthenticationFilter: LocalAuthenticationFilter,
        rememberMeAuthenticationFilter: RememberMeAuthenticationFilter,
    ): SecurityFilterChain =
        http
            .cors { httpSecurityCorsConfigurer ->
                httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource())
            }.csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .headers { headerConfigs -> headerConfigs.frameOptions { it.disable() } }
            .authorizeHttpRequests {
                it
                    .requestMatchers(PathRequest.toH2Console())
                    .permitAll()
                    .requestMatchers("/user")
                    .permitAll()
                    .requestMatchers("/graphql")
                    .permitAll()
                    .requestMatchers("/graphiql")
                    .permitAll()
                    .anyRequest()
                    .permitAll()
            }.addFilterAfter(
                localAuthenticationFilter,
                LogoutFilter::class.java,
            ).addFilterAfter(
                rememberMeAuthenticationFilter,
                UsernamePasswordAuthenticationFilter::class.java,
            ).build()

    @Bean
    fun rememberMeAuthenticationService(
        cookieProperties: RememberMeCookieProperties,
        userRepository: UserRepository,
        sessionPersistentRepository: SessionPersistentRepository,
    ): RememberMeAuthenticationService =
        RememberMeAuthenticationService(
            key = cookieProperties.key,
            cookieName = cookieProperties.name,
            userRepository = userRepository,
            sessionPersistentRepository = sessionPersistentRepository,
        )

    @Bean
    fun rememberMeAuthenticationFilter(
        authenticationManager: AuthenticationManager,
        rememberMeAuthenticationService: RememberMeAuthenticationService,
    ): RememberMeAuthenticationFilter {
        val rememberMeAuthenticationFilter =
            RememberMeAuthenticationFilter(
                authenticationManager,
                rememberMeAuthenticationService,
            )
        return rememberMeAuthenticationFilter
    }

    @Bean
    fun localAuthenticationFilter(
        authenticationManager: AuthenticationManager,
        authenticationSuccessHandler: LocalAuthenticationSuccessHandler,
        authenticationFailureHandler: LocalAuthenticationFailureHandler,
        rememberMeAuthenticationService: RememberMeAuthenticationService,
    ): LocalAuthenticationFilter {
        val filter =
            LocalAuthenticationFilter(AntPathRequestMatcher("/auth/local", HttpMethod.POST.name()))
        filter.setAuthenticationManager(authenticationManager)
        filter.setAuthenticationSuccessHandler(authenticationSuccessHandler)
        filter.setAuthenticationFailureHandler(authenticationFailureHandler)
        filter.setSecurityContextRepository(HttpSessionSecurityContextRepository())
        filter.rememberMeServices = rememberMeAuthenticationService
        return filter
    }

    @Bean
    fun authenticationManager(
        userDetailsService: LocalAuthenticationService,
        passwordEncoder: PasswordEncoder,
    ): AuthenticationManager {
        val localAuthenticationProvider = DaoAuthenticationProvider()
        localAuthenticationProvider.setUserDetailsService(userDetailsService)
        localAuthenticationProvider.setPasswordEncoder(passwordEncoder)

        val rememberMeAuthenticationProvider = RememberMeAuthenticationProvider(rememberMeCookieProperties.key)

        return ProviderManager(localAuthenticationProvider, rememberMeAuthenticationProvider)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val corsConfiguration = CorsConfiguration()
        corsConfiguration.allowedOrigins = listOf("*")
        corsConfiguration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        corsConfiguration.allowedHeaders = listOf("*")
        corsConfiguration.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfiguration)
        return source
    }
}
