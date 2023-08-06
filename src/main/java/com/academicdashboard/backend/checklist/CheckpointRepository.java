package com.academicdashboard.backend.checklist;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckpointRepository extends MongoRepository<Checkpoint, ObjectId> {
    Optional<Checkpoint> findCheckpointByPointId(String pointId);
}
