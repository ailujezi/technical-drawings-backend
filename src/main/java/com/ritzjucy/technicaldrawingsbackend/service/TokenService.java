package com.ritzjucy.technicaldrawingsbackend.service;

import com.ritzjucy.technicaldrawingsbackend.exception.AuthException;
import com.ritzjucy.technicaldrawingsbackend.exception.NotFoundException;
import com.ritzjucy.technicaldrawingsbackend.entity.UserEntity;
import com.ritzjucy.technicaldrawingsbackend.entity.repository.UserRepo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;

@Service
public class TokenService
{
    private static final String base64KSecretKEy
            = Base64.getEncoder().encodeToString("139432kdqjdm110dj1md0213e10d12md2tmf2jj4jf3".getBytes());

    @Value("${token.access.validity-mins}")
    private Long accessTokenValidityMins;

    @Value("${token.refresh.validity-mins}")
    private Long refreshTokenValidityMins;

    @Autowired
    private UserRepo userRepo;

    public String createAccessToken(UserEntity user)
    {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("user_id", user.getId())
                .claim("token_type", "access")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * accessTokenValidityMins)) // Set expiration time
                .signWith(SignatureAlgorithm.HS256, base64KSecretKEy)
                .compact();
    }

    public String createRefreshToken(UserEntity user)
    {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("user_id", user.getId())
                .claim("token_type", "refresh")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 *  60 * refreshTokenValidityMins)) // Set expiration time
                .signWith(SignatureAlgorithm.HS256, base64KSecretKEy)
                .compact();
    }

    public void validateToken(HttpServletRequest request)
    {
        if (request.getHeader("Authorization") == null
        || request.getHeader("Authorization").isBlank()) {
            throw new AuthException("missing token");
        }

        String authorization = request.getHeader("Authorization");
        if (!authorization.startsWith("JWT") && !authorization.startsWith("Bearer")) {
            throw new AuthException("token must start have the following form 'JWT ey...'");
        }

        authorization = authorization
                .replaceFirst("JWT ", "")
                .replaceFirst("Bearer ", "");

        try {
            Jwts.parser()
                    .setSigningKey(base64KSecretKEy)
                    .build()
                    .parseClaimsJws(authorization);
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            throw new AuthException("bad token");
        } catch (ExpiredJwtException e) {
            throw new AuthException("expired token");
        }
    }

    public UserEntity getUserFromToken(HttpServletRequest request)
    {
        String authorization = request.getHeader("Authorization")
                .replaceFirst("JWT ", "")
                .replaceFirst("Bearer ", "");

        Long userId = getUserId(authorization);
        return userRepo.findById(userId).orElseThrow(
                () -> new NotFoundException("could not find user with id %d".formatted(userId)));
    }

    public Long getUserId(String token)
    {
        return getClaims(token).get("user_id", Long.class);
    }

    private Claims getClaims(String token)
    {
        try {
            return Jwts.parser()
                    .setSigningKey(base64KSecretKEy)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }
        catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            throw new AuthException("Bad token");
        }
        catch (ExpiredJwtException e) {
            throw new AuthException("Expired token");
        }
    }

}
