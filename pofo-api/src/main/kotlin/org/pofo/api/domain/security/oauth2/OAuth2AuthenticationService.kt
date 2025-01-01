package org.pofo.api.domain.security.oauth2

import io.github.oshai.kotlinlogging.KotlinLogging
import org.pofo.api.domain.security.PrincipalDetails
import org.pofo.api.domain.security.jwt.JwtTokenData
import org.pofo.domain.rds.domain.user.User
import org.pofo.domain.rds.domain.user.UserRepository
import org.pofo.domain.rds.domain.user.UserSocialAccount
import org.pofo.domain.rds.domain.user.UserSocialAccountRepository
import org.pofo.domain.rds.domain.user.UserSocialType
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import java.util.UUID

private val logger =
    KotlinLogging
        .logger {
        }

@Service
class OAuth2AuthenticationService(
    private val userRepository: UserRepository,
    private val userSocialAccountRepository: UserSocialAccountRepository,
) : OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val delegate =
            DefaultOAuth2UserService()
        val oAuth2User =
            delegate
                .loadUser(
                    userRequest,
                )

        val registrationId =
            userRequest.clientRegistration.registrationId
        val socialType =
            UserSocialType
                .getSocialType(
                    registrationId,
                )
        val userNameAttributeName =
            userRequest.clientRegistration.providerDetails.userInfoEndpoint.userNameAttributeName
        val attributes =
            oAuth2User.attributes
        val accessToken =
            userRequest.accessToken.tokenValue
        logger
            .debug {
                attributes
            }

        val oAuth2Attribute =
            OAuth2Attribute
                .of(
                    socialType,
                    userNameAttributeName,
                    attributes,
                )
        val user =
            getUserElseCreate(
                oAuth2Attribute,
                socialType,
                accessToken,
            )
        return PrincipalDetails(
            JwtTokenData(
                userId = user.id,
                email = user.email,
                name = "some name",
                role = user.role,
            ),
        )
    }

    private fun getUserElseCreate(
        oAuth2Attribute: OAuth2Attribute,
        socialType: UserSocialType,
        accessToken: String,
    ): User {
        val findUser =
            userRepository
                .findBySocialAccountIdAntType(
                    oAuth2Attribute.userProperties.id,
                    socialType,
                )
        if (findUser !=
            null
        ) {
            return findUser
        }

        val createdUser =
            User
                .builder()
                .email(
                    oAuth2Attribute.userProperties.email,
                ).password(
                    "OAuth2-${UUID.randomUUID()}",
                ).avatarUrl(
                    oAuth2Attribute.userProperties.avatarUrl,
                ).build()
        userRepository
            .save(
                createdUser,
            )
        val socialAccount =
            UserSocialAccount(
                oAuth2Attribute.userProperties.id,
                createdUser,
                socialType,
                accessToken,
            )
        userSocialAccountRepository
            .save(
                socialAccount,
            )
        return createdUser
    }
}
