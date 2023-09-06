package com.academicdashboard.backend.auth;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<Void> registerStudent(
            @RequestBody RegisterRequest request) {
        authenticationService.register(request);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticateStudent(
            @RequestBody AuthenticationRequest request) {

        return new ResponseEntity<AuthenticationResponse>(
                authenticationService.authenticate(request), 
                HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(
            HttpServletRequest request, 
            HttpServletResponse response) throws IOException {

        //Request New Access Token (JWT) Using Refresh Token
        return new ResponseEntity<AuthenticationResponse>(
            authenticationService.refreshToken(request, response),
                HttpStatus.OK);
    }

    @PostMapping("/valid/access-token")
    public ResponseEntity<Void> isAccessTokenValid(
            @RequestBody Map<String,String>  payload) {
        authenticationService.isAccessTokenValid(
                payload.get("username"), 
                payload.get("accessToken"));
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

}
