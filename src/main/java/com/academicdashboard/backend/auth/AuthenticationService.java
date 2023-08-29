package com.academicdashboard.backend.auth;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.academicdashboard.backend.config.JwtService;
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
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
        var refreshToken = jwtService.generateRefreshToken(user);//Generate Refresh Token
        revokeAllUserTokens(user.getUserId()); //Expire & Revoke All Old Tokens
        saveUserToken(user.getUsername(), TokenType.ACCESS, jwtToken); //Store Access Token
        saveUserToken(user.getUsername(), TokenType.REFRESH, refreshToken); //Store Refresh Token

        return AuthenticationResponse.builder()
            .username(request.getUsername())
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .build();
    }

    //Request New Access Token (JWT) Using Refresh Token
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        refreshToken = authHeader.substring(7); //Extracts JWT (Removes "Bearer ")
        username = jwtService.extractUsername(refreshToken); //Extract username from JWT

        if(username != null) {
            var user = this.userRepository.findUserByUsername(username)
                .orElseThrow();

            var isTokenValid = tokenRepository.findByToken(refreshToken)
                .map(t -> !t.isExpired() && !t.isRevoked())
                .orElse(false);

            if(jwtService.isTokenValid(refreshToken, user) || isTokenValid) {
                var accessToken = jwtService.generateToken(user);
            revokeAllUserTokens(user.getUserId()); //Expire & Revoke All Old Tokens
            saveUserToken(user.getUsername(), TokenType.ACCESS, accessToken); //Save New AccessToken to Repo
                var authResponse = AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
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

    private void revokeAllUserTokens(String userId) {
        //Create a Query for User's Existing Tokens That Aren't Expired nor Revoked
        Query query = new Query(new Criteria()
                .andOperator(
                    new Criteria().orOperator(
                        Criteria.where("revoked").is(false), 
                        Criteria.where("expired").is(false)), 
                    Criteria.where("userId").is(userId)));

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

}
