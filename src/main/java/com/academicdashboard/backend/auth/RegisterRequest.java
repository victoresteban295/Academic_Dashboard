package com.academicdashboard.backend.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String firstName;
    private String lastName;

    private String profileType; //STUDENT || PROFESSOR 
    private String email;
    private String phone;
    private String username; 
    private String password;

    private String schoolName;
    private String schoolId;

}
