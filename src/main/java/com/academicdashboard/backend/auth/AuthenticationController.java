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
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(
            @RequestBody RegisterRequest request) {
        authenticationService.register(request);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    // @PostMapping("/authenticate")
    // public ResponseEntity<AuthenticationResponse> authenticateStudent(
    //         @RequestBody AuthenticationRequest request) {
    //
    //     return new ResponseEntity<AuthenticationResponse>(
    //             authenticationService.authenticate(request), 
    //             HttpStatus.OK);
    // }

    record Authorize(String role) {}

    @PostMapping("/authenticate")
    public ResponseEntity<Authorize> authenticateUser(
            @RequestBody AuthenticationRequest request) {

        AuthenticationResponse authResponse = authenticationService.authenticate(request);
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
        ResponseCookie refreshCookie = ResponseCookie
            .from("refreshToken", authResponse.getRefreshToken())
            .httpOnly(true)
            .path("/")
            .build();
        ResponseCookie accessCookie = ResponseCookie
            .from("accessToken", authResponse.getAccessToken())
            .httpOnly(true)
            .path("/")
            .build();
    
        String role;
        if(authResponse.getRole().equals("PROFESSOR")) {
            role = "professor";
        } else {
            role = "student";
        }

        Authorize authorize = new Authorize(role);
        
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, userCookie.toString())
            .header(HttpHeaders.SET_COOKIE, roleCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .body(authorize);
    }

    // @PostMapping("/refresh-token")
    // public ResponseEntity<AuthenticationResponse> refreshToken(
    //         HttpServletRequest request, 
    //         HttpServletResponse response) throws IOException {
    //
    //     //Request New Access Token (JWT) Using Refresh Token
    //     return new ResponseEntity<AuthenticationResponse>(
    //         authenticationService.refreshToken(request, response),
    //             HttpStatus.OK);
    // }

    @PostMapping("/refresh-token")
    public ResponseEntity<String> refreshToken(
            @CookieValue(name = "username") String username,
            @CookieValue(name = "role") String role,
            @CookieValue(name = "refreshToken") String refreshToken) {

        AuthenticationResponse authResponse = authenticationService
            .refreshToken(username, role, refreshToken);

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
        ResponseCookie refreshCookie = ResponseCookie
            .from("refreshToken", authResponse.getRefreshToken())
            .httpOnly(true)
            .path("/")
            .build();
        ResponseCookie accessCookie = ResponseCookie
            .from("accessToken", authResponse.getAccessToken())
            .httpOnly(true)
            .path("/")
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, userCookie.toString())
            .header(HttpHeaders.SET_COOKIE, roleCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .body(authResponse.getRole());
    }

    // @PostMapping("/valid/access-token")
    // public ResponseEntity<Void> isAccessTokenValid(
    //         @RequestBody Map<String,String>  payload) {
    //     authenticationService.isAccessTokenValid(
    //             payload.get("username"), 
    //             payload.get("accessToken"));
    //     return new ResponseEntity<Void>(HttpStatus.OK);
    // }

    @PostMapping("/valid/access-token")
    public ResponseEntity<Void> isAccessTokenValid(
            @CookieValue(name = "username") String username,
            @CookieValue(name = "role") String role,
            @CookieValue(name = "accessToken") String accessToken) {


        authenticationService.isAccessTokenValid(
                username, 
                role,
                accessToken);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

}
