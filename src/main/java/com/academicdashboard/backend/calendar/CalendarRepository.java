package com.academicdashboard.backend.calendar;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarRepository extends MongoRepository<Calendar, ObjectId> {

}
