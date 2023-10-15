package com.academicdashboard.backend.profile;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.academicdashboard.backend.exception.ApiRequestException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfessorService {

    private final ProfessorRepository professorRepository;

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

    public Professor getProfessorProfile(String username, String role) {
        if(verifyUser(username, role)) {
            return professorRepository.findByUsername(username)
                .orElseThrow(() -> new ApiRequestException("Could not find username in repo"));
        } else {
            throw new ApiRequestException("Username not in context holder");
        }
    }
    
}
