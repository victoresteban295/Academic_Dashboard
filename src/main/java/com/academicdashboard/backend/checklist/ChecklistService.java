package com.academicdashboard.backend.checklist;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.academicdashboard.backend.exception.ApiRequestException;
import com.academicdashboard.backend.user.User;

import lombok.RequiredArgsConstructor;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

@Service
@RequiredArgsConstructor
public class ChecklistService {
    private final ChecklistRepository checklistRepository;
    private final MongoTemplate mongoTemplate;

    //Create New Public Id (JNanoId)
    private static String publicId(int size) {
        Random random = new Random();
        char[] alphabet = {'a','b','c','d','e','1','2','3','5'};
        return NanoIdUtils.randomNanoId(random, alphabet, size); //Create New Public Id
    }

    private boolean verifyUser(String username) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        return currentUser.equals(username);
    }

    /************************************/
    /*********** CRUD METHODS ***********/
    /************************************/

    //Find Checklist By ListId || Returns Checklist
    // public Checklist getChecklist(String username, String listId) {
    //     if(verifyUser(username)) {
    //         return checklistRepository
    //             .findChecklistByListId(listId)
    //             .orElseThrow(() -> new ApiRequestException("Checklist Not Found"));
    //     } else {
    //         throw new ApiRequestException("Username Not Found");
    //     }
    // }

    // //Create New Checklist || Returns Checklist
    // public Checklist createChecklist(String username, String title) {
    //     if(verifyUser(username)) {
    //         //Create & Save Checklist
    //         Checklist checklist = checklistRepository
    //             .insert(Checklist.builder()
    //                     .listId(publicId(10))
    //                     .title(title)
    //                     .groupId("")
    //                     .checkpoints(new ArrayList<>())
    //                     .completedPoints(new ArrayList<>())
    //                     .build());
    //
    //         //Save Checklist in User's 'checklists' attribute
    //         mongoTemplate.findAndModify(
    //                 new Query().addCriteria(Criteria.where("username").is(username)),
    //                 new Update().push("checklists", checklist),
    //                 new FindAndModifyOptions().returnNew(true).upsert(true),
    //                 User.class);
    //         return checklist;
    //     } else {
    //         throw new ApiRequestException("User Not Found");
    //     }
    // }

    //Create New Checklist || Returns Checklist
    public Checklist createChecklist(String username, String title, String listId) {
        if(verifyUser(username)) {
            //Create & Save Checklist
            Checklist checklist = checklistRepository
                .insert(Checklist.builder()
                        .listId(listId)
                        .title(title)
                        .groupId("")
                        .checkpoints(new ArrayList<>())
                        .completedPoints(new ArrayList<>())
                        .build());

            //Save Checklist in User's 'checklists' attribute
            mongoTemplate.findAndModify(
                    new Query().addCriteria(Criteria.where("username").is(username)),
                    new Update().push("checklists", checklist),
                    new FindAndModifyOptions().returnNew(true).upsert(true),
                    User.class);
            return checklist;
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Modify Checklist's Title || Returns Modified Checklist
    public Checklist modifyTitle(String username, String listId, String title) {
        if(verifyUser(username)) {
            //Find Checklist to Update
            Checklist checklist = checklistRepository
                .findChecklistByListId(listId)
                .orElseThrow(() -> new ApiRequestException("Checklist Not Found"));
            checklist.setTitle(title); //Modify Title
            return checklistRepository.save(checklist); //Save Modified Checklist
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Modify Checklist's Checkpoints || Returns Modified Checklist
    //  * Use to add checkpoints
    //  * Use to remove checkpoints
    //  * Use to reorder checkpoints
    //  * Use to Move checkpoints to completed checkpoints list
    public Checklist modifyCheckpoints(
            String username, 
            String listId, 
            List<Checkpoint> checkpoints, 
            List<Checkpoint> completedPoints) {

        if(verifyUser(username)) {
            //Find Checklist to Update
            Checklist checklist = checklistRepository
                .findChecklistByListId(listId)
                .orElseThrow(() -> new ApiRequestException("Checklist Not Found"));
            checklist.setCheckpoints(checkpoints); //Update Checkpoints
            checklist.setCompletedPoints(completedPoints); //Update Completed Checkpoints
            return checklistRepository.save(checklist); //Save Modified Checklist
        } else {
            throw new ApiRequestException("Username Not Found");
        }
    }

    //Delete Checklist || Void
    public void deleteChecklist(String username, String listId) {
        if(verifyUser(username)) {
            checklistRepository.deleteChecklistByListId(listId); //Delete Checklist
        } else {
            throw new ApiRequestException("Username Not Found");
        }
    }
}
