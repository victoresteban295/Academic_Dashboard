package com.academicdashboard.backend.checklist;

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
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CheckpointService {

    private final CheckpointRepository checkpointRepository;
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
    // private static Update setUpdate(String field, String value) {
    //     return new Update().set(field, value);
    // }

    private static Update pushUpdate(String field, Checkpoint checkpoint) {
        return new Update().push(field).value(checkpoint);
    }

    private static Update pullUpdate(String field, Checkpoint checkpoint) {
        return new Update().pull(field, checkpoint); 
    }

    /*********** OPTION DEFINITION METHOD ***********/
    private static FindAndModifyOptions options(boolean returnNew, boolean upsert) {
        return new FindAndModifyOptions().returnNew(returnNew).upsert(upsert);
    }


    /*********** CRUD METHODS ***********/

    //Create New Checkpoint Into Existing Checklist | Returns Checklist
    public Checklist addCheckpoint(String username, String listId, String content) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        if(currentUser.equals(username)) {
            if (mongoTemplate.exists(query("listId", listId), Checklist.class)) {
                String pointId = publicId(5);
                Checkpoint checkpoint = checkpointRepository.insert(
                        Checkpoint.builder()
                        .pointId(pointId)
                        .content(content)
                        .isComplete(false)
                        .isSubpoint(false)
                        .build()
                        );
                return mongoTemplate.findAndModify(
                        query("listId", listId), 
                        pushUpdate("checkpoints", checkpoint), 
                        options(true, true), 
                        Checklist.class);
            } else {
                throw new ApiRequestException("Checklist You Provided Doesn't Exist");
            }
        } else {
            throw new ApiRequestException("Provided Wrong Username");
        }
    }

    //Modify Existing Checkpoint | Returns Modified Checkpoint
    public Checkpoint modifyCheckpoint(String username, String pointId, String content) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        if(currentUser.equals(username)) {
            Checkpoint checkpoint = checkpointRepository
                .findCheckpointByPointId(pointId)
                .orElseThrow(() -> new ApiRequestException("Checkpoint You Provided Doesn't Exist"));
            checkpoint.setContent(content);
            return checkpointRepository.save(checkpoint);
        } else {
            throw new ApiRequestException("Provided Wrong Username");
        }
    }

    //Delete Checkpoint | Void
    //NOTE: Deleteing Checkpoint Automatically Removes its Reference in Checklist
    public void deleteCheckpoint(String username, String pointId) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        if(currentUser.equals(username)) {
            Checkpoint checkpoint = checkpointRepository
                .findCheckpointByPointId(pointId)
                .orElseThrow(() -> new ApiRequestException("Checkpoint You Wanted to Delete Doesn't Exist"));

            //Delete Subcheckpoints 
            if(!checkpoint.getSubCheckpoints().isEmpty()) {
                List<Checkpoint> subpoints = checkpoint.getSubCheckpoints();
                for(Checkpoint subpoint : subpoints) {
                    checkpointRepository.delete(subpoint);
                }
                checkpointRepository.delete(checkpoint);
            } else {
                checkpointRepository.delete(checkpoint);
            }
        } else {
            throw new ApiRequestException("Provided Wrong Username");
        }
    }

    //Existing Checkpoint to Subcheckpoint | Return Checkpoint w/ Subpoints
    public Checkpoint turnIntoSubcheckpoint(String username, String listId, String pointId, String subpointId) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        if(currentUser.equals(username)) {
            if(mongoTemplate.exists(query("listId", listId), Checklist.class)) {
                Checkpoint checkpoint = checkpointRepository
                    .findCheckpointByPointId(pointId)
                    .orElseThrow(() -> new ApiRequestException("Parent Checkpoint You Provided Doesn't Exist"));

                Checkpoint subcheckpoint = checkpointRepository
                    .findCheckpointByPointId(subpointId)
                    .orElseThrow(() -> new ApiRequestException("SubCheckpoint You Provided Doesn't Exist"));

                if(!checkpoint.isSubpoint() || !subcheckpoint.isSubpoint()) {
                    //Remove from Checklist's checkpoints attribute
                    mongoTemplate.findAndModify(
                            query("listId", listId), 
                            pullUpdate("checkpoints", subcheckpoint), 
                            Checklist.class);
                    subcheckpoint.setSubpoint(true); //Stamp as Subpoint
                    //Insert to Checkpoint's subcheckpoints attribute
                    subcheckpoint = checkpointRepository.save(subcheckpoint);
                    return mongoTemplate.findAndModify(
                            query("pointId", pointId), 
                            pushUpdate("subCheckpoints", subcheckpoint), 
                            options(true, true), 
                            Checkpoint.class);
                } else {
                    throw new ApiRequestException("Cannot add Subcheckpoint Under a Subcheckpoint");
                }
            } else {
                throw new ApiRequestException("Checklist You Provided Doesn't Exist");
            }
        } else {
            throw new ApiRequestException("Provided Wrong Username");
        }
    }
    
    //Create New SubCheckpoint under Checkpoint | Return Checkpoint
    public Checkpoint newSubcheckpoint(String username, String pointId, String content) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        if(currentUser.equals(username)) {
            if(mongoTemplate.exists(query("pointId", pointId), Checkpoint.class)) {
                //Create New Checkpoint Object as Subcheckpoint
                String subpointId = publicId(5);
                Checkpoint subcheckpoint = checkpointRepository.insert(
                        Checkpoint.builder()
                        .pointId(subpointId)
                        .content(content)
                        .isComplete(false)
                        .isSubpoint(true)
                        .build()
                        );

                //Add Subcheckpoint to Checkpoint
                return mongoTemplate.findAndModify(
                        query("pointId", pointId), 
                        pushUpdate("subCheckpoints", subcheckpoint), 
                        options(true, true), 
                        Checkpoint.class);
            } else {
                throw new ApiRequestException("Parent Checkpoint You Provided Doesn't Exist");
            }
        } else {
            throw new ApiRequestException("Provided Wrong Username");
        }
    }

    //Subcheckpoint to Checkpoint | Return Checklist
    public Checklist reverseSubcheckpoint(String username, String listId, String pointId, String subpointId) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        if(currentUser.equals(username)) {
            if(mongoTemplate.exists(query("listId", listId), Checklist.class)) {
                Checkpoint subpoint = checkpointRepository
                    .findCheckpointByPointId(subpointId)
                    .orElseThrow(() -> new ApiRequestException("SubCheckpoint You Provided Doesn't Exist"));
                checkpointRepository
                    .findCheckpointByPointId(pointId)
                    .orElseThrow(() -> new ApiRequestException("Parent Checkpoint You Provided Doesn't Exist"));
                
                //Remove from Checkpoint's checkpoints atrribute
                mongoTemplate.findAndModify(
                        query("pointId", pointId), 
                        pullUpdate("subCheckpoints", subpoint), 
                        Checkpoint.class);
                subpoint.setSubpoint(false); //Stamp as Checkpoint
                subpoint = checkpointRepository.save(subpoint);
                return mongoTemplate.findAndModify(
                        query("listId", listId), 
                        pushUpdate("checkpoints", subpoint), 
                        options(true, true), 
                        Checklist.class);
            } else {
                throw new ApiRequestException("Checklist You Provided Doesn't Exist");
            }
        } else {
            throw new ApiRequestException("Provided Wrong Username");
        }
    }

    //Check off Complete Property on Checkpoint | Return Checkpoint
    public Checkpoint completeCheckpoint(String  username, String pointId) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        if(currentUser.equals(username)) {
            Query query = query("pointId", pointId);
            Checkpoint checkpoint = Optional.ofNullable(
                    mongoTemplate.findOne(
                            query, 
                            Checkpoint.class))
                .orElseThrow(() -> new ApiRequestException("Checkpoint You Provided Doesn't Exist"));

            return mongoTemplate.findAndModify(
                        query, 
                        new Update().set("isComplete", !checkpoint.isComplete()), 
                        options(true, true), 
                        Checkpoint.class);
        } else {
            throw new ApiRequestException("Provided Wrong Username");
        }
    }

}
