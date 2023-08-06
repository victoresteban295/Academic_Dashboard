package com.academicdashboard.backend.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import com.academicdashboard.backend.token.TokenRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    /* Each Time User Logs Out, Expired & Revoked Current Token */

    private final TokenRepository tokenRepository;

    @Override
    public void logout(
            HttpServletRequest request, 
            HttpServletResponse response, 
            Authentication authentication) {

        //Extract JWT From Request
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if(authHeader == null || !authHeader.startsWith("Bearer ")) return;
        jwt = authHeader.substring(7);
        var storedToken = tokenRepository.findByToken(jwt).orElse(null);

        if(storedToken != null) {
            //Expire & Revoke Current Token
            storedToken.setExpired(true);
            storedToken.setRevoked(true);

            //Update Token in Repo
            tokenRepository.save(storedToken);

            //Clear Security Context 
            SecurityContextHolder.clearContext();
        }
    }
}
