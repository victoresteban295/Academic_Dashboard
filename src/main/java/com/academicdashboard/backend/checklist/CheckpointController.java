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
@RequestMapping("/api/stud/checkpoint")
@RequiredArgsConstructor
public class CheckpointController {

    private final CheckpointService checkpointService;

    //Create New Checkpoint Into Existing Checklist | Returns Checklist
    @PostMapping("/{username}/new/{listId}")
    public ResponseEntity<Checklist> addCheckpoint(
            @RequestBody Map<String, String> payload,
            @PathVariable String username,
            @PathVariable String listId) {
        
        return new ResponseEntity<Checklist>(
            checkpointService.addCheckpoint(username, listId, payload.get("content")), 
            HttpStatus.CREATED);
    }

    //Modify Existing Checkpoint | Returns Modified Checkpoint
    @PutMapping("/{username}/modify/{pointId}")
    public ResponseEntity<Checkpoint> modifyCheckpoint(
            @RequestBody Map<String, String> payload, 
            @PathVariable String username,
            @PathVariable String pointId) {

        return new ResponseEntity<Checkpoint>(
                checkpointService.modifyCheckpoint(
                    username,
                    pointId, 
                    payload.get("content")
                ),
                HttpStatus.OK);
    }

    //Delete Checkpoint | Void
    @DeleteMapping("/{username}/delete/{pointId}")
    public ResponseEntity<Void> deleteCheckpoint(
            @PathVariable String username,
            @PathVariable String pointId) {

        checkpointService.deleteCheckpoint(username, pointId);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    //Existing Checkpoint to Subcheckpoint | Return Checkpoint w/ Subpoints
    @PutMapping("/{username}/make/subpoint/{listId}")
    public ResponseEntity<Checkpoint> turnIntoSubcheckpoint(
            @RequestBody Map<String, String> payload, 
            @PathVariable String username,
            @PathVariable String listId) {

       return new ResponseEntity<Checkpoint>(
                checkpointService.turnIntoSubcheckpoint(
                    username,
                    listId, 
                    payload.get("pointId"),
                    payload.get("subpointId")
                ), 
               HttpStatus.OK);
    }

    //Create New SubCheckpoint under Checkpoint | Return Checkpoint
    @PutMapping("/{username}/new/subpoint/{pointId}")
    public ResponseEntity<Checkpoint> newSubcheckpoint(
            @RequestBody Map<String, String> payload, 
            @PathVariable String username,
            @PathVariable String pointId) {

        return new ResponseEntity<Checkpoint>(
                checkpointService.newSubcheckpoint(
                    username,
                    pointId, 
                    payload.get("content")
                ), 
                HttpStatus.OK);
    }

    //Subcheckpoint to Checkpoint | Return Checklist
    @PutMapping("/{username}/reverse/subpoint/{listId}")
    public ResponseEntity<Checklist> reverseSubcheckpoint(
            @RequestBody Map<String, String> payload, 
            @PathVariable String username,
            @PathVariable String listId) {

        return new ResponseEntity<Checklist>(
                checkpointService.reverseSubcheckpoint(
                    username,
                    listId, 
                    payload.get("pointId"),
                    payload.get("subpointId")
                ), 
                HttpStatus.OK);
    }

    //Check off Complete Property on Checkpoint | Return Checkpoint
    @PutMapping("{username}/complete/{pointId}")
    public ResponseEntity<Checkpoint> completeCheckpoint(
            @PathVariable String username,
            @PathVariable String pointId) {

        return new ResponseEntity<Checkpoint>(
                checkpointService.completeCheckpoint(username, pointId), 
                HttpStatus.OK);
    }

}
