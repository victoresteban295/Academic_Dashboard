package com.academicdashboard.backend.checklist;

import java.util.ArrayList;
import java.util.Random;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
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

    //Verify Username Matches Logged-in User
    private boolean verifyUser(String username) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        return currentUser.equals(username);
    }

    /************************************/
    /*********** CRUD METHODS ***********/
    /************************************/

    //Create New Grouplist | Returns Grouplist Created
    public Grouplist createGrouplist(String username, String title) {
        if(verifyUser(username)) {
            String groupId = publicId(10); //Create Grouplist's grouId
            //Create Grouplist
            Grouplist grouplist = grouplistRepository.insert(
                    Grouplist.builder()
                    .groupId(groupId)
                    .title(title)
                    .checklists(new ArrayList<>())
                    .build());

            //Add New Grouplist to User
            mongoTemplate.update(User.class)
                .matching(Criteria.where("username").is(username))
                .apply(new Update().push("grouplists").value(grouplist));

            return grouplist;
        } else {
            throw new ApiRequestException("Username Not Found");
        }
    }

    //Modify Grouplist || Return Modified Grouplist
    public Grouplist modifyGrouplist(String username, Grouplist modifiedGrouplist) {
        if(verifyUser(username)){
            return grouplistRepository.save(modifiedGrouplist);
        } else {
            throw new ApiRequestException("Username Not Found");
        }
    }

    //Delete Grouplist || Void
    public void deleteGrouplist(String username, String groupId) {
        if(verifyUser(username)) {
            grouplistRepository.deleteGrouplistByGroupId(groupId);
        } else {
            throw new ApiRequestException("Username Not Found");
        }
    }
}
