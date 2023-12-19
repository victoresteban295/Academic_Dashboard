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
            //Find User Document
            User user = mongoTemplate
                .findOne(new Query().addCriteria(Criteria.where("username").is(username)), 
                    User.class);

            //User's Grouplists
            List<Grouplist> grouplists = user.getGrouplists();
            String trimTitle = title.trim();

            //User's Are Limited to 20 Grouplists
            //Grouplist Title Are Limited to 20 Characters & Cannot Be Empty
            if(grouplists.size() <= 20 && trimTitle.length() <= 20 && trimTitle.length() > 1) {
                //Create Grouplist
                Grouplist grouplist = grouplistRepository.insert(
                        Grouplist.builder()
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
                if(grouplists.size() > 20) {
                    throw new ApiRequestException("User's Grouplists Limit Exceeded: 20");
                } else if(trimTitle.length() > 20) {
                    throw new ApiRequestException("Grouplist's Title Cannot Exceeded 20 Characters");
                } else {
                    throw new ApiRequestException("Empty Grouplist's Title");
                }
            }
        } else {
            throw new ApiRequestException("Username Not Found");
        }
    }

    //Modify Grouplist's Title || Return Modified Grouplist
    public Grouplist modifyTitle(String username, String groupId, String title) {
        if(verifyUser(username)) {
            String trimTitle = title.trim();
            if(trimTitle.length() <= 50 && trimTitle.length() > 1) {
                //Find Grouplist To Modify
                Grouplist grouplist = grouplistRepository
                    .findGrouplistByGroupId(groupId)
                    .orElseThrow(() -> new ApiRequestException("Grouplist Not Found"));
                grouplist.setTitle(trimTitle); //Set New Title
                return grouplistRepository.save(grouplist); //Save & Return Modified Grouplist
            } else {
                if(trimTitle.length() > 20) {
                    throw new ApiRequestException("Grouplist's Title Cannot Exceeded 20 Characters");
                } else {
                    throw new ApiRequestException("Empty Grouplist's Title");
                }
            }
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Reorder User's Grouplists || Returns Re-ordered Grouplists
    public List<Grouplist> reorderGrouplists(String username, List<Grouplist> grouplists) {
        if(verifyUser(username)) {
            //Re-order Grouplists
            List<Grouplist> reorderGrouplists = new ArrayList<>();

            //Loop thru grouplists & add grouplist to Re-ordered Grouplists
            for(Grouplist grouplist: grouplists) {
                //Extract Grouplist Object
                Grouplist group = grouplistRepository
                    .findGrouplistByGroupId(grouplist.getGroupId())
                    .orElseThrow(() -> new ApiRequestException("Grouplist Not Found"));
                //Add to Re-ordered Grouplists
                reorderGrouplists.add(group);
            }

            //Find User whose List is Updating
            User user = mongoTemplate
                .findOne(query("username", username),User.class);

            //Update User's Grouplists
            user.setGrouplists(reorderGrouplists);
            mongoTemplate.save(user); //Save Changes of User Document

            return reorderGrouplists;
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Re-order Grouplist's Checklists || Return Modified Grouplist
    public Grouplist reorderGroupChecklists(String username, Grouplist grouplist) {
        if(verifyUser(username)) {
            //Extract groupId & new checklists from Grouplist
            String groupId = grouplist.getGroupId(); 
            List<Checklist> checklists = grouplist.getChecklists();

            //Re-ordered Checklists
            List<Checklist> reorderLists = new ArrayList<>();

            //Extract Checklist From Database
            for(Checklist checklist : checklists) {
                Checklist list = mongoTemplate.findOne(
                    query("listId", checklist.getListId()), 
                    Checklist.class);
                reorderLists.add(list);
            }

            Grouplist editGrouplist = grouplistRepository
                .findGrouplistByGroupId(groupId)
                .orElseThrow(() -> new ApiRequestException("Grouplist Not Found"));

            editGrouplist.setChecklists(reorderLists);
            return grouplistRepository.save(editGrouplist);
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Create & Add New Checklist to Grouplist || Return Modified Grouplist
    public Grouplist createChecklist(String username, String groupId, String listId, String title) {
        if(verifyUser(username)) {
            //Find Grouplist
            Grouplist grouplist = grouplistRepository
                .findGrouplistByGroupId(groupId)
                .orElseThrow(() -> new ApiRequestException("Grouplist Not Found"));

            List<Checklist> checklists = grouplist.getChecklists();
            String trimTitle = title.trim();
            if(checklists.size() <= 20 && trimTitle.length() <= 50 && trimTitle.length() > 1) {
                //Create & Save Checklist
                Checklist checklist = mongoTemplate.insert(Checklist.builder()
                        .listId(listId)
                        .title(title)
                        .groupId(groupId)
                        .checkpoints(new ArrayList<>())
                        .completedPoints(new ArrayList<>())
                        .build());

                return mongoTemplate.findAndModify(
                    query("groupId", groupId), 
                    pushUpdate("checklists", checklist), 
                    options(true, true), 
                    Grouplist.class);
            } else {
                if(checklists.size() > 20) {
                    throw new ApiRequestException("Grouplist's Checklists Limit Exceeded: 20");
                } else if(trimTitle.length() > 50) {
                    throw new ApiRequestException("Checklist's Title Cannot Exceeded 50 Characters");
                } else {
                    throw new ApiRequestException("Empty Checklist's Title");
                }
            }
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Add Existing Checklist to Grouplist || Return Modified Grouplist
    public Grouplist addChecklist(String username, String listId, String groupId) {
        if(verifyUser(username)) {
            Grouplist grouplist = grouplistRepository
                .findGrouplistByGroupId(groupId)
                .orElseThrow(() -> new ApiRequestException("Grouplist Not Found"));

            //Each Grouplist is Limited to 20 Checklists
            List<Checklist> checklists = grouplist.getChecklists();
            if(checklists.size() <= 20) {
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

                return mongoTemplate.findAndModify(
                    query("groupId", groupId), 
                    pushUpdate("checklists", updatedChecklist), 
                    options(true, true), 
                    Grouplist.class);
            } else {
                throw new ApiRequestException("Grouplist's Checklist Limit Exceeded: 20");
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
            if(fromExists) {
                //Find Destinated Grouplist
                Grouplist toGrouplist = grouplistRepository
                    .findGrouplistByGroupId(toGroupId)
                    .orElseThrow(() -> new ApiRequestException("Grouplist Not Found"));

                List<Checklist> toChecklists = toGrouplist.getChecklists();
                if(toChecklists.size() <= 20) {
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
                    throw new ApiRequestException("Grouplist's Checklist Limit Exceeded: 20");
                }
            } else {
                throw new ApiRequestException("Grouplist Not Found");
            }
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Remove Checklist From Grouplist | Returns Modified Grouplist
    public Grouplist removeChecklist(String username, String listId, String groupId) {
        if(verifyUser(username)) {
            //Find User Document
            User user = mongoTemplate
                .findOne(new Query().addCriteria(Criteria.where("username").is(username)), 
                    User.class);

            //User's Non-Grouped Checklists
            List<Checklist> checklists = user.getChecklists();

            //User's Are Limited to 20 (Non-Grouped) Checklists
            if(checklists.size() <= 20) {
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
                throw new ApiRequestException("User's Checklists Limit Exceeded: 20");
            }
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Delete Grouplist || Void
    public void deleteGrouplist(String username, String groupId) {
        if(verifyUser(username)) {
            //Find User Document
            User user = mongoTemplate
                .findOne(new Query().addCriteria(Criteria.where("username").is(username)), 
                    User.class);

            //Delete Grouplist From User Document
            List<Grouplist> grouplists = user.getGrouplists();
            grouplists.removeIf(grouplist -> grouplist.getGroupId().equals(groupId));
            user.setGrouplists(grouplists);
            mongoTemplate.save(user);

            //Find Grouplist Getting Deleted
            Grouplist grouplist = grouplistRepository
                .findGrouplistByGroupId(groupId)
                .orElseThrow(() -> new ApiRequestException("Grouplist Not Found"));

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
