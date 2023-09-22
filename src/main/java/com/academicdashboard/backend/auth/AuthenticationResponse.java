package com.academicdashboard.backend.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String username;
    private String role; //STUDENT || PROFESSOR
    private String accessToken; //JWT
    // private String refreshToken;
}
