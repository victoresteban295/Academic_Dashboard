package com.academicdashboard.backend.institution;

import org.springframework.stereotype.Service;

import com.academicdashboard.backend.exception.ApiRequestException;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

import java.util.ArrayList;
import java.util.Random;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository deptRepository;
    private final MongoTemplate mongoTemplate;

    //Create New Public Id (JNanoId)
    private static String publicId(int size) {
        Random random = new Random();
        char[] alphabet = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','p','q','r','s','t','u','v','w','x','y','z','1','2','3','5','6','7','8','9'};
        return NanoIdUtils.randomNanoId(random, alphabet, size); //Create New Public Id
    }

    /*********** QUERY DEFINITION METHOD ***********/
    private static Query query(String field, String equalsValue) {
        return new Query().addCriteria(Criteria.where(field).is(equalsValue));
    } 


    private static Update pushUpdate(String field, Department dept) {
        return new Update().push(field).value(dept);
    }

    /*********** OPTION DEFINITION METHOD ***********/
    private static FindAndModifyOptions options(boolean returnNew, boolean upsert) {
        return new FindAndModifyOptions().returnNew(returnNew).upsert(upsert);
    }


    /*********** CRUD METHODS ***********/

    //Create New Department | Returns Institution
    public Institution createDept(String schoolId, String deptName) {
        if(mongoTemplate.exists(query("schoolId", schoolId), Institution.class)) {
            Institution school = mongoTemplate.findOne(query("schoolId", schoolId), Institution.class);
            String schoolName = school.getSchoolName();
            String deptId = publicId(10);
            Department dept = deptRepository.insert(
                    Department.builder()
                    .deptId(deptId)
                    .dept(deptName)
                    .schoolName(schoolName)
                    .majors(new ArrayList<>())
                    .minors(new ArrayList<>())
                    .build());
            mongoTemplate.findAndModify(
                    query("schoolId", schoolId), 
                    new Update().push("deptNames").value(deptName), 
                    options(true, true), 
                    Institution.class);
            return mongoTemplate.findAndModify(
                    query("schoolId", schoolId), 
                    pushUpdate("departments", dept), 
                    options(true, true), 
                    Institution.class);

        } else {
            throw new ApiRequestException("Invalid School Identification Code");
        }
    }

    //Modify Existing Department | Returns Modified Department
    public Department modifyDepartment(String deptId, String newName) {
        Department dept = deptRepository
            .findDepartmentByDeptId(deptId)
            .orElseThrow(() -> new ApiRequestException("Invalid Department Identification Code"));
        dept.setDept(newName);
        return deptRepository.save(dept);
    }

    //Delete Existing Department | Void
    public void deleteDepartment(String deptId) {
        Department dept = deptRepository
            .findDepartmentByDeptId(deptId)
            .orElseThrow(() -> new ApiRequestException("Invalid Department Identification Code"));

        deptRepository.delete(dept);
    }

    //Add New Major to Department and Institution | Returns Modified Department
    public Department createMajor(String schoolId, String deptId, String major) {
        if(mongoTemplate.exists(query("schoolId", schoolId), Institution.class)) {
            if(mongoTemplate.exists(query("deptId", deptId), Department.class)) {
                mongoTemplate.findAndModify(
                        query("schoolId", schoolId), 
                        new Update().push("majors").value(major), 
                        options(true, true), 
                        Institution.class);

                return mongoTemplate.findAndModify(
                        query("deptId", deptId), 
                        new Update().push("majors").value(major),
                        options(true, true), 
                        Department.class);
            } else {
                throw new ApiRequestException("Invalid Department Identification Code");
            }

        } else {
            throw new ApiRequestException("Invalid School Identification Code");
        }
    }

    //Delete Existing Major from Department and Insititution | Return Modified Department
    public Department deleteMajor(String schoolId, String deptId, String major) {
        if(mongoTemplate.exists(query("schoolId", schoolId), Institution.class)) {
            if(mongoTemplate.exists(query("deptId", deptId), Department.class)) {
                mongoTemplate.findAndModify(
                        query("schoolId", schoolId), 
                        new Update().pull("majors", major), 
                        options(true, true), 
                        Institution.class);

                return mongoTemplate.findAndModify(
                        query("deptId", deptId), 
                        new Update().pull("majors", major),
                        options(true, true), 
                        Department.class);
            } else {
                throw new ApiRequestException("Invalid Department Identification Code");
            }

        } else {
            throw new ApiRequestException("Invalid School Identification Code");
        }
    }

    //Add New Minor to Department and Institution | Returns Modified Department
    public Department createMinor(String schoolId, String deptId, String minor) {
        if(mongoTemplate.exists(query("schoolId", schoolId), Institution.class)) {
            if(mongoTemplate.exists(query("deptId", deptId), Department.class)) {
                mongoTemplate.findAndModify(
                        query("schoolId", schoolId), 
                        new Update().push("minors").value(minor), 
                        options(true, true), 
                        Institution.class);

                return mongoTemplate.findAndModify(
                        query("deptId", deptId), 
                        new Update().push("minors").value(minor),
                        options(true, true), 
                        Department.class);
            } else {
                throw new ApiRequestException("Invalid Department Identification Code");
            }

        } else {
            throw new ApiRequestException("Invalid School Identification Code");
        }
    }

    //Delete Existing Major from Department and Insititution | Return Modified Department
    public Department deleteMinor(String schoolId, String deptId, String minor) {
        if(mongoTemplate.exists(query("schoolId", schoolId), Institution.class)) {
            if(mongoTemplate.exists(query("deptId", deptId), Department.class)) {
                mongoTemplate.findAndModify(
                        query("schoolId", schoolId), 
                        new Update().pull("minors", minor), 
                        options(true, true), 
                        Institution.class);

                return mongoTemplate.findAndModify(
                        query("deptId", deptId), 
                        new Update().pull("minors", minor),
                        options(true, true), 
                        Department.class);
            } else {
                throw new ApiRequestException("Invalid Department Identification Code");
            }

        } else {
            throw new ApiRequestException("Invalid School Identification Code");
        }
    }
}
