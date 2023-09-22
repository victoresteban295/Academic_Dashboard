package com.academicdashboard.backend.auth;

import java.util.ArrayList;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.academicdashboard.backend.config.JwtService;
import com.academicdashboard.backend.exception.ApiRequestException;
import com.academicdashboard.backend.profile.Professor;
import com.academicdashboard.backend.profile.ProfessorRepository;
import com.academicdashboard.backend.profile.Profile;
import com.academicdashboard.backend.profile.Student;
import com.academicdashboard.backend.profile.StudentRepository;
import com.academicdashboard.backend.token.Token;
import com.academicdashboard.backend.token.TokenRepository;
import com.academicdashboard.backend.token.TokenType;
import com.academicdashboard.backend.user.Role;
import com.academicdashboard.backend.user.User;
import com.academicdashboard.backend.user.UserRepository;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final MongoTemplate mongoTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /* Register New User */
    public void register(RegisterRequest request) {

        Profile profile;
        Role role;
        String userId = NanoIdUtils.randomNanoId();

        if(request.getProfileType().equals("STUDENT")) {
            profile = studentRepository
                .insert(Student.builder()
                        .username(request.getUsername())
                        .firstname(request.getFirstname())
                        .middlename(request.getMiddlename())
                        .lastname(request.getLastname())
                        .birthMonth(request.getBirthMonth())
                        .birthDay(request.getBirthDay())
                        .birthYear(request.getBirthYear())
                        .gradeLvl(request.getGradeLvl())
                        .major(request.getMajor())
                        .minor(request.getMinor())
                        .concentration(request.getConcentration())
                        .build());
            role = Role.STUDENT;
        } else {
            profile = professorRepository
                .insert(Professor.builder()
                        .username(request.getUsername())
                        .firstname(request.getFirstname())
                        .middlename(request.getMiddlename())
                        .lastname(request.getLastname())
                        .birthMonth(request.getBirthMonth())
                        .birthDay(request.getBirthDay())
                        .birthYear(request.getBirthYear())
                        .department(request.getDepartment())
                        .academicRole(request.getAcademicRole())
                        .apptYear(request.getApptYear())
                        .officeBuilding(request.getOfficeBuilding())
                        .officeRoom(request.getOfficeRoom())
                        .build());
            role = Role.PROFESSOR;
        }

        //Create New User Using Builder
        var user = User.builder()
            .userId(userId)
            .firstname(request.getFirstname())
            .middlename(request.getMiddlename())
            .lastname(request.getLastname())
            .profileType(request.getProfileType())
            .email(request.getEmail())
            .phone(request.getPhone())
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(role)
            .schoolName(request.getSchoolName())
            .schoolId(request.getSchoolId())
            .profile(profile)
            .courses(new ArrayList<>())
            .calendars(new ArrayList<>())
            .grouplists(new ArrayList<>())
            .checklists(new ArrayList<>())
            .reminderLists(new ArrayList<>())
            .build();

        userRepository.save(user); //Save New User to Repository
    }

    /* Authenticate Existing User */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        //Use the AuthenticationManager's authenticate() method 
        //to authenticate users based on the username & password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(), 
                    request.getPassword()));

        //Pull Student User From Repository
        var user = userRepository.findUserByUsername(request.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("User Not Found!"));

        //Create new JWT Token for Response
        var jwtToken = jwtService.generateToken(user); //Generate JWT
        // var refreshToken = jwtService.generateRefreshToken(user);//Generate Refresh Token
        revokeAllUserTokens(user.getUsername()); //Expire & Revoke All Old Tokens
        saveUserToken(user.getUsername(), TokenType.ACCESS, jwtToken); //Store Access Token
        // saveUserToken(user.getUsername(), TokenType.REFRESH, refreshToken); //Store Refresh Token

        return AuthenticationResponse.builder()
            .username(request.getUsername())
            .role(user.getRole().toString())
            .accessToken(jwtToken)
            // .refreshToken(refreshToken)
            .build();
    }

    //Request New Access Token (JWT) Using Refresh Token
    // public AuthenticationResponse refreshToken(
    //         String username, 
    //         String role, 
    //         String refreshToken) {
    //
    //     //Ensures Cookies Were Found
    //     boolean hasCookies = (username != null) && (role != null) && (refreshToken != null);
    //     if(!hasCookies) {
    //         throw new ApiRequestException("No Access Token Found");
    //     }
    //
    //     //Ensure Token is a Refresh Token
    //     Token refreshTokenObj = tokenRepository.findByToken(refreshToken) 
    //         .orElseThrow( () -> new ApiRequestException("Invalid Refresh Token"));
    //
    //     username = jwtService.extractUsername(refreshToken); //Extract username from JWT
    //
    //     if((username != null) && (refreshTokenObj.getTokenType() == TokenType.REFRESH)) {
    //         var user = this.userRepository.findUserByUsername(username)
    //             .orElseThrow();
    //
    //         boolean isTokenValid;
    //         //NOTE: jwtService.isTokenValid() methods checks the actual expiration of the token itself
    //         //Ensure Refresh Token Has Not Been Revoked By Our Backend 
    //         if(!refreshTokenObj.isRevoked() && !refreshTokenObj.isExpired()) {
    //             isTokenValid = true;
    //         } else {isTokenValid = false;}
    //
    //         if(jwtService.isTokenValid(refreshToken, user) && isTokenValid) {
    //             var accessToken = jwtService.generateToken(user); //Generate New Access Token
    //             revokeAllUserAccessTokens(user.getUsername()); //Expire & Revoke All Old Access Tokens
    //             saveUserToken(user.getUsername(), TokenType.ACCESS, accessToken); //Save New AccessToken to Repo
    //
    //             return AuthenticationResponse.builder()
    //                 .username(username)
    //                 .role(role)
    //                 .accessToken(accessToken)
    //                 .refreshToken(refreshToken) //Same Refresh Token Provided
    //                 .build();
    //         } else {
    //             throw new ApiRequestException("Invalid Refresh Token");
    //         }
    //     } else {
    //         throw new ApiRequestException("Invalid Refresh Token");
    //     }
    // }

    public void isAccessTokenValid(
            String username, 
            String role, 
            String accessToken) {

        //Check if User with that Username Exists
        User user = userRepository
            .findUserByUsername(username)
            .orElseThrow(() -> new ApiRequestException("Invalid Username"));

        //Check if Roles Provided Matches
        if(!user.getRole().toString().equals(role)) {
            throw new ApiRequestException("Invalid Role");
        }

        Token jwt = tokenRepository
            .findByToken(accessToken)
            .orElseThrow(() -> new ApiRequestException("Invalid Access Token"));

        boolean isTokenValid = (jwtService.isTokenValid(accessToken, user)) && !jwt.isRevoked() && !jwt.isExpired();
        boolean isAccessToken = jwt.getTokenType().equals(TokenType.ACCESS);
        if(!isTokenValid || !isAccessToken) {
           throw new ApiRequestException("Invalid Access Token");
        }
    }

    /*************** Private Methods ***************/
    private void saveUserToken(String username, TokenType tokenType, String jwt) {
        //Create a Token Instance
        var token = Token.builder()
            .token(jwt)
            .tokenType(tokenType)
            .username(username)
            .revoked(false)
            .expired(false)
            .build();

        tokenRepository.save(token); //Save Token to Repository
    }

    private void revokeAllUserTokens(String username) {
        //Create a Query for User's Existing Tokens That Aren't Expired nor Revoked
        Query query = new Query(new Criteria()
                .andOperator(
                    new Criteria().orOperator(
                        Criteria.where("revoked").is(false), 
                        Criteria.where("expired").is(false)), 
                    Criteria.where("username").is(username)));

        //Extracted Tokens (the match query) from Repository 
        var validUserTokens = mongoTemplate.find(query, Token.class);
        if(validUserTokens.isEmpty()) return; //Return if not tokens 

        //Update Each Individual User Token to be Expired & Revoked
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens); //Add Updates to Repo
    }

    // private void revokeAllUserAccessTokens(String username) {
    //     //Create a Query for User's Existing Tokens That Aren't Expired nor Revoked
    //     Query query = new Query(new Criteria()
    //             .andOperator(
    //                 new Criteria().orOperator(
    //                     Criteria.where("revoked").is(false), 
    //                     Criteria.where("expired").is(false)), 
    //                 Criteria.where("username").is(username)));
    //
    //     //Extracted Tokens (the match query) from Repository 
    //     var validUserTokens = mongoTemplate.find(query, Token.class);
    //     if(validUserTokens.isEmpty()) return; //Return if not tokens 
    //
    //     //Update Each Individual User Access Token That Aren't Expired nor Revoked
    //     validUserTokens.forEach(token -> {
    //         //Revoke & Expire Access Tokens ONLY
    //         if(token.getTokenType() == TokenType.ACCESS) {
    //             token.setExpired(true);
    //             token.setRevoked(true);
    //         }
    //     });
    //
    //     tokenRepository.saveAll(validUserTokens); //Add Updates to Repo
    //
    // }

}
