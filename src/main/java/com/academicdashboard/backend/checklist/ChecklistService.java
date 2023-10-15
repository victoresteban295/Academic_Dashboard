package com.academicdashboard.backend.checklist;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

import lombok.RequiredArgsConstructor;

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

    /*********** QUERY DEFINITION METHOD ***********/
    private static Query query(String field, String equalsValue) {
        return new Query().addCriteria(Criteria.where(field).is(equalsValue));
    } 

    /*********** UPDATE DEFINITION METHODS ***********/
    private static Update setUpdate(String field, String value) {
        return new Update().set(field, value);
    }

    private static Update pushUpdate(String field, Checklist checklist) {
        return new Update().push(field).value(checklist);
    }

    /*********** OPTION DEFINITION METHOD ***********/
    private static FindAndModifyOptions options(boolean returnNew, boolean upsert) {
        return new FindAndModifyOptions().returnNew(returnNew).upsert(upsert);
    }

    public boolean verifyUser(String username) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        return currentUser.equals(username);
    }

    /*********** CRUD METHODS ***********/

    //Find Checklist By ListId | Returns Checklist
    public Checklist getChecklist(String username, String listId) {
        if(verifyUser(username)) {
            return checklistRepository
                .findChecklistByListId(listId)
                .orElseThrow(() -> new ApiRequestException("Provided Wrong ListId"));
        } else {
            throw new ApiRequestException("Provided Wrong Username");
        }
    }

    //Create New Checklist | Returns Checklist Created
    public Checklist createChecklist(String username, String title) {
        if(verifyUser(username)) {
            String listId = publicId(5);
            Checklist checklist = checklistRepository
                .insert(
                        Checklist.builder()
                            .listId(listId)
                            .title(title)
                            .checkpoints(new ArrayList<>())
                            .build());

            mongoTemplate.findAndModify(
                    query("username", username), 
                    pushUpdate("checklists", checklist), 
                    options(true, true), 
                    User.class);

            return checklist;
        } else {
            throw new ApiRequestException("Provided Wrong Username");
        }
    }

    //Modify Existing Checklist | Returns Modified Checklist
    public Checklist modifyChecklist(String username, String listId, String newTitle) {
        if(verifyUser(username)) {
            if(mongoTemplate.exists(query("listId", listId), Checklist.class)) {
                return mongoTemplate.findAndModify(
                        query("listId", listId), 
                        setUpdate("title", newTitle), 
                        options(true, true), 
                        Checklist.class);
            } else {
                throw new ApiRequestException("Checklist You Wanted to Modify Doesn't Exist");
            }
        } else {
            throw new ApiRequestException("Provided Wrong Username");
        }

    }

    //Delete Existing Checklist | Void 
    public void deleteChecklist(String username, String listId) {
        if(verifyUser(username)) {
            //Delete Corresponding Checkpoints
            Checklist checklist = Optional.ofNullable(
                    mongoTemplate.findOne(
                            query("listId", listId), 
                            Checklist.class))
                .orElseThrow(() -> new ApiRequestException("Checklist You Wanted to Delete Doesn't Exist"));

            List<Checkpoint> checkpoints = checklist.getCheckpoints(); 
            for(Checkpoint point : checkpoints) {
                //Remove any subcheckpoints
                if(!point.getSubCheckpoints().isEmpty()) {
                    List<Checkpoint> subpoints = point.getSubCheckpoints();
                    for(Checkpoint subpoint : subpoints) {
                        mongoTemplate.remove(query("pointId", subpoint.getPointId()), Checkpoint.class);
                    }
                }
                mongoTemplate.remove(query("pointId", point.getPointId()), Checkpoint.class);
            }

            //Delete Checklist
            mongoTemplate.remove(
                    query("listId", listId), 
                    Checklist.class);
        } else {
            throw new ApiRequestException("Provided Wrong Username");
        }

    }

}
