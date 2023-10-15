package com.academicdashboard.backend.user;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.academicdashboard.backend.checklist.Checklist;
import com.academicdashboard.backend.checklist.Grouplist;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // @GetMapping("user/profile/{role}/{username}")
    // public ResponseEntity<User> getUserDetails(
    //         @PathVariable String username, 
    //         @PathVariable String role) {
    //
    //     return new ResponseEntity<User>(
    //             userService.getUserDetails(username, role), 
    //             HttpStatus.OK);
    // }
    
    /***********************************/
    /* ********** Checklist ********** */
    /***********************************/

    @GetMapping("/{username}/get/checklists")
    public ResponseEntity<List<Checklist>> getChecklists(@PathVariable String username) {
        return new ResponseEntity<List<Checklist>>(
                userService.getChecklists(username),
                HttpStatus.OK);
    }

    @GetMapping("/{username}/get/grouplists")
    public ResponseEntity<List<Grouplist>> getGrouplists(@PathVariable String username) {
        return new ResponseEntity<List<Grouplist>>(
                userService.getGrouplists(username),
                HttpStatus.OK);
    }

    @PutMapping("/{username}/rearrange/checklist") 
    public ResponseEntity<List<Checklist>> rearrangeChecklists(
            @PathVariable String username, 
            @RequestBody List<Checklist> checklists) {

        return new ResponseEntity<List<Checklist>>(
                userService.rearrangeChecklists(
                    username, 
                    checklists), 
                HttpStatus.OK);
    }

    /**************************************/
    /* ********** Authenticate ********** */
    /**************************************/

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



