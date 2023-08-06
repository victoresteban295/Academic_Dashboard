package com.academicdashboard.backend.token;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends MongoRepository<Token, ObjectId> {

    Optional<Token> findByToken(String token);
}
