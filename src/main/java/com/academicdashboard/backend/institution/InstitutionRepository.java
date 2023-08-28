package com.academicdashboard.backend.institution;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitutionRepository extends MongoRepository<Institution, ObjectId> {
    Optional<Institution> findInstitutionBySchoolId(String schoolId);
    Optional<Institution> findInstitutionByProfIdCode(String profIdCode);
    Optional<Institution> findInstitutionByStudIdCode(String studIdCode);
}
