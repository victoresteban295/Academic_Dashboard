package com.academicdashboard.backend.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1.0/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    record Authorize(String username, String role) {}

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(
            @RequestBody RegisterRequest request) {
        authenticationService.register(request);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Authorize> authenticateUser(
            @RequestBody AuthenticationRequest request) {

        AuthenticationResponse authResponse = authenticationService.authenticate(request);
        String role;
        if(authResponse.getRole().equals("PROFESSOR")) {
            role = "professor";
        } else {
            role = "student";
        }

        ResponseCookie userCookie = ResponseCookie
            .from("username", authResponse.getUsername())
            .httpOnly(true)
            .path("/")
            .build();
        ResponseCookie roleCookie = ResponseCookie
            .from("role", authResponse.getRole())
            .httpOnly(true)
            .path("/")
            .build();
        ResponseCookie accessCookie = ResponseCookie
            .from("accessToken", authResponse.getAccessToken())
            .httpOnly(true)
            .path("/")
            .build();

        Authorize authorize = new Authorize(authResponse.getUsername(), role);
        
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, userCookie.toString())
            .header(HttpHeaders.SET_COOKIE, roleCookie.toString())
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .body(authorize);
    }


    @PostMapping("/validate/access-token")
    public ResponseEntity<Authorize> isAccessTokenValid(
            @CookieValue(name = "username") String username,
            @CookieValue(name = "role") String role,
            @CookieValue(name = "accessToken") String accessToken) {

        authenticationService.isAccessTokenValid(
                username, 
                role,
                accessToken);

        String authRole;
        if(role.equals("PROFESSOR")) {
            authRole = "professor";
        } else {
            authRole = "student";
        }

        Authorize authorize = new Authorize(username, authRole);

        return new ResponseEntity<Authorize>(authorize, HttpStatus.OK);
    }

}
