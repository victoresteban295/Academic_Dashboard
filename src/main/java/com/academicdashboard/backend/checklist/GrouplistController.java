package com.academicdashboard.backend.checklist;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/grouplist")
@RequiredArgsConstructor
public class GrouplistController {
    private final GrouplistService grouplistService;

    //Create New Grouplist | Returns Grouplist Created
    @PostMapping("/{username}/new")
    public ResponseEntity<Grouplist> createGrouplist(
            @PathVariable String username, 
            @RequestBody Map<String, String> payload) {
        return new ResponseEntity<Grouplist>(
                grouplistService.createGrouplist(
                    username, 
                    payload.get("title")), 
                HttpStatus.CREATED);
    } 

    //Modify Grouplist || Return Modified Grouplist
    @PutMapping("/{username}/modify")
    public ResponseEntity<Grouplist> modifyGrouplist(
            @PathVariable String username, 
            @RequestBody Grouplist grouplist) {

        return new ResponseEntity<Grouplist>(
                grouplistService.modifyGrouplist(
                    username, 
                    grouplist), 
                HttpStatus.OK);
    }

    //Delete Grouplist || Void
    @DeleteMapping("/{userame}/delete/{listId}")
    public ResponseEntity<Void> deleteGrouplist(
            @PathVariable String username, 
            @PathVariable String listId) {

        grouplistService.deleteGrouplist(username, listId);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
}
