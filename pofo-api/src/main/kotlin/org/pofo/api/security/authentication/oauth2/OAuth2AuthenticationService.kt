package org.pofo.api.security.authentication.oauth2

import io.github.oshai.kotlinlogging.KotlinLogging
import org.pofo.api.security.PrincipalDetails
import org.pofo.domain.user.User
import org.pofo.domain.user.UserRepository
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class OAuth2AuthenticationService(
    private val userRepository: UserRepository,
) : OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val delegate = DefaultOAuth2UserService()
        val oAuth2User = delegate.loadUser(userRequest)

        val registrationId = userRequest.clientRegistration.registrationId
        val clientType = OAuth2ClientType.getClientType(registrationId)
        val userNameAttributeName =
            userRequest.clientRegistration.providerDetails.userInfoEndpoint.userNameAttributeName
        val attributes = oAuth2User.attributes
        logger.debug { attributes }

        val oAuth2Attribute = OAuth2Attribute.of(clientType, userNameAttributeName, attributes)
        return PrincipalDetails(
            user = User.create("", ""),
            attributes = attributes,
        )
    }
}
