package com.academicdashboard.backend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    /* Extract the Username from JWT */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject); //Subject == Username
    }

    /* Extract a Single Claim From JWT */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /* Generate JWT Without Extra Claims */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /* Generate JWT */
    public String generateToken(
            Map<String, Object> extraClaims, 
            UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    // public String generateRefreshToken(UserDetails userDetails) {
    //     return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    // }

    /* Actual Creation of JWT */
    private String buildToken(
            Map<String, Object> extraClaims, 
            UserDetails userDetails,
            long expiration) {

        String role = ""; //Extract (String) Role From GrantedAuthority
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        for(GrantedAuthority authority : authorities) {
            role = authority.getAuthority().substring(5);
        }

        return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .claim("role", role)
            .setIssuedAt(new Date(System.currentTimeMillis())) //when JWT was created
            .setExpiration(new Date(System.currentTimeMillis() + expiration)) //Length of Token Validation
            .signWith(getSignInKey(), SignatureAlgorithm.HS256) //Sign JWT
            .compact(); //Generates & Returns Actual JWT
    }

    /* Validate JWT & If User Belongs to JWT */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token); //Extract Username
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token); //Validation
    }

    /* Check If JWT is Expired (false = NO | true = YES)*/
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /* Extract JWT Expiration Date */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /* Extract All Claims from JWT */
    private Claims extractAllClaims(String token) {
        return Jwts
            .parserBuilder() //parse the jwt token
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token) //actual parsing
            .getBody();
    }

    /* Get Sign-in Key */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
