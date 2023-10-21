package com.academicdashboard.backend.checklist;

import java.util.ArrayList;
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
    public Checklist getChecklist(String username, String listId) {
        if(verifyUser(username)) {
            return checklistRepository
                .findChecklistByListId(listId)
                .orElseThrow(() -> new ApiRequestException("Checklist Not Found"));
        } else {
            throw new ApiRequestException("Username Not Found");
        }
    }

    //Create New Checklist || Returns Checklist
    public Checklist createChecklist(String username, String title) {
        if(verifyUser(username)) {
            String listId = publicId(10);
            Checklist checklist = checklistRepository
                .insert(
                        Checklist.builder()
                            .listId(listId)
                            .title(title)
                            .checkpoints(new ArrayList<>())
                            .build());

            //Checklist Reference Stored in User
            RefList refList = RefList.builder()
                .title(title)
                .listId(listId)
                .build();

            mongoTemplate.findAndModify(
                    new Query().addCriteria(Criteria.where("username").is(username)),
                    new Update().set("checklists", refList),
                    new FindAndModifyOptions().returnNew(true).upsert(true),
                    User.class);
            return checklist;
        } else {
            throw new ApiRequestException("Provided Wrong Username");
        }
    }

    //Modify Checklist || Return Modified Checklist
    public Checklist modifyChecklist(String username, Checklist modifyChecklist) {
        if(verifyUser(username)) {
            return checklistRepository.save(modifyChecklist);
        } else {
            throw new ApiRequestException("Username Not Found");
        }
    }

    //Delete Checklist || Void
    public void deleteChecklist(String username, String listId) {
        if(verifyUser(username)) {
            checklistRepository.deleteChecklistByListId(listId);
        } else {
            throw new ApiRequestException("Username Not Found");
        }
    }
}
