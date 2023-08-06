package com.academicdashboard.backend.checklist;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrouplistRepository extends MongoRepository<Grouplist, ObjectId> {
    Optional<Grouplist> findGrouplistByGroupId(String groupId); 
}
