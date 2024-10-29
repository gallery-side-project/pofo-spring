package org.pofo.api.security

import org.pofo.domain.security.remember_me.RememberMeTokenRepository
import org.pofo.domain.user.UserRepository
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.RememberMeAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
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
@EnableConfigurationProperties(RememberMeCookieProperties::class)
class SecurityConfig(
    private val authenticationSuccessHandler: CustomAuthenticationSuccessHandler,
    private val authenticationFailureHandler: CustomAuthenticationFailureHandler,
    private val userRepository: UserRepository,
    private val tokenRepository: RememberMeTokenRepository,
    private val rememberMeCookieProperties: RememberMeCookieProperties,
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain =
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
                localAuthenticationFilter(),
                LogoutFilter::class.java,
            ).addFilterAfter(
                rememberMeAuthenticationFilter(),
                LocalAuthenticationFilter::class.java,
            ).build()

    @Bean
    fun passwordEncoder(): PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    @Bean
    fun authenticationManager(): AuthenticationManager =
        ProviderManager(
            listOf(localAuthenticationProvider(), rememberMeAuthenticationProvider()),
        )

    @Bean
    fun localAuthenticationProvider(): LocalAuthenticationProvider =
        LocalAuthenticationProvider(
            userRepository = userRepository,
            passwordEncoder = passwordEncoder(),
        )

    @Bean
    fun localAuthenticationFilter(): LocalAuthenticationFilter {
        val requestMatcher = AntPathRequestMatcher("/auth/login", HttpMethod.POST.name())
        val filter = LocalAuthenticationFilter(requestMatcher)
        filter.setAuthenticationManager(authenticationManager())
        filter.setAuthenticationSuccessHandler(authenticationSuccessHandler)
        filter.setAuthenticationFailureHandler(authenticationFailureHandler)
        filter.setSecurityContextRepository(HttpSessionSecurityContextRepository())
        filter.rememberMeServices = rememberMeService()
        return filter
    }

    @Bean
    fun rememberMeAuthenticationProvider(): RememberMeAuthenticationProvider =
        RememberMeAuthenticationProvider(rememberMeCookieProperties.tokenKey)

    @Bean
    fun rememberMeService(): RememberMeService =
        RememberMeService(
            key = rememberMeCookieProperties.tokenKey,
            cookieName = rememberMeCookieProperties.name,
            userRepository = userRepository,
            tokenRepository = tokenRepository,
        )

    @Bean
    fun rememberMeAuthenticationFilter(): RememberMeAuthenticationFilter {
        val rememberMeAuthenticationFilter =
            RememberMeAuthenticationFilter(
                authenticationManager(),
                rememberMeService(),
            )
        return rememberMeAuthenticationFilter
    }

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
