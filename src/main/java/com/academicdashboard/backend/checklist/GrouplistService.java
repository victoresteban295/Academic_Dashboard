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
public class GrouplistService {

    private final GrouplistRepository grouplistRepository;
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

    private static Update pullUpdate(String field, Checklist checklist) {
        return new Update().pull(field, checklist); 
    }

    /*********** OPTION DEFINITION METHOD ***********/
    private static FindAndModifyOptions options(boolean returnNew, boolean upsert) {
        return new FindAndModifyOptions().returnNew(returnNew).upsert(upsert);
    }

    /*********** CRUD METHODS ***********/

    //Create New Grouplist | Returns Grouplist Created
    public Grouplist createGrouplist(String username, String title) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        if(currentUser.equals(username)) {
            String groupId = publicId(5);
            Grouplist grouplist = grouplistRepository.insert(
                    Grouplist.builder()
                    .groupId(groupId)
                    .title(title)
                    .checklists(new ArrayList<>())
                    .build()
                    );

            mongoTemplate.update(User.class)
                .matching(Criteria.where("username").is(username))
                .apply(new Update().push("grouplists").value(grouplist))
                .first();

            return grouplist;
        } else {
            throw new ApiRequestException("Provided Wrong Username");
        }
    }

    //Modify Existing Grouplist | Returns Modified Grouplist
    public Grouplist modifyGrouplist(String username, String groupId, String newTitle) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        if(currentUser.equals(username)) {
            if(mongoTemplate.exists(query("groupId", groupId), Grouplist.class)) {
                return mongoTemplate.findAndModify(
                    query("groupId", groupId), 
                    setUpdate("title", newTitle), 
                    options(true, true), 
                    Grouplist.class);
            } else {
                throw new ApiRequestException("Grouplist You Wanted to Modify Doesn't Exist");
            }
        } else {
            throw new ApiRequestException("Provided Wrong Username");
        }
    }

    //Add New Checklist to Grouplist | Returns Grouplist
    public Grouplist addNewToGrouplist(String username, String groupId, String listTitle) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        if(currentUser.equals(username)) {
            String listId = publicId(5);
            Checklist checklist = mongoTemplate.insert(
                    Checklist.builder()
                    .listId(listId)
                    .title(listTitle)
                    .checkpoints(new ArrayList<>())
                    .build()
                    );

            if(mongoTemplate.exists(query("groupId", groupId), Grouplist.class)) {
                return mongoTemplate.findAndModify(
                    query("groupId", groupId), 
                    pushUpdate("checklists", checklist), 
                    options(true, true), 
                    Grouplist.class);
            } else {
                throw new ApiRequestException("Grouplist You Wanted to Modify Doesn't Exist");
            }
        } else {
            throw new ApiRequestException("Provided Wrong Username");
        }
    }

    //Add Existing Checklist to Grouplist | Returns Grouplist
    public Grouplist addExistToGrouplist(String username, String groupId, String listId) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        if(currentUser.equals(username)) {
            //Find Existing Checklist
            Checklist checklist = Optional.ofNullable(
                    mongoTemplate.findOne(
                        query("listId", listId), 
                        Checklist.class))
                .orElseThrow(() -> new ApiRequestException("Checklist You Wanted to Modify Doesn't Exist"));

            //Remove Checklist Obj Reference from the User's checklists attribute
            mongoTemplate.findAndModify(
                    query("username", username), 
                    pullUpdate("checklists", checklist), 
                    User.class);

            if(mongoTemplate.exists(query("groupId", groupId), Grouplist.class)) {
                return mongoTemplate.findAndModify(
                    query("groupId", groupId), 
                    pushUpdate("checklists", checklist), 
                    options(true, true), 
                    Grouplist.class);
            } else {
                throw new ApiRequestException("Grouplist You Wanted to Modify Doesn't Exist");
            }
        } else {
            throw new ApiRequestException("Provided Wrong Username");
        }
    }

    //Remove Existing Checklist From Grouplist | Returns Modified Grouplist
    public Grouplist removefromGrouplist(String username, String groupId, String listId) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        if(currentUser.equals(username)) {
            if(mongoTemplate.exists(query("username", username), User.class)) {
                //Find Existing Checklist
                Checklist checklist = Optional.ofNullable(
                        mongoTemplate.findOne(
                            query("listId", listId), 
                            Checklist.class))
                    .orElseThrow(() -> new ApiRequestException("Checklist You Wanted to Modify Doesn't Exist"));

                if(mongoTemplate.exists(query("groupId", groupId), Grouplist.class)) {
                    //Add Checklist Obj Reference back to User's checklists attribute
                    mongoTemplate.findAndModify(
                            query("username", username), 
                            pushUpdate("checklists", checklist), 
                            User.class);

                    return mongoTemplate.findAndModify(
                        query("groupId", groupId), 
                        pullUpdate("checklists", checklist), 
                        options(true, true), 
                        Grouplist.class);
                } else {
                    throw new ApiRequestException("Grouplist You Wanted to Modify Doesn't Exist");
                }
            } else {
                throw new ApiRequestException("User Not Found");
            }
        } else {
            throw new ApiRequestException("Provided Wrong Username");
        }
    }

    //Delete Grouplist | Void
    public void deleteGrouplist(String username, String groupId, boolean deleteAll) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        if(currentUser.equals(username)) {
            Grouplist grouplist = Optional.ofNullable(
                    mongoTemplate.findOne(
                        query("groupId", groupId), 
                        Grouplist.class))
                .orElseThrow(() -> new ApiRequestException("Grouplist You Wanted to Delete Doesn't Exist"));

            List<Checklist> checklists = grouplist.getChecklists(); //Checklist Under Grouplist

            if (deleteAll) {
                //Deleting Completely All Checklist Found within Deleted Grouplist
                for(Checklist list : checklists) {
                    mongoTemplate.remove(
                            query("listId", list.getListId()), 
                            Checklist.class);
                }
            } else {
                //Move Checklist Found within Deleted Grouplist to User's Checklists attribute
                for(Checklist list : checklists) {
                    mongoTemplate.findAndModify(
                            query("username", username), 
                            pushUpdate("checklists", list), 
                            User.class);
                }
            }

            //Delete Grouplist
            mongoTemplate.remove(
                    query("groupId", groupId), 
                    Grouplist.class);
        } else {
            throw new ApiRequestException("Provided Wrong Username");
        }
    }
}
