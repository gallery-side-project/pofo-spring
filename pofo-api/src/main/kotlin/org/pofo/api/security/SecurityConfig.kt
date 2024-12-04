package org.pofo.api.security

import org.pofo.api.security.authentication.local.LocalAuthenticationFilter
import org.pofo.api.security.authentication.local.LocalAuthenticationService
import org.pofo.api.security.authentication.oauth2.OAuth2AuthenticationService
import org.pofo.api.security.authentication.rememberMe.RememberMeAuthenticationService
import org.pofo.api.security.authentication.rememberMe.RememberMeCookieProperties
import org.pofo.api.security.exception.handler.CommonAccessDeniedHandler
import org.pofo.api.security.exception.handler.CommonAuthenticationEntryPoint
import org.pofo.api.security.exception.handler.CommonAuthenticationFailureHandler
import org.pofo.api.security.exception.handler.CommonAuthenticationSuccessHandler
import org.pofo.domain.domain.security.SessionPersistentRepository
import org.pofo.domain.domain.user.UserRepository
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.RememberMeAuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationProvider
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.logout.LogoutFilter
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(RememberMeCookieProperties::class)
class SecurityConfig(
    private val rememberMeCookieProperties: RememberMeCookieProperties,
) {
    @Bean
    fun filterChain(
        http: HttpSecurity,
        localAuthenticationFilter: LocalAuthenticationFilter,
        oAuth2AuthorizationRequestRedirectFilter: OAuth2AuthorizationRequestRedirectFilter,
        oAuth2LoginAuthenticationFilter: OAuth2LoginAuthenticationFilter,
        rememberMeAuthenticationFilter: RememberMeAuthenticationFilter,
    ): SecurityFilterChain =
        http
            .cors { httpSecurityCorsConfigurer ->
                httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource())
            }.csrf { it.disable() }
            .headers { headerConfigs -> headerConfigs.frameOptions { it.disable() } }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .authorizeHttpRequests {
                it
                    .requestMatchers(PathRequest.toH2Console()).hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/user").permitAll()
                    .requestMatchers("/tech-stack/**").permitAll()
                    .requestMatchers("/graphql", "/graphiql").permitAll()
                    .anyRequest().authenticated()
            }.exceptionHandling {
                it.authenticationEntryPoint(CommonAuthenticationEntryPoint())
                it.accessDeniedHandler(CommonAccessDeniedHandler())
            }.addFilterAfter(
                localAuthenticationFilter,
                LogoutFilter::class.java,
            ).addFilterAfter(
                oAuth2AuthorizationRequestRedirectFilter,
                LocalAuthenticationFilter::class.java,
            ).addFilterAfter(
                oAuth2LoginAuthenticationFilter,
                OAuth2AuthorizationRequestRedirectFilter::class.java,
            ).addFilterAfter(
                rememberMeAuthenticationFilter,
                OAuth2LoginAuthenticationFilter::class.java,
            ).build()

    @Bean
    fun oAuth2AuthorizationRequestRedirectFilter(
        inMemoryClientRegistrationRepository: InMemoryClientRegistrationRepository,
    ): OAuth2AuthorizationRequestRedirectFilter =
        OAuth2AuthorizationRequestRedirectFilter(inMemoryClientRegistrationRepository, "/auth/oauth2")

    @Bean
    fun oAuth2LoginAuthenticationFilter(
        authenticationManager: AuthenticationManager,
        authenticationSuccessHandler: CommonAuthenticationSuccessHandler,
        authenticationFailureHandler: CommonAuthenticationFailureHandler,
        inMemoryClientRegistrationRepository: InMemoryClientRegistrationRepository,
        rememberMeAuthenticationService: RememberMeAuthenticationService,
    ): OAuth2LoginAuthenticationFilter {
        val filter =
            OAuth2LoginAuthenticationFilter(
                inMemoryClientRegistrationRepository,
                HttpSessionOAuth2AuthorizedClientRepository(),
                "/auth/oauth2/callback/*",
            )
        filter.setAuthenticationManager(authenticationManager)
        filter.setAuthenticationSuccessHandler(authenticationSuccessHandler)
        filter.setAuthenticationFailureHandler(authenticationFailureHandler)
        filter.rememberMeServices = rememberMeAuthenticationService
        return filter
    }

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
    fun localAuthenticationFilter(
        authenticationManager: AuthenticationManager,
        authenticationSuccessHandler: CommonAuthenticationSuccessHandler,
        authenticationFailureHandler: CommonAuthenticationFailureHandler,
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
        oAuth2AuthenticationService: OAuth2AuthenticationService,
        passwordEncoder: PasswordEncoder,
    ): AuthenticationManager {
        val localAuthenticationProvider = DaoAuthenticationProvider()
        localAuthenticationProvider.setUserDetailsService(userDetailsService)
        localAuthenticationProvider.setPasswordEncoder(passwordEncoder)

        val oAuth2LoginAuthenticationProvider =
            OAuth2LoginAuthenticationProvider(
                DefaultAuthorizationCodeTokenResponseClient(),
                oAuth2AuthenticationService,
            )

        val rememberMeAuthenticationProvider = RememberMeAuthenticationProvider(rememberMeCookieProperties.key)

        return ProviderManager(
            localAuthenticationProvider,
            oAuth2LoginAuthenticationProvider,
            rememberMeAuthenticationProvider,
        )
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
