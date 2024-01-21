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
    public Checklist createChecklist(String inputUsername, String title, String listId) {
        String username = inputUsername.trim().toLowerCase();
        if(verifyUser(username)) {
            //Find User Document
            User user = mongoTemplate
                .findOne(new Query().addCriteria(Criteria.where("username").is(username)), 
                    User.class);

            //User's Non-Grouped Checklists
            List<Checklist> checklists = user.getChecklists();
            String trimTitle = title.trim(); //Remove any leading/trailing spaces

            //User's Are Limited to 20 (Non-Grouped) Checklists
            //Checklist Title Are Limited to 50 Characters & Cannot Be Empty
            if(checklists.size() < 20 && trimTitle.length() <= 50 && trimTitle.length() > 1) {
                //Create & Save Checklist
                Checklist checklist = checklistRepository
                    .insert(Checklist.builder()
                            .username(username)
                            .listId(listId)
                            .title(trimTitle)
                            .groupId("")
                            .checkpoints(new ArrayList<>())
                            .completedPoints(new ArrayList<>())
                            .build());
                checklists.add(checklist); //Add Checklist 
                user.setChecklists(checklists); //Update User's Checklists
                mongoTemplate.save(user); //Save Changes to User Document
                return checklist;
            } else {
                if(checklists.size() == 20) {
                    throw new ApiRequestException("User's Checklists Limit Exceeded: 20");
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

    //Edit Checklist's Title || Returns Modified Checklist
    public Checklist editTitle(String listId, String title) {
        //Find Checklist to Update
        Checklist checklist = checklistRepository
            .findChecklistByListId(listId)
            .orElseThrow(() -> new ApiRequestException("Checklist Not Found"));

        //Extract Username
        String username = checklist.getUsername();

        if(verifyUser(username)) {
            String trimTitle = title.trim();
            if(trimTitle.length() <= 50 && trimTitle.length() > 1) {
                checklist.setTitle(title); //Modify Title
                return checklistRepository.save(checklist); //Save Modified Checklist
            } else {
                if(trimTitle.length() > 50) {
                    throw new ApiRequestException("Checklist's Title Cannot Exceed 50 Characters");
                } else {
                    throw new ApiRequestException("Empty Checklist's Title");
                }
            }
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Modify Checklist's Checkpoints || Returns Modified Checklist
    //  * Use to add checkpoints
    //  * Use to remove checkpoints
    //  * Use to reorder checkpoints
    //  * Use to Move checkpoints to completed checkpoints list
    public Checklist editCheckpoints(
            String listId, 
            List<Checkpoint> checkpoints, 
            List<Checkpoint> completedPoints) {

        //Find Checklist to Update
        Checklist checklist = checklistRepository
            .findChecklistByListId(listId)
            .orElseThrow(() -> new ApiRequestException("Checklist Not Found"));

        //Extract Username
        String username = checklist.getUsername();

        if(verifyUser(username)) {
            //Limited to 25 Checkpoints & 25 Completed Checkpoints
            if(checkpoints.size() <= 25 && completedPoints.size() <= 25) {
                checklist.setCheckpoints(checkpoints); //Update Checkpoints
                checklist.setCompletedPoints(completedPoints); //Update Completed Checkpoints
                return checklistRepository.save(checklist); //Save Modified Checklist
            } else {
                //Checkpoints Limit Exceeded
                if(checkpoints.size() > 25) {
                    throw new ApiRequestException("Checkpoints Limit Exceeded: 25");
                //Completed Checkpoints Limit Exceeded
                } else {
                    throw new ApiRequestException("Completed Checkpoints Limit Exceeded: 25");
                }
            }
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Edit Checklist's Grouplist || Return Checklist
    public Checklist editGrouplist(String listId, String toGroupId) {
        //Find Checklist
        Checklist checklist = checklistRepository
            .findChecklistByListId(listId)
            .orElseThrow(() -> new ApiRequestException("Checklist Not Found"));

        //Extract Username
        String username = checklist.getUsername();
        if(verifyUser(username)) {
            //Current Checklist's Group
            String fromGroupId = checklist.getGroupId();

            //Ensure toGroupId & fromGroupId Aren't Matching
            if(!toGroupId.equals(fromGroupId)) {
                //Move Checklist from One Group to Another Group
                if(!toGroupId.equals("") && !fromGroupId.equals("")) {
                    return moveGrouptoGroup(checklist, fromGroupId, toGroupId);
                //Move Non-Grouped Checklist to Grouplist
                } else if(!toGroupId.equals("") && fromGroupId.equals("")) {
                    return moveNongrouptoGroup(username, checklist, toGroupId);
                //Remove Checklist From Grouplist
                } else {
                    return removeFromGroup(username, checklist, fromGroupId);
                }
            } else {
                throw new ApiRequestException("Checklist Already Grouped in GroupId Provided");
            }
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Move Checklist From One Group to Another Group
    private Checklist moveGrouptoGroup(Checklist checklist, String fromGroupId, String toGroupId) {
        //Find Checklist's Original Grouplist
        // Grouplist fromGrouplist = mongoTemplate.findOne(
        //         new Query().addCriteria(Criteria.where("groupId").is(fromGroupId)),
        //         Grouplist.class);
        Grouplist toGrouplist = mongoTemplate.findOne(
                new Query().addCriteria(Criteria.where("groupId").is(toGroupId)),
                Grouplist.class);
        if(toGrouplist == null) throw new ApiRequestException("Grouplist Not Found");

        //Grouplist's Checklists Limit is 20
        if(toGrouplist.getChecklists().size() < 20) {
            //Remove Checklist From Original Grouplist
            mongoTemplate.findAndModify(
                new Query().addCriteria(Criteria.where("groupId").is(fromGroupId)),
                new Update().pull("checklists", checklist),
                new FindAndModifyOptions().returnNew(true).upsert(true),
                Grouplist.class);

            //Update & Save Checklist's GroupId Field
            checklist.setGroupId(toGroupId);
            Checklist updatedChecklist = checklistRepository.save(checklist);
            
            //Add Updated Checklist to New Grouplist
            mongoTemplate.findAndModify(
                new Query().addCriteria(Criteria.where("groupId").is(toGroupId)),
                new Update().push("checklists").value(updatedChecklist),
                new FindAndModifyOptions().returnNew(true).upsert(true),
                Grouplist.class);
            return updatedChecklist;
        } else throw new ApiRequestException("Grouplist's Checklists Limit Exceeded: 20");
    }

    //Move Non-Grouped Checklist to Grouplist
    private Checklist moveNongrouptoGroup(String username, Checklist checklist, String toGroupId) {
        //Find Checklist's New Grouplist
        Grouplist toGrouplist = mongoTemplate.findOne(
                new Query().addCriteria(Criteria.where("groupId").is(toGroupId)),
                Grouplist.class);
        if(toGrouplist == null) throw new ApiRequestException("Grouplist Not Found");

        //Grouplist's Checklists Limit is 20
        if(toGrouplist.getChecklists().size() < 20) {
            //Remove Checklist From User's 'checklists' Field
            mongoTemplate.findAndModify(
                new Query().addCriteria(Criteria.where("username").is(username)),
                new Update().pull("checklists", checklist),
                User.class);

            //Update Checklist's GroupId Field
            checklist.setGroupId(toGroupId);
            Checklist updatedChecklist = checklistRepository.save(checklist);

            //Add Updated Checklist to New Grouplist
            mongoTemplate.findAndModify(
                new Query().addCriteria(Criteria.where("groupId").is(toGroupId)),
                new Update().push("checklists").value(updatedChecklist),
                new FindAndModifyOptions().returnNew(true).upsert(true),
                Grouplist.class);
            return updatedChecklist;

        } else throw new ApiRequestException("Grouplist's Checklists Limit Exceeded: 20");
    }

    //Remove Checklist From Grouplist
    private Checklist removeFromGroup(String username, Checklist checklist, String fromGroupId) {
        //Find User
        User user = mongoTemplate.findOne(
                new Query().addCriteria(Criteria.where("username").is(username)),
                User.class);

        //User's Are Limited to 20 (Non-Grouped) Checklists
        if(user.getChecklists().size() < 20) {
            //Remove Checklist From Original Grouplist
            mongoTemplate.findAndModify(
                new Query().addCriteria(Criteria.where("groupId").is(fromGroupId)),
                new Update().pull("checklists", checklist),
                Grouplist.class);

            checklist.setGroupId("");
            Checklist updatedChecklist = checklistRepository.save(checklist);

            //Add Updated Checklist to User's Checklists
            mongoTemplate.findAndModify(
                new Query().addCriteria(Criteria.where("username").is(username)),
                new Update().push("checklists").value(updatedChecklist),
                User.class);
            return updatedChecklist;

        } else throw new ApiRequestException("User's Checklists Limit Exceeded: 20");
    }

    //Delete Checklist || Void
    public void deleteChecklist(String listId) {
        //Find Checklist Getting Deleted
        Checklist checklist = checklistRepository
            .findChecklistByListId(listId)
            .orElseThrow(() -> new ApiRequestException("Checklist Not Found"));

        //Extract Username
        String username = checklist.getUsername();

        if(verifyUser(username)) {
            String groupId = checklist.getGroupId();
            //Checklist is Non-Grouped
            if(groupId.equals("")) {
                //Find User Document
                User user = mongoTemplate.findOne(new Query()
                        .addCriteria(Criteria.where("username").is(username)), 
                        User.class);
                //Extract User's Checklists
                List<Checklist> checklists = user.getChecklists();

                //Remove Deleted Checklist from User's Checklists
                checklists.removeIf(list -> list.getListId().equals(listId));
                user.setChecklists(checklists); //Update User's Checklists
                mongoTemplate.save(user); //Save Changes to User Document

            //Checklist is Grouped
            } else {
                //Find Group Document
                Grouplist grouplist = mongoTemplate.findOne(new Query()
                        .addCriteria(Criteria.where("groupId").is(groupId)), 
                        Grouplist.class);

                //Extract User's Checklists
                List<Checklist> checklists = grouplist.getChecklists();

                //Remove Deleted Checklist from Grouplist's Checklists
                checklists.removeIf(list -> list.getListId().equals(listId));
                grouplist.setChecklists(checklists); //Update Grouplist's Checklists
                mongoTemplate.save(grouplist); //Save Changes to Grouplist Document
            }

            //Delete Checklist
            checklistRepository
                .deleteChecklistByListId(listId);

        } else {
            throw new ApiRequestException("User Not Found");
        }
    }
}
