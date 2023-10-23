package com.academicdashboard.backend.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
// import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.academicdashboard.backend.checklist.Checklist;
// import org.springframework.data.mongodb.core.query.Update;
// import com.academicdashboard.backend.checklist.Checklist;
import com.academicdashboard.backend.checklist.Grouplist;
import com.academicdashboard.backend.exception.ApiRequestException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    /*********** QUERY DEFINITION METHOD ***********/
    private static Query query(String field, String equalsValue) {
        return new Query().addCriteria(Criteria.where(field).is(equalsValue));
    } 

    // /*********** UPDATE DEFINITION METHODS ***********/
    // private static Update setUpdate(String field, String value) {
    //     return new Update().set(field, value);
    // }
    //
    // private static Update pushUpdate(String field, Checklist checklist) {
    //     return new Update().push(field).value(checklist);
    // }
    //
    // /*********** OPTION DEFINITION METHOD ***********/
    // private static FindAndModifyOptions options(boolean returnNew, boolean upsert) {
    //     return new FindAndModifyOptions().returnNew(returnNew).upsert(upsert);
    // }

    private boolean verifyUser(String username) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        return currentUser.equals(username);
    }

    // public User getUserDetails(String username, String role) {
    //     if(verifyUser(username, role)) {
    //         return userRepository.findUserByUsername(username)
    //             .orElseThrow(() -> new ApiRequestException("Provided Wrong Username"));
    //     } else {
    //         throw new ApiRequestException("Provided Wrong Username");
    //     }
    // }
    
    /***********************************/
    /* ********** Checklist ********** */
    /***********************************/

    //Get User's Checklists || Return List<Checklist>
    public List<Checklist> getChecklists(String username) {
        if(verifyUser(username)) {
            return userRepository.findUserByUsername(username)
                .get().getChecklists();
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Get User's Grouplists || Return List<Grouplist> 
    public List<Grouplist> getGrouplists(String username) {
        if(verifyUser(username)) {
            return userRepository.findUserByUsername(username)
                .get().getGrouplists();
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Get All User's Checklists || Return List<Checklist>
    public List<Checklist> getAllChecklists(String username) {
        if(verifyUser(username)) {
            User user = userRepository
                .findUserByUsername(username)
                .orElseThrow(() -> new ApiRequestException("User Not Found"));
            List<Checklist> allChecklists = user.getChecklists(); //Get All User's Checklists
            List<Grouplist> grouplists = user.getGrouplists(); //Get All User's Grouplists

            //Attach All Grouped Checklist to allChecklists
            for(Grouplist grouplist : grouplists) {
                List<Checklist> checklists = grouplist.getChecklists();
                for(Checklist checklist : checklists) {
                    allChecklists.add(checklist);
                }
            }
            return allChecklists; //All User's Checklists
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Reorder User's Checklists || Return List<Checklist> 
    public List<Checklist> reorderChecklists(String username, List<Checklist> reorderChecklists) {
        if(verifyUser(username)) {
            //Find User & Update Checklist
            User user = userRepository
                .findUserByUsername(username)
                .orElseThrow(() -> new ApiRequestException("User Not Found"));

            //Reorder Checklists
            List<Checklist> checklists = new ArrayList<>(); //Updated Checklists
            for(Checklist checklist : reorderChecklists) {
                checklists.add(mongoTemplate.findOne(
                            query("listId", checklist.getListId()), 
                            Checklist.class)); 
            }

            //Update & Save User with Updated Checklist
            user.setChecklists(checklists); 
            userRepository.save(user); 
            return user.getChecklists(); 
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Reorder User's Grouplists || Return List<Grouplist>
    public List<Grouplist> reorderGrouplists(String username, List<Grouplist> reorderGrouplists) {
        if(verifyUser(username)) {
            //Find User & Update Grouplist
            User user = userRepository
                .findUserByUsername(username)
                .orElseThrow(() -> new ApiRequestException("User Not Found"));

            //Reorder Grouplists
            List<Grouplist> grouplists = new ArrayList<>(); //Updated Grouplists
            for(Grouplist grouplist : reorderGrouplists) {
                grouplists.add(mongoTemplate.findOne(
                            query("groupId", grouplist.getGroupId()), 
                            Grouplist.class)); 
            }

            //Update & Save User with Updated Grouplist
            user.setGrouplists(grouplists); 
            userRepository.save(user); 
            return user.getGrouplists(); 
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    /**************************************/
    /* ********** Authenticate ********** */
    /**************************************/

    /* Username, Email, Phone Validation */
    public void usernameExist(String username) {
        //True = Exist || False == Doesn't Exist
        boolean exists = userRepository.findUserByUsername(username).isPresent();
        if(exists) {
            throw new ApiRequestException("Username Is Already Taken");
        }
    }
    public void emailExist(String email) {
        //True = Exist || False == Doesn't Exist
        boolean exists = userRepository.findUserByEmail(email).isPresent();
        if(exists) {
            throw new ApiRequestException("Email Is Already Taken");
        }
    }
    public void phoneExist(String phone) {
        //True = Exist || False == Doesn't Exist
        boolean exists = userRepository.findUserByPhone(phone).isPresent();
        if(exists) {
            throw new ApiRequestException("Phone Is Already Taken");
        }
    }
}
