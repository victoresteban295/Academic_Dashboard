package com.academicdashboard.backend.checklist;

import java.util.ArrayList;
import java.util.List;

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

    //Create New Grouplist | Returns Grouplist 
    public Grouplist createGrouplist(String username, String title, String groupId) {
        if(verifyUser(username)) {
            //Find User Document
            User user = mongoTemplate
                .findOne(new Query().addCriteria(Criteria.where("username").is(username)), 
                    User.class);

            //User's Grouplists
            List<Grouplist> grouplists = user.getGrouplists();
            String trimTitle = title.trim();

            //User's Are Limited to 20 Grouplists
            //Grouplist Title Are Limited to 20 Characters & Cannot Be Empty
            if(grouplists.size() < 20 && trimTitle.length() <= 20 && trimTitle.length() > 1) {
                //Create Grouplist
                Grouplist grouplist = grouplistRepository.insert(
                        Grouplist.builder()
                        .username(username)
                        .groupId(groupId)
                        .title(trimTitle)
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
                if(grouplists.size() == 20) {
                    throw new ApiRequestException("User's Grouplists Limit Exceeded: 20");
                } else if(trimTitle.length() > 20) {
                    throw new ApiRequestException("Grouplist's Title Cannot Exceed 20 Characters");
                } else {
                    throw new ApiRequestException("Empty Grouplist's Title");
                }
            }
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Edit Grouplist's Title || Return Grouplist
    public Grouplist editTitle(String groupId, String title) {
        //Find Grouplist To Modify
        Grouplist grouplist = grouplistRepository
            .findGrouplistByGroupId(groupId)
            .orElseThrow(() -> new ApiRequestException("Grouplist Not Found"));

        //Extract Username
        String username = grouplist.getUsername();

        if(verifyUser(username)) {
            String trimTitle = title.trim();
            if(trimTitle.length() <= 20 && trimTitle.length() > 1) {
                grouplist.setTitle(trimTitle); //Set New Title
                return grouplistRepository.save(grouplist); //Save & Return Modified Grouplist
            } else {
                if(trimTitle.length() > 20) {
                    throw new ApiRequestException("Grouplist's Title Cannot Exceed 20 Characters");
                } else {
                    throw new ApiRequestException("Empty Grouplist's Title");
                }
            }
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Edit Grouplist's Checklists || Return Grouplist
    public Grouplist editChecklists(String groupId, List<Checklist> checklists) {
        //Find Grouplist 
        Grouplist grouplist = grouplistRepository
            .findGrouplistByGroupId(groupId)
            .orElseThrow(() -> new ApiRequestException("Grouplist Not Found"));

        //Extract Username
        String username = grouplist.getUsername();
        if(verifyUser(username)) {
            //Edited Checklists
            List<Checklist> editChecklists = new ArrayList<>();

            //Extract Checklist From Database
            for(Checklist checklist : checklists) {
                Checklist list = mongoTemplate.findOne(
                    query("listId", checklist.getListId()), 
                    Checklist.class);
                editChecklists.add(list);
            }


            grouplist.setChecklists(editChecklists);
            return grouplistRepository.save(grouplist);
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Create New Checklist Under Groulist || Return Grouplist
    public Grouplist createChecklist(String groupId, String listId, String title) {
        //Find Grouplist
        Grouplist grouplist = grouplistRepository
            .findGrouplistByGroupId(groupId)
            .orElseThrow(() -> new ApiRequestException("Grouplist Not Found"));

        //Extract Username
        String username = grouplist.getUsername();
        if(verifyUser(username)) {
            //Extract Checklists
            List<Checklist> checklists = grouplist.getChecklists();
            String trimTitle = title.trim(); //Trim New Title

            //Checklists Limit Size = 20 || Tilte Limit Size = 50 chars 
            if(checklists.size() < 20 && trimTitle.length() <= 50 && trimTitle.length() > 1) {
                //Create & Save Checklist
                Checklist checklist = mongoTemplate.insert(Checklist.builder()
                        .username(username)
                        .listId(listId)
                        .title(trimTitle)
                        .groupId(groupId)
                        .checkpoints(new ArrayList<>())
                        .completedPoints(new ArrayList<>())
                        .build());

                //Add Updated Checklists to Grouplist
                return mongoTemplate.findAndModify(
                    query("groupId", groupId), 
                    pushUpdate("checklists", checklist), 
                    options(true, true), 
                    Grouplist.class);
            } else {
                if(checklists.size() == 20) {
                    throw new ApiRequestException("Grouplist's Checklists Limit Exceeded: 20");
                } else if(trimTitle.length() > 50) {
                    throw new ApiRequestException("Checklist's Title Cannot Exceed 50 Characters");
                } else {
                    throw new ApiRequestException("Empty Checklist's Title");
                }
            }
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Delete Grouplist || Void
    public void deleteGrouplist(String groupId) {
        //Find Grouplist Getting Deleted
        Grouplist grouplist = grouplistRepository
            .findGrouplistByGroupId(groupId)
            .orElseThrow(() -> new ApiRequestException("Grouplist Not Found"));

        String username = grouplist.getUsername();
        if(verifyUser(username)) {
            //Find User Document
            User user = mongoTemplate
                .findOne(new Query().addCriteria(Criteria.where("username").is(username)), 
                    User.class);

            //Delete Grouplist From User Document
            List<Grouplist> grouplists = user.getGrouplists();
            grouplists.removeIf(group -> group.getGroupId().equals(groupId));
            user.setGrouplists(grouplists);
            mongoTemplate.save(user);

            //Delete All Checklist Under Grouplist
            List<Checklist> checklists = grouplist.getChecklists();
            for(Checklist checklist : checklists) {
                //Delete Checklist
                mongoTemplate.findAndRemove(new Query()
                        .addCriteria(Criteria.where("listId").is(checklist.getListId())), 
                        Checklist.class); 
            }

            //Delete Grouplist
            grouplistRepository.deleteGrouplistByGroupId(groupId);
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }
}
