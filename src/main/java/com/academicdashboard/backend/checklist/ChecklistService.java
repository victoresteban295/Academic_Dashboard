package com.academicdashboard.backend.checklist;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.academicdashboard.backend.exception.ApiRequestException;
import com.academicdashboard.backend.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChecklistService {
    private final ChecklistRepository checklistRepository;
    private final MongoTemplate mongoTemplate;

    private boolean verifyUser(String username) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        return currentUser.equals(username);
    }

    /************************************/
    /*********** CRUD METHODS ***********/
    /************************************/

    //Create New Checklist || Returns Checklist
    public Checklist createChecklist(String username, String title, String listId) {
        if(verifyUser(username)) {
            //Find User Document
            User user = mongoTemplate
                .findOne(new Query().addCriteria(Criteria.where("username").is(username)), 
                    User.class);

            //User's Non-Grouped Checklists
            List<Checklist> checklists = user.getChecklists();

            //User's Are Limited to 20 (Non-Grouped) Checklists
            if(checklists.size() < 20) {
                //Create & Save Checklist
                Checklist checklist = checklistRepository
                    .insert(Checklist.builder()
                            .listId(listId)
                            .title(title)
                            .groupId("")
                            .checkpoints(new ArrayList<>())
                            .completedPoints(new ArrayList<>())
                            .build());
                checklists.add(checklist); //Add Checklist 
                user.setChecklists(checklists); //Update User's Checklists
                mongoTemplate.save(user); //Save Changes to User Document
                return checklist;
            } else {
                throw new ApiRequestException("User's Checklists Limit Exceeded: 20");
            }
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Reorder User's Checklists || Returns Re-ordered Checklists
    public List<Checklist> reorderChecklist(String username, List<Checklist> checklists) {
        if(verifyUser(username)) {
            //Re-ordered Checklists
            List<Checklist> reorderChecklists = new ArrayList<>();

            //Loop thru checklists & add Checkist to Re-ordered Checklists
            for(Checklist checklist : checklists) {
                //Extract Checklist Object
                Checklist list = checklistRepository
                    .findChecklistByListId(checklist.getListId())
                    .orElseThrow(() -> new ApiRequestException("Checklist Not Found"));
                //Add to Re-ordered Checklists
                reorderChecklists.add(list);
            }

            //Find User whose List is Updating
            User user = mongoTemplate
                .findOne(
                    new Query().addCriteria(Criteria.where("username").is(username)), 
                    User.class);

            //Update User's Checklists
            user.setChecklists(reorderChecklists);
            mongoTemplate.save(user); //Save Changes of User Document

            return reorderChecklists;
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
    public Checklist modifyCheckpoints01(
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
    public Checklist modifyCheckpoints(
            String username, 
            String listId, 
            List<Checkpoint> checkpoints, 
            List<Checkpoint> completedPoints) {

        if(verifyUser(username)) {
            //Limited to 20 Checkpoints & 20 Completed Checkpoints
            if(checkpoints.size() <= 20 && completedPoints.size() <= 20) {
                //Find Checklist to Update
                Checklist checklist = checklistRepository
                    .findChecklistByListId(listId)
                    .orElseThrow(() -> new ApiRequestException("Checklist Not Found"));

                checklist.setCheckpoints(checkpoints); //Update Checkpoints
                checklist.setCompletedPoints(completedPoints); //Update Completed Checkpoints
                return checklistRepository.save(checklist); //Save Modified Checklist
            } else {
                //Checkpoints Limit Exceeded
                if(checkpoints.size() > 20) {
                    throw new ApiRequestException("Checkpoints Limit Exceeded: 20");
                //Completed Checkpoints Limit Exceeded
                } else {
                    throw new ApiRequestException("Completed Checkpoints Limit Exceeded: 20");
                }
            }
        } else {
            throw new ApiRequestException("Username Not Found");
        }
    }

    //Delete Checklist || Void
    public void deleteChecklist(String username, String listId) {
        if(verifyUser(username)) {
            //Delte Checklist
            checklistRepository
                .deleteChecklistByListId(listId);

            //Find User Document
            User user = mongoTemplate.findOne(new Query()
                    .addCriteria(Criteria.where("username").is(username)), 
                    User.class);
            //Extract User's Checklists
            List<Checklist> checklists = user.getChecklists();

            //Remove Deleted Checklist from User's Checklists
            checklists.removeIf(checklist -> checklist.getListId() == listId);
            user.setChecklists(checklists); //Update User's Checklists
            mongoTemplate.save(user); //Save Changes to User Document
        } else {
            throw new ApiRequestException("Username Not Found");
        }
    }
}
