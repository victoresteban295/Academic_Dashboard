package com.academicdashboard.backend.checklist;

import java.util.List;
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

    // //Create New Grouplist | Returns Grouplist Created
    // @PostMapping("/{username}/new")
    // public ResponseEntity<Grouplist> createGrouplist(
    //         @PathVariable String username, 
    //         @RequestBody Map<String, String> payload) {
    //
    //     return new ResponseEntity<Grouplist>(
    //             grouplistService.createGrouplist(
    //                 username, 
    //                 payload.get("title")), 
    //             HttpStatus.CREATED);
    // } 

    //Create New Grouplist | Returns Grouplist Created
    @PostMapping("/{username}/new")
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

    //Modify Grouplist's Title || Return Modified Grouplist
    @PutMapping("/{username}/modify/title/{groupId}")
    public ResponseEntity<Grouplist> modifyTitle(
            @PathVariable String username, 
            @PathVariable String groupId,
            @RequestBody Map<String, String> payload) {

        return new ResponseEntity<Grouplist>(
                grouplistService.modifyTitle(
                    username, 
                    groupId,
                    payload.get("title")), 
                HttpStatus.OK);
    }

    //Create & Add New Checklist to Grouplist || Return Modified Grouplist
    @PutMapping("/{username}/new/checklist/{groupId}")
    public ResponseEntity<Grouplist> createChecklist(
            @PathVariable String username, 
            @PathVariable String groupId, 
            @RequestBody Map<String, String> payload) {

        return new ResponseEntity<Grouplist>(
                grouplistService.createChecklist(
                    username, 
                    groupId,
                    payload.get("title")), 
                HttpStatus.OK);
    }

    //Add Existing Checklist to Grouplist || Return Modified Grouplist
    @PutMapping("/{username}/add/{listId}/to/{groupId}")
    public ResponseEntity<Grouplist> addChecklist(
            @PathVariable String username, 
            @PathVariable String groupId, 
            @PathVariable String listId) {

        return new ResponseEntity<Grouplist>(
                grouplistService.addChecklist(
                    username, 
                    listId,
                    groupId), 
                HttpStatus.OK);
    }

    //Move Checklist From Grouplist to Grouplist || Return Designated Grouplist
    @PutMapping("/{username}/move/{listId}/from/{fromGroupId}/to/{toGroupId}")
    public ResponseEntity<Grouplist> moveChecklist(
            @PathVariable String username, 
            @PathVariable String listId, 
            @PathVariable String fromGroupId, 
            @PathVariable String toGroupId) {

        return new ResponseEntity<Grouplist>(
                grouplistService.moveChecklist(
                    username, 
                    listId,
                    fromGroupId,
                    toGroupId), 
                HttpStatus.OK);
    }

    //Reorder Grouplist's Checklists || Return Modified Grouplist
    @PutMapping("/{username}/reorder/checklists/{groupId}") 
    public ResponseEntity<Grouplist> reorderChecklists(
            @PathVariable String username, 
            @PathVariable String groupId, 
            @RequestBody List<Checklist> checklists) {

        return new ResponseEntity<Grouplist>(
                grouplistService.reorderChecklists(
                    username, 
                    groupId, 
                    checklists), 
                HttpStatus.OK);
    }

    //Remove Checklist From Grouplist | Returns Modified Grouplist
    @PutMapping("/{username}/remove/{listId}/from/{groupId}")
    public ResponseEntity<Grouplist> removeChecklist(
            @PathVariable String username, 
            @PathVariable String groupId, 
            @PathVariable String listId) {

        return new ResponseEntity<Grouplist>(
                grouplistService.removeChecklist(
                    username, 
                    listId,
                    groupId), 
                HttpStatus.OK);
    }

    //Delete Grouplist || Void
    @DeleteMapping("/{username}/delete/{groupId}")
    public ResponseEntity<Void> deleteGrouplist(
            @PathVariable String username, 
            @PathVariable String groupId) {

        grouplistService.deleteGrouplist(username, groupId);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
}
