package com.academicdashboard.backend.checklist;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChecklistRepository extends MongoRepository<Checklist, ObjectId> {

    Optional<Checklist> findChecklistByListId(String listId);

    @DeleteQuery
    void deleteChecklistByListId(String listId);
}
