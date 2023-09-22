package com.academicdashboard.backend.user;

// import java.util.Collection;
//
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.academicdashboard.backend.exception.ApiRequestException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // private boolean verifyUser(String username, String role) {
    //     String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
    //     String currentRole = ""; 
    //     Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext()
    //         .getAuthentication().getAuthorities();
    //     for(GrantedAuthority authority : authorities) {
    //         currentRole = authority.getAuthority().substring(5);
    //     }
    //     return (currentUser.equals(username)) && (currentRole.equals(role));
    // }

    // public User getUserDetails(String username, String role) {
    //     if(verifyUser(username, role)) {
    //         return userRepository.findUserByUsername(username)
    //             .orElseThrow(() -> new ApiRequestException("Provided Wrong Username"));
    //     } else {
    //         throw new ApiRequestException("Provided Wrong Username");
    //     }
    // }

    public void usernameExist(String username) {
        //True = Exist || False == Doesn't Exist
        boolean exists = userRepository.findUserByUsername(username).isPresent();

        if(exists) {
            throw new ApiRequestException("Username Is Already Taken");
        }
    }

    public void emailExist(String email) {
        //True = Exist || False == Doesn't Exist
        boolean exists = userRepository.findUserByEmail(email).isPresent();

        if(exists) {
            throw new ApiRequestException("Email Is Already Taken");
        }
    }

    public void phoneExist(String phone) {
        //True = Exist || False == Doesn't Exist
        boolean exists = userRepository.findUserByPhone(phone).isPresent();

        if(exists) {
            throw new ApiRequestException("Phone Is Already Taken");
        }
    }
}
