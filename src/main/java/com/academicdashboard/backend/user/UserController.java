package com.academicdashboard.backend.user;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.academicdashboard.backend.checklist.Checklist;
import com.academicdashboard.backend.checklist.Grouplist;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/v1.0")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    
    /***********************************/
    /* ********** Checklist ********** */
    /***********************************/

    //Get User's Checklists || Return List<Checklist>
    @GetMapping("/users/{username}/checklists")
    public ResponseEntity<List<Checklist>> getChecklists(
            @PathVariable String username) {

        return new ResponseEntity<List<Checklist>>(
                userService.getChecklists(username),
                HttpStatus.OK);
    }

    //Get User's Grouplists || Return List<Grouplist> 
    @GetMapping("/users/{username}/grouplists")
    public ResponseEntity<List<Grouplist>> getGrouplists(
            @PathVariable String username) {

        return new ResponseEntity<List<Grouplist>>(
                userService.getGrouplists(username),
                HttpStatus.OK);
    }

    //Get All User's Checklists || Return List<Checklist>
    @GetMapping("/users/{username}/all/checklists")
    public ResponseEntity<List<Checklist>> getAllChecklists(
            @PathVariable String username) {

        return new ResponseEntity<List<Checklist>>(
                userService.getAllChecklists(username),
                HttpStatus.OK);
    }

    //Reorder User's Checklists || Return List<Checklist> 
    @PatchMapping("/users/{username}/checklists") 
    public ResponseEntity<List<Checklist>> reorderChecklists(
            @PathVariable String username, 
            @RequestBody Map<String, List<Checklist>> payload) {

        return new ResponseEntity<List<Checklist>>(
                userService.reorderChecklists(
                    username, 
                    payload.get("checklists")), 
                HttpStatus.OK);
    }

    //Reorder User's Grouplists || Return List<Grouplist>
    @PatchMapping("/users/{username}/grouplists")
    public ResponseEntity<List<Grouplist>> reorderGrouplists(
            @PathVariable String username, 
            @RequestBody Map<String, List<Grouplist>> payload) {

        return new ResponseEntity<List<Grouplist>>(
                userService.reorderGrouplists(
                    username, 
                    payload.get("grouplists")), 
                HttpStatus.OK);
    }

    /**************************************/
    /* ********** Authenticate ********** */
    /**************************************/

    @GetMapping("/auth/username/{username}")
    public ResponseEntity<Void> usernameExist(@PathVariable String username) {
        userService.usernameExist(username);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/auth/email/{email}")
    public ResponseEntity<Void> emailExist(@PathVariable String email) {
        userService.emailExist(email);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/auth/phone/{phone}")
    public ResponseEntity<Void> phoneExist(@PathVariable String phone) {
        userService.phoneExist(phone);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
}



