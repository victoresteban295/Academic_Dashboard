package com.academicdashboard.backend.profile;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfessorRepository extends MongoRepository<Professor, ObjectId> {
    Optional<Professor> findByUsername(String username);
}
