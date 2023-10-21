package com.academicdashboard.backend.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
// import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
// import org.springframework.data.mongodb.core.query.Update;
// import com.academicdashboard.backend.checklist.Checklist;
import com.academicdashboard.backend.checklist.Grouplist;
import com.academicdashboard.backend.checklist.RefList;
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

    //Get User's Checklist || Return Checklists
    public List<RefList> getUserChecklists(String username) {
        if(verifyUser(username)) {
            List<RefList> checklists = userRepository
                .findUserByUsername(username)
                .get()
                .getChecklists();
            return checklists;
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Get User's Grouplist || Return Grouplist
    public List<Grouplist> getUserGrouplists(String username) {
        if(verifyUser(username)) {
            return userRepository
                .findUserByUsername(username)
                .get()
                .getGrouplists();
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Determine If User Has Checklists || Returns First ListId
    public String getUserFirstCheclist(String username) {
        if(verifyUser(username)) {
            //Find User By Username
            User user = userRepository
                .findUserByUsername(username)
                .orElseThrow(() -> new ApiRequestException("User Not Found"));

            //Get User's Checlists and Grouplist
            List<RefList> checklists = user.getChecklists();
            List<Grouplist> grouplists = user.getGrouplists();

            //If Found, Returns First listId in User's Checklist
            if(checklists.size() > 0) {
                return checklists.get(0).getListId();

            //Else, Returns First listId it Encounters in User's Grouplist
            } else if(grouplists.size() > 0){
                for(Grouplist grouplist : grouplists) {
                    List<RefList> lists = grouplist.getChecklists();
                    if(lists.size() > 0) {
                        return lists.get(0).getListId();
                    }
                }
            } 
            return ""; //User Has No Checklist
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Modify/Rearrange User's Checklists || Returns Checklists
    public List<RefList> modifyUserChecklists(String username, List<RefList> checklists) {
        if(verifyUser(username)) {
            User user = userRepository
                .findUserByUsername(username)
                .orElseThrow(() -> new ApiRequestException("User Not Found"));
            user.setChecklists(checklists); //Set Checklist's Modifications
            userRepository.save(user); //Save User with Updated Checklists
            return user.getChecklists(); 
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Modify/Rearrange User's Grouplists || Returns Grouplists
    public List<Grouplist> modifyGrouplists(String username, List<Grouplist> rearrangeGroups) {
        if(verifyUser(username)) {
            User user = userRepository
                .findUserByUsername(username)
                .orElseThrow(() -> new ApiRequestException("Provided Wrong Username"));
            List<Grouplist> grouplists = new ArrayList<>();
            for(Grouplist rearrangeGroup : rearrangeGroups) {
                grouplists.add(mongoTemplate.findOne(query("groupId", rearrangeGroup.getGroupId()), Grouplist.class)); 
            }
            user.setGrouplists(grouplists); //Update User's Checklists
            userRepository.save(user); //Save User with Updated Checklists
            return user.getGrouplists(); 
        } else {
            throw new ApiRequestException("Wrong Username Provided");
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
