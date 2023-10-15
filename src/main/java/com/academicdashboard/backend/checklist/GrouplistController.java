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
    @PostMapping("{username}/new")
    public ResponseEntity<Grouplist> createGrouplist(
            @RequestBody Map<String, String> payload,
            @PathVariable String username) {

        return new ResponseEntity<Grouplist>(
            grouplistService.createGrouplist(
                username,
                payload.get("title")
            ),
            HttpStatus.CREATED);
    }

    //Modify Existing Grouplist | Returns Modified Grouplist
    @PutMapping("/{username}/modify/{groupId}")
    public ResponseEntity<Grouplist> modifyGrouplist(
            @RequestBody Map<String, String> payload,
            @PathVariable String username,
            @PathVariable String groupId) {

        return new ResponseEntity<Grouplist>(
            grouplistService.modifyGrouplist(
                    username,
                    groupId,
                    payload.get("title")
                ),
                HttpStatus.OK);
    }

    //Add New Checklist to Grouplist | Returns Grouplist
    @PutMapping("/{username}/addnew/{groupId}")
    public ResponseEntity<Grouplist> addNewToGrouplist(
            @RequestBody Map<String, String> payload,
            @PathVariable String username,
            @PathVariable String groupId) {

        return new ResponseEntity<Grouplist>(
            grouplistService.addNewToGrouplist(
                username,
                groupId,
                payload.get("title")
                ),
            HttpStatus.OK);
    }

    //Add Existing Checklist to Grouplist | Returns Grouplist
    @PutMapping("/{username}/addexist")
    public ResponseEntity<Grouplist> addExistToGrouplist(
            @RequestBody Map<String, String> payload,
            @PathVariable String username) {

        return new ResponseEntity<Grouplist>(
            grouplistService.addExistToGrouplist(
                username, 
                payload.get("groupId"), 
                payload.get("listId")
            ),
            HttpStatus.OK);
    }

    //Remove Existing Checklist From Grouplist | Returns Modified Grouplist
    @PutMapping("/{username}/removefrom")
    public ResponseEntity<Grouplist> removefromGrouplist(
            @RequestBody Map<String, String> payload,
            @PathVariable String username) {

        return new ResponseEntity<Grouplist>(
            grouplistService.removefromGrouplist(
                username, 
                payload.get("groupId"), 
                payload.get("listId")
            ),
            HttpStatus.OK);
    }

    record Condition(String groupId, boolean deleteAll){}

    //Delete Grouplist | Void
    @DeleteMapping("{username}/delete")
    public ResponseEntity<Void> deleteGrouplist(
            @RequestBody Condition condition, 
            @PathVariable String username) {

        grouplistService.deleteGrouplist(username, condition.groupId(), condition.deleteAll());
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

}
