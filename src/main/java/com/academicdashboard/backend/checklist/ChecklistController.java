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
@RequestMapping("/api/stud/checklist")
@RequiredArgsConstructor
public class ChecklistController {

    private final ChecklistService checklistService;

    //Create New Checklist | Returns Checklist Created
    @PostMapping("/{username}/new")
    public ResponseEntity<Checklist> createChecklist(
            @RequestBody Map<String, String> payload, 
            @PathVariable String username) {

        return new ResponseEntity<Checklist>(
            checklistService.createChecklist(
                    username,
                    payload.get("title")
                ), 
                HttpStatus.CREATED);
    }

    //Modify Existing Checklist | Returns Modified Checklist
    @PutMapping("/{username}/modify/{listId}")
    public ResponseEntity<Checklist> modifyChecklist(
            @RequestBody Map<String, String> payload,
            @PathVariable String username,
            @PathVariable String listId) {

        return new ResponseEntity<Checklist>(
            checklistService.modifyChecklist(
                    username,
                    listId,
                    payload.get("title")
                ),
                HttpStatus.OK);
    }

    //Delete Existing Checklist | Returns Status Code 204
    @DeleteMapping("/{username}/delete/{listId}")
    public ResponseEntity<Void> deleteChecklist(
            @PathVariable String username,
            @PathVariable String listId) {

        checklistService.deleteChecklist(username, listId);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

}
