package com.gruppo42.restapi.security;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.Date;

/**
 * JWTTokenProvider.
 * This is pretty much self-explanatory.
 * Generates tokens, retrieves ids and validates them.
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    //Used to sign tokens in for the sake off token integrity
    @Value("${app.jwtSecret}")
    private String jwtSecret;

    //Token expiration
    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    public String generateToken(Authentication authentication)
    {

        //Get user info from the authentication
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        //Return a signed jwt for token integrity
        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public Long getUserIdFromJWT(String token)
    {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken)
    {
        try
        {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex)
        {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex)
        {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex)
        {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex)
        {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex)
        {
            logger.error("JWT claims string is empty.");
        }
        return false;
    }
}