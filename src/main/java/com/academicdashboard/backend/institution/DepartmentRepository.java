package com.academicdashboard.backend.institution;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends MongoRepository<Department, ObjectId> {
    Optional<Department> findDepartmentByDeptId(String deptId);
    Optional<Department> findDepartmentBySchoolName(String schoolName);
}
