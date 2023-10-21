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

import com.academicdashboard.backend.checklist.Grouplist;
import com.academicdashboard.backend.checklist.RefList;

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

    //Get All User's Checklist || Return Checklists
    @GetMapping("/{username}/get/checklists")
    public ResponseEntity<List<RefList>> getUserChecklists(@PathVariable String username) {
        return new ResponseEntity<List<RefList>>(
                userService.getUserChecklists(username),
                HttpStatus.OK);
    }

    //Get User's Grouplist || Return Grouplist
    @GetMapping("/{username}/get/grouplists")
    public ResponseEntity<List<Grouplist>> getUserGrouplists(@PathVariable String username) {
        return new ResponseEntity<List<Grouplist>>(
                userService.getUserGrouplists(username),
                HttpStatus.OK);
    }

    //Determine If User Has Checklists || Returns First ListId
    @GetMapping("/{username}/get/first/checklist")
    public ResponseEntity<String> getUserFirstChecklists(@PathVariable String username) {
        return new ResponseEntity<String>(
                userService.getUserFirstCheclist(username),
                HttpStatus.OK);
    }

    //Modify/Rearrange User's Checklists || Returns Checklists
    @PutMapping("/{username}/rearrange/checklist") 
    public ResponseEntity<List<RefList>> modifyUserChecklists(
            @PathVariable String username, 
            @RequestBody List<RefList> checklists) {

        return new ResponseEntity<List<RefList>>(
                userService.modifyUserChecklists(
                    username, 
                    checklists), 
                HttpStatus.OK);
    }

    //Modify/Rearrange User's Grouplists || Returns Grouplists
    @PutMapping("/{username}/rearrange/grouplist") 
    public ResponseEntity<List<Grouplist>> rearrangeGrouplists(
            @PathVariable String username, 
            @RequestBody List<Grouplist> grouplists) {

        return new ResponseEntity<List<Grouplist>>(
                userService.modifyGrouplists(
                    username, 
                    grouplists), 
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



