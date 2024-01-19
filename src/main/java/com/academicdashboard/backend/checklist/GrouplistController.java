package com.academicdashboard.backend.checklist;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1.0")
@RequiredArgsConstructor
public class GrouplistController {
    private final GrouplistService grouplistService;

    //Create New Grouplist | Returns Grouplist 
    @PostMapping("/users/{username}/grouplists")
    public ResponseEntity<Grouplist> createGrouplist(
            @PathVariable String username, 
            @RequestBody Map<String, String> payload) {

        return new ResponseEntity<Grouplist>(
                grouplistService.createGrouplist(
                    username, 
                    payload.get("title"), 
                    payload.get("groupId")), 
                HttpStatus.CREATED);
    } 

    //Edit Grouplist's Title || Return Grouplist
    @PatchMapping("/grouplists/{groupId}")
    public ResponseEntity<Grouplist> editTitle(
            @PathVariable String groupId,
            @RequestBody Map<String, String> payload) {

        return new ResponseEntity<Grouplist>(
                grouplistService.editTitle(
                    groupId,
                    payload.get("title")), 
                HttpStatus.OK);
    }

    //Edit Grouplist's Checklists || Return Grouplist
    @PatchMapping("/grouplists/{groupId}/checklists")
    public ResponseEntity<Grouplist> editChecklists(
            @PathVariable String groupId,
            @RequestBody Map<String, List<Checklist>> payload) {

        return new ResponseEntity<Grouplist>(
                grouplistService.editChecklists(
                    groupId,
                    payload.get("checklists")), 
                HttpStatus.OK);
    }

    //Create New Checklist Under Grouplist || Return Grouplist
    @PostMapping("/grouplists/{groupId}/checklists")
    public ResponseEntity<Grouplist> createChecklist(
            @PathVariable String groupId, 
            @RequestBody Map<String, String> payload) {

        return new ResponseEntity<Grouplist>(
                grouplistService.createChecklist(
                    groupId,
                    payload.get("listId"),
                    payload.get("title")), 
                HttpStatus.OK);
    }

    //Delete Grouplist || Void
    @DeleteMapping("/grouplists/{groupId}")
    public ResponseEntity<Void> deleteGrouplist(
            @PathVariable String groupId) {

        grouplistService.deleteGrouplist(groupId);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
}
