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

    //Academic Instituiton
    private String profileType; //STUDENT || PROFESSOR 
    private String schoolName; //Academic Institution
    private String schoolId; //Student/Professor Identification Code

    //Personal Information
    private String firstname;
    private String middlename;
    private String lastname;
    private String birthMonth;
    private int birthDay;
    private int birthYear;

    //Account Information
    private String email;
    private String phone;
    private String username; 
    private String password;

    //Professor Information
    private String academicRole;
    private int apptYear;
    private String department;
    private String officeBuilding;
    private String officeRoom;

    //Student Information
    private String gradeLvl;
    private String major;
    private String minor;
    private String concentration;

}
