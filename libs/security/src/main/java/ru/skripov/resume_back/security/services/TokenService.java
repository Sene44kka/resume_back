package ru.skripov.resume_back.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.skripov.resume_back.security.utils.JwtTokenUtil;
import ru.skripov.resume_back.security.entities.User;

import java.util.Date;
import java.util.Map;

@Service
public class TokenService {
    private static final String USERNAME = "username";
    private static final String EXPIRES_AT = "expires_at";
    private static final String VALID = "valid";
    private static final String TOKEN_TYPE = "token_type";
    private static final String REMAINING_SECONDS = "remaining_seconds";

    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    private TokenService(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public TokenPair generateTokenPair(User user) {
        String accessToken = jwtTokenUtil.generateToken(user);
        String refreshToken = jwtTokenUtil.generateRefreshToken(user);

        return new TokenPair(
                accessToken,
                refreshToken,
                jwtTokenUtil.getAccessTokenExpiration(),
                jwtTokenUtil.getRefreshTokenExpiration(),
                jwtTokenUtil.extractExpiration(accessToken),
                jwtTokenUtil.extractExpiration(refreshToken)
        );
    }

    public record TokenPair(
            String accessToken,
            String refreshToken,
            int accessTokenExpiration,
            int refreshTokenExpiration,
            Date accessTokenExpiresAt,
            Date refreshTokenExpiresAt
    ) {
        public boolean isAccessTokenExpired() {
            return new Date().after(accessTokenExpiresAt);
        }

        public boolean isRefreshTokenExpired() {
            return new Date().after(refreshTokenExpiresAt);
        }
    }

    public boolean isValidToken(String token) {
        return jwtTokenUtil.validateToken(token);
    }

    public Map<String, Object> getTokenInfo(String token) {
        return Map.of(
                USERNAME, jwtTokenUtil.extractUsername(token),
                EXPIRES_AT, jwtTokenUtil.extractExpiration(token),
                VALID, jwtTokenUtil.validateToken(token),
                TOKEN_TYPE, jwtTokenUtil.getTokenType(token),
                REMAINING_SECONDS, jwtTokenUtil.getRemainingTimeSeconds(token)
        );
    }
}