package org.pofo.api.security

import org.pofo.api.security.local.LocalAuthenticationFilter
import org.pofo.api.security.local.LocalAuthenticationService
import org.pofo.api.security.rememberMe.RememberMeCookieProperties
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
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
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import javax.sql.DataSource

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
    fun rememberMeAuthenticationFilter(
        authenticationManager: AuthenticationManager,
        rememberMeAuthenticationService: PersistentTokenBasedRememberMeServices,
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
        userDetailsService: LocalAuthenticationService,
        persistentTokenRepository: PersistentTokenRepository,
    ): PersistentTokenBasedRememberMeServices {
        val rememberMeAuthenticationService =
            PersistentTokenBasedRememberMeServices(
                rememberMeCookieProperties.tokenKey,
                userDetailsService,
                persistentTokenRepository,
            )
        rememberMeAuthenticationService.setCookieName(rememberMeCookieProperties.name)
        rememberMeAuthenticationService.setTokenValiditySeconds(60 * 60 * 24 * 15)
        return rememberMeAuthenticationService
    }

    @Bean
    fun persistentTokenRepository(dataSource: DataSource): PersistentTokenRepository {
        val tokenRepository = JdbcTokenRepositoryImpl()
        tokenRepository.dataSource = dataSource

        val jdbcTemplate = JdbcTemplate(dataSource)
        val tableExistsQuery = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'persistent_logins'"
        val tableExists = jdbcTemplate.queryForObject<Int>(tableExistsQuery) > 0
        if (!tableExists) {
            tokenRepository.setCreateTableOnStartup(true)
        }
        return tokenRepository
    }

    @Bean
    fun localAuthenticationFilter(
        authenticationManager: AuthenticationManager,
        authenticationSuccessHandler: CustomAuthenticationSuccessHandler,
        authenticationFailureHandler: CustomAuthenticationFailureHandler,
        rememberMeAuthenticationService: PersistentTokenBasedRememberMeServices,
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

        val rememberMeAuthenticationProvider = RememberMeAuthenticationProvider(rememberMeCookieProperties.tokenKey)

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
