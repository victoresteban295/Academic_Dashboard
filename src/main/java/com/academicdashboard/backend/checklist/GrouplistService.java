package com.academicdashboard.backend.checklist;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

@Service
@RequiredArgsConstructor
public class GrouplistService {
    private final GrouplistRepository grouplistRepository;
    private final MongoTemplate mongoTemplate;

    /*********** QUERY DEFINITION METHOD ***********/
    private static Query query(String field, String equalsValue) {
        return new Query().addCriteria(Criteria.where(field).is(equalsValue));
    } 

    private static Update pushUpdate(String field, Checklist checklist) {
        return new Update().push(field).value(checklist);
    }

    private static Update pullUpdate(String field, Checklist checklist) {
        return new Update().pull(field, checklist); 
    }

    /*********** OPTION DEFINITION METHOD ***********/
    private static FindAndModifyOptions options(boolean returnNew, boolean upsert) {
        return new FindAndModifyOptions().returnNew(returnNew).upsert(upsert);
    }

    //Verify Username Matches Logged-in User
    private boolean verifyUser(String username) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        return currentUser.equals(username);
    }

    /************************************/
    /*********** CRUD METHODS ***********/
    /************************************/

    //Create New Grouplist | Returns Grouplist Created
    public Grouplist createGrouplist(String username, String title, String groupId) {
        if(verifyUser(username)) {
            //Create Grouplist
            Grouplist grouplist = grouplistRepository.insert(
                    Grouplist.builder()
                    .groupId(groupId)
                    .title(title)
                    .checklists(new ArrayList<>())
                    .build());

            //Add New Grouplist to User
            mongoTemplate.findAndModify(
                    new Query().addCriteria(Criteria.where("username").is(username)),
                    new Update().push("grouplists", grouplist),
                    new FindAndModifyOptions().returnNew(true).upsert(true),
                    User.class);
            return grouplist;
        } else {
            throw new ApiRequestException("Username Not Found");
        }
    }

    //Modify Grouplist's Title || Return Modified Grouplist
    public Grouplist modifyTitle(String username, String groupId, String title) {
        if(verifyUser(username)) {
            //Find Grouplist To Modify
            Grouplist grouplist = grouplistRepository
                .findGrouplistByGroupId(groupId)
                .orElseThrow(() -> new ApiRequestException("Grouplist Not Found"));
            grouplist.setTitle(title); //Set New Title
            return grouplistRepository.save(grouplist); //Save & Return Modified Grouplist
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Create & Add New Checklist to Grouplist || Return Modified Grouplist
    public Grouplist createChecklist(String username, String groupId, String listId, String title) {
        if(verifyUser(username)) {
            //Create & Save Checklist
            Checklist checklist = mongoTemplate.insert(Checklist.builder()
                    .listId(listId)
                    .title(title)
                    .groupId(groupId)
                    .checkpoints(new ArrayList<>())
                    .completedPoints(new ArrayList<>())
                    .build());

            //Find & Update Grouplist with Newly Created Checklist
            boolean grouplistExists = mongoTemplate.exists(query("groupId", groupId), Grouplist.class); 
            if(grouplistExists) {
                return mongoTemplate.findAndModify(
                    query("groupId", groupId), 
                    pushUpdate("checklists", checklist), 
                    options(true, true), 
                    Grouplist.class);
            } else {
                throw new ApiRequestException("Grouplist Not Found");
            }
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Add Existing Checklist to Grouplist || Return Modified Grouplist
    public Grouplist addChecklist(String username, String listId, String groupId) {
        if(verifyUser(username)) {
            //Find Existing Checklist
            Checklist checklist = Optional.ofNullable(
                    mongoTemplate.findOne(
                        query("listId", listId), 
                        Checklist.class))
                .orElseThrow(() -> new ApiRequestException("Checklist Not Found"));

            //Remove Checklist From User's 'checklists' attribute
            mongoTemplate.findAndModify(
                    query("username", username), 
                    pullUpdate("checklists", checklist), 
                    User.class);

            Checklist updatedChecklist = mongoTemplate.findAndModify(
                    query("listId", listId), 
                    new Update().set("groupId", groupId), 
                    Checklist.class);

            //Find & Update Grouplist with Checklist
            boolean grouplistExists = mongoTemplate.exists(query("groupId", groupId), Grouplist.class); 
            if(grouplistExists) {
                return mongoTemplate.findAndModify(
                    query("groupId", groupId), 
                    pushUpdate("checklists", updatedChecklist), 
                    options(true, true), 
                    Grouplist.class);
            } else {
                throw new ApiRequestException("Grouplist Not Found");
            }
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Move Checklist From Grouplist to Grouplist || Return Designated Grouplist
    public Grouplist moveChecklist(
            String username,
            String listId, 
            String fromGroupId, 
            String toGroupId) {

        if(verifyUser(username)) {
            boolean fromExists = grouplistRepository.existsByGroupId(fromGroupId); 
            boolean toExists = grouplistRepository.existsByGroupId(toGroupId); 
            if(fromExists && toExists) {
                //Find Existing Checklist
                Checklist checklist = Optional.ofNullable(
                        mongoTemplate.findOne(
                            query("listId", listId), 
                            Checklist.class))
                    .orElseThrow(() -> new ApiRequestException("Checklist Not Found"));
                
                //Remove Checklist From Original Grouplist
                mongoTemplate.findAndModify(
                    query("groupId", fromGroupId), 
                    pullUpdate("checklists", checklist), 
                    options(true, true), 
                    Grouplist.class);

                Checklist updatedChecklist = mongoTemplate.findAndModify(
                    query("listId", listId), 
                    new Update().set("groupId", toGroupId), 
                    Checklist.class);

                //Add Checklist To Designated Grouplist
                return mongoTemplate.findAndModify(
                    query("groupId", toGroupId), 
                    pushUpdate("checklists", updatedChecklist), 
                    options(true, true), 
                    Grouplist.class);
            } else {
                throw new ApiRequestException("Grouplist Not Found");
            }
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Reorder Grouplist's Checklists || Return Modified Grouplist
    public Grouplist reorderChecklists(String username, String groupId, List<Checklist> reorderChecklists) { 
        if(verifyUser(username)) {
            //Find Grouplist By groupId
            Grouplist grouplist = grouplistRepository
                .findGrouplistByGroupId(groupId)
                .orElseThrow(() -> new ApiRequestException("Grouplist Not Found"));

            //Reorder Grouplist's Checklists
            List<Checklist> checklists = new ArrayList<>();
            for(Checklist modifiedChecklist : reorderChecklists) {
                checklists.add(mongoTemplate.findOne(
                            query("listId", modifiedChecklist.getListId()), 
                            Checklist.class)); 
            }
            grouplist.setChecklists(checklists);
            return grouplistRepository.save(grouplist);
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Remove Checklist From Grouplist | Returns Modified Grouplist
    public Grouplist removeChecklist(String username, String listId, String groupId) {
        if(verifyUser(username)) {
            //Find Checklist By listId
            Checklist checklist = Optional.ofNullable(
                    mongoTemplate.findOne(
                        query("listId", listId), 
                        Checklist.class))
                .orElseThrow(() -> new ApiRequestException("Checklist Not Found"));

            boolean grouplistExists = mongoTemplate.exists(query("groupId", groupId), Grouplist.class);
            if(grouplistExists) {

                //Remove Checklist from Grouplist's 'checklists' attribute
                Grouplist grouplist = mongoTemplate.findAndModify(
                    query("groupId", groupId), 
                    pullUpdate("checklists", checklist), 
                    options(true, true), 
                    Grouplist.class);

                Checklist updatedChecklist = mongoTemplate.findAndModify(
                    query("listId", listId), 
                    new Update().set("groupId", ""), 
                    Checklist.class);

                //Add Checklist to User's 'checklists' attribute
                mongoTemplate.findAndModify(
                        query("username", username), 
                        pushUpdate("checklists", updatedChecklist), 
                        User.class);
                
                return grouplist;
            } else {
                throw new ApiRequestException("Grouplist Not Found");
            }
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Delete Grouplist || Void
    public void deleteGrouplist(String username, String groupId) {
        if(verifyUser(username)) {
            grouplistRepository.deleteGrouplistByGroupId(groupId);
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }
}
