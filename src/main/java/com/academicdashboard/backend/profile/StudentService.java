package com.academicdashboard.backend.profile;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.academicdashboard.backend.exception.ApiRequestException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    private boolean verifyUser(String username, String role) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        String currentRole = ""; 
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext()
            .getAuthentication().getAuthorities();
        for(GrantedAuthority authority : authorities) {
            currentRole = authority.getAuthority().substring(5);
        }
        return (currentUser.equals(username)) && (currentRole.equals(role));
    }

    public Student getStudentProfile(String username, String role) {
        if(verifyUser(username, role)) {
            return studentRepository.findByUsername(username)
                .orElseThrow(() -> new ApiRequestException("Provided Wrong Username"));
        } else {
            throw new ApiRequestException("Provided Wrong Username");
        }
    }
    
}
