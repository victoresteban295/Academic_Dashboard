package com.academicdashboard.backend.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("user/profile/{role}/{username}")
    public ResponseEntity<User> getUserDetails(
            @PathVariable String username, 
            @PathVariable String role) {

        return new ResponseEntity<User>(
                userService.getUserDetails(username, role), 
                HttpStatus.OK);
    }

    @GetMapping("/auth/username/taken/{username}")
    public ResponseEntity<Void> usernameExist(@PathVariable String username) {
        userService.usernameExist(username);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/auth/email/taken/{email}")
    public ResponseEntity<Void> emailExist(@PathVariable String email) {
        userService.emailExist(email);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/auth/phone/taken/{phone}")
    public ResponseEntity<Void> phoneExist(@PathVariable String phone) {
        userService.phoneExist(phone);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
}



