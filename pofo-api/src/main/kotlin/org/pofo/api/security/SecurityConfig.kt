package org.pofo.api.security

import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val authenticationConfiguration: AuthenticationConfiguration,
    private val customAuthenticationSuccessHandler: CustomAuthenticationSuccessHandler,
    private val customAuthenticationFailureHandler: CustomAuthenticationFailureHandler,
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
            }.addFilterBefore(
                customAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter::class.java,
            ).build()

    @Bean
    fun passwordEncoder(): PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    @Bean
    fun customAuthenticationFilter(): CustomAuthenticationFilter {
        val requestMatcher = AntPathRequestMatcher("/auth/login", HttpMethod.POST.name())
        val filter = CustomAuthenticationFilter(requestMatcher)
        filter.setAuthenticationManager(authenticationConfiguration.authenticationManager)
        filter.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler)
        filter.setAuthenticationFailureHandler(customAuthenticationFailureHandler)
        filter.setSecurityContextRepository(HttpSessionSecurityContextRepository())
        return filter
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
