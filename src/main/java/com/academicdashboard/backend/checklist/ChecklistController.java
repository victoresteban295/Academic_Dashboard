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
@RequestMapping("/api/checklist")
@RequiredArgsConstructor
public class ChecklistController {
    private final ChecklistService checklistService;

    //Create New Checklist || Returns Checklist
    @PostMapping("/{username}/new")
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

    //Reorder User's Checklists || Returns Re-ordered Checklists
    @PutMapping("/{username}/reorder")
    public ResponseEntity<List<Checklist>> reorderChecklists(
            @PathVariable String username, 
            @RequestBody Map<String, List<Checklist>> payload) {

        return new ResponseEntity<List<Checklist>>(
                checklistService.reorderChecklist(
                    username, 
                    payload.get("checklists")), 
                HttpStatus.OK);
    }

    //Modify Checklist's Title || Returns Modified Checklist
    @PutMapping("/{username}/modify/title/{listId}")
    public ResponseEntity<Checklist> modifyTitle(
            @PathVariable String username, 
            @PathVariable String listId, 
            @RequestBody Map<String, String> payload) {

        return new ResponseEntity<Checklist>(
                checklistService.modifyTitle(
                    username, 
                    listId,
                    payload.get("title")), 
                HttpStatus.OK);
    }

    //Modify Checklist's Checkpoints || Returns Modified Checklist
    //  * Use to add checkpoints
    //  * Use to remove checkpoints
    //  * Use to reorder checkpoints
    //  * Use to Move checkpoints to completed checkpoints list
    @PutMapping("/{username}/modify/checkpoints/{listId}")
    public ResponseEntity<Checklist> modifyCheckpoints(
            @PathVariable String username, 
            @PathVariable String listId, 
            @RequestBody Map<String, List<Checkpoint>> payload) {

        return new ResponseEntity<Checklist>(
                checklistService.modifyCheckpoints(
                    username, 
                    listId,
                    payload.get("checkpoints"),
                    payload.get("completedPoints")),
                HttpStatus.OK);
    }

    //Delete Checklist || Void
    @DeleteMapping("/{username}/delete/{listId}")
    public ResponseEntity<Void> deleteChecklist(
            @PathVariable String username, 
            @PathVariable String listId) {

        checklistService.deleteChecklist(username, listId);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
}
