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
    public List<Checklist> getChecklists(String inputUsername) {
        String username = inputUsername.trim().toLowerCase();
        if(verifyUser(username)) {
            return userRepository.findUserByUsername(username)
                .get().getChecklists();
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Get User's Grouplists || Return List<Grouplist> 
    public List<Grouplist> getGrouplists(String inputUsername) {
        String username = inputUsername.trim().toLowerCase();
        if(verifyUser(username)) {
            return userRepository.findUserByUsername(username)
                .get().getGrouplists();
        } else {
            throw new ApiRequestException("User Not Found");
        }
    }

    //Get All User's Checklists || Return List<Checklist>
    public List<Checklist> getAllChecklists(String inputUsername) {
        String username = inputUsername.trim().toLowerCase();
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
    public List<Checklist> reorderChecklists(String inputUsername, List<Checklist> reorderChecklists) {
        String username = inputUsername.trim().toLowerCase();
        if(verifyUser(username)) {
            //User's Limited to 20 Checklists
            if(reorderChecklists.size() > 20) throw new ApiRequestException("User's Checklists Limit Exceeded: 20");

            //Find User & Update Checklist
            User user = userRepository
                .findUserByUsername(username)
                .orElseThrow(() -> new ApiRequestException("User Not Found"));

            //Reorder Checklists
            List<Checklist> checklists = new ArrayList<>(); //Updated Checklists
            for(Checklist list : reorderChecklists) {
                Checklist checklist = mongoTemplate.findOne(
                            query("listId", list.getListId()), 
                            Checklist.class); 
                if(checklist == null) throw new ApiRequestException("Checklist Not Found");

                //Ensure Checklist Belongs to User and is Non-Grouped
                if(checklist.getUsername().equals(username) && checklist.getGroupId().equals("")) {
                    checklists.add(checklist);
                } else throw new ApiRequestException("Checklist Not Found");
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
    public List<Grouplist> reorderGrouplists(String inputUsername, List<Grouplist> reorderGrouplists) {
        String username = inputUsername.trim().toLowerCase();
        if(verifyUser(username)) {
            //User's Limited to 20 Grouplists
            if(reorderGrouplists.size() > 20) throw new ApiRequestException("User's Grouplist Limit Exceeded: 20");

            //Find User & Update Grouplist
            User user = userRepository
                .findUserByUsername(username)
                .orElseThrow(() -> new ApiRequestException("User Not Found"));

            //Reorder Grouplists
            List<Grouplist> grouplists = new ArrayList<>(); //Updated Grouplists
            for(Grouplist grouplist : reorderGrouplists) {
                Grouplist group = mongoTemplate.findOne(
                            query("groupId", grouplist.getGroupId()), 
                            Grouplist.class); 
                //Grouplist Not Found
                if(group == null) throw new ApiRequestException("Grouplist Not Found");

                //Ensure Grouplist Belongs to User
                if(group.getUsername().equals(username)) {
                    grouplists.add(group); 
                } else throw new ApiRequestException("Grouplist Not Found");
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
        //Keywords that are not allowed in a username
        String user = username.toLowerCase().trim();
        boolean testProf = user.contains("test") && user.contains("professor");
        boolean testStud = user.contains("test") && user.contains("student");
        boolean demoProf = user.contains("demo") && user.contains("professor");
        boolean demoStud = user.contains("demo") && user.contains("student");
        boolean userUnique = testProf && testStud && demoProf && demoStud;

        //True = Exist || False == Doesn't Exist
        boolean exists = userRepository.findUserByUsername(user).isPresent();
        if(exists || !userUnique) {
            throw new ApiRequestException("Username Is Already Taken");
        }
    }
    public void emailExist(String inputEmail) {
        String email = inputEmail.trim().toLowerCase();
        //True = Exist || False == Doesn't Exist
        boolean exists = userRepository.findUserByEmail(email).isPresent();
        if(exists) {
            throw new ApiRequestException("Email Is Already Taken");
        }
    }
    public void phoneExist(String inputPhone) {
        String phone = inputPhone.trim();
        //True = Exist || False == Doesn't Exist
        //True = Exist || False == Doesn't Exist
        boolean exists = userRepository.findUserByPhone(phone).isPresent();
        if(exists) {
            throw new ApiRequestException("Phone Is Already Taken");
        }
    }
}
