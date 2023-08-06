package com.academicdashboard.backend.auth;

import java.io.IOException;

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
    public ResponseEntity<AuthenticationResponse> registerStudent(
            @RequestBody RegisterRequest request) {

        return new ResponseEntity<AuthenticationResponse>(
                authenticationService.register(request), 
                HttpStatus.OK);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticateStudent(
            @RequestBody AuthenticationRequest request) {

        return new ResponseEntity<AuthenticationResponse>(
                authenticationService.authenticate(request), 
                HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //Request New Access Token (JWT) Using Refresh Token
        authenticationService.refreshToken(request, response);
    }

}
