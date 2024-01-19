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
public class ChecklistController {
    private final ChecklistService checklistService;

    //Create New Checklist (Non-Grouped) || Return Checklist
    @PostMapping("/users/{username}/checklists")
    public ResponseEntity<Checklist> createChecklist(
            @PathVariable String username, 
            @RequestBody Map<String, String> payload) {

        return new ResponseEntity<Checklist>(
                checklistService.createChecklist(
                    username, 
                    payload.get("title"), 
                    payload.get("listId")), 
                HttpStatus.CREATED);
    }

    //Edit Checklist's Title || Returns Modified Checklist
    @PatchMapping("/checklists/{listId}")
    public ResponseEntity<Checklist> editTitle(
            @PathVariable String listId, 
            @RequestBody Map<String, String> payload) {

        return new ResponseEntity<Checklist>(
                checklistService.editTitle(
                    listId,
                    payload.get("title")), 
                HttpStatus.OK);
    }

    //Edit Checklist's Checkpoints || Returns Checklist
    //  * Use to add checkpoints
    //  * Use to remove checkpoints
    //  * Use to reorder checkpoints
    //  * Use to Move checkpoints to completed checkpoints list
    @PatchMapping("/checklists/{listId}/checkpoints")
    public ResponseEntity<Checklist> editCheckpoints(
            @PathVariable String listId, 
            @RequestBody Map<String, List<Checkpoint>> payload) {

        return new ResponseEntity<Checklist>(
                checklistService.editCheckpoints(
                    listId,
                    payload.get("checkpoints"),
                    payload.get("completedPoints")),
                HttpStatus.OK);
    }

    //Edit Checklist's Grouplist || Return Checklist
    @PatchMapping("/checklists/{listId}/grouplists")
    public ResponseEntity<Checklist> editGrouplist(
            @PathVariable String listId, 
            @RequestBody Map<String, String> payload) {

        return new ResponseEntity<Checklist>(
                checklistService.editGrouplist(
                    listId,
                    payload.get("groupId")),
                HttpStatus.OK);
    }

    //Delete Checklist || Void
    @DeleteMapping("/checklists/{listId}")
    public ResponseEntity<Void> deleteChecklist(
            @PathVariable String listId) {

        checklistService.deleteChecklist(listId);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
}
