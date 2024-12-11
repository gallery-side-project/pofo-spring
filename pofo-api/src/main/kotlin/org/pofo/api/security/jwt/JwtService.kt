package org.pofo.api.security.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.pofo.domain.rds.domain.user.UserRole
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService {
    @Value("\${jwt.secretKey}")
    private lateinit var secretKeyStr: String

    fun secretKey(): SecretKey = Keys.hmacShaKeyFor(secretKeyStr.toByteArray())

    companion object {
        const val ACCESS_TOKEN_EXPIRATION: Long = 1000 * 60 * 30 // 30m
        const val REFRESH_TOKEN_EXPIRATION: Long = 1000 * 60 * 60 * 24 * 15 // 15d
    }

    fun generateAccessToken(
        jwtTokenData: JwtTokenData
    ): String {
        val claimsMap = jwtTokenData.toMap()
        val now = System.currentTimeMillis()
        return Jwts
            .builder()
            .subject(jwtTokenData.userId.toString())
            .claims(claimsMap)
            .issuedAt(Date(now))
            .expiration(Date(now + ACCESS_TOKEN_EXPIRATION))
            .signWith(secretKey())
            .compact()
    }

    fun generateRefreshToken(userId: Long): String {
        val now = System.currentTimeMillis()
        return Jwts
            .builder()
            .subject(userId.toString())
            .issuedAt(Date(now))
            .expiration(Date(now + REFRESH_TOKEN_EXPIRATION))
            .signWith(secretKey())
            .compact()
    }

    private fun <T> extractClaim(
        token: String,
        claimResolver: (Claims) -> T,
    ): T {
        val claims: Claims =
            Jwts
                .parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(token)
                .payload
        return claimResolver(claims)
    }

    fun extractUserId(token: String): Long {
        return extractClaim(token) { claims ->
            claims.subject.toLong()
        }
    }

    fun extractTokenData(token: String): JwtTokenData {
        return extractClaim(token) { claims ->
            JwtTokenData(
                userId = claims.subject.toLong(),
                email = claims[JwtTokenData.KEY_EMAIL] as String,
                name = claims[JwtTokenData.KEY_NAME] as String,
                role = UserRole.valueOf(claims[JwtTokenData.KEY_ROLE] as String),
                imageUrl = claims[JwtTokenData.KEY_IMAGE_URL] as String?
            )
        }
    }
}
