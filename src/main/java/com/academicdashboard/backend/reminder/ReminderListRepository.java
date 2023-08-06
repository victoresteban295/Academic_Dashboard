package com.academicdashboard.backend.reminder;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReminderListRepository extends MongoRepository<ReminderList, ObjectId> {

}
