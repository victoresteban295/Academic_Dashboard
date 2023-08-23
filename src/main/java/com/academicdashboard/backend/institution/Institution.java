package com.academicdashboard.backend.institution;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.academicdashboard.backend.profile.Professor;
import com.academicdashboard.backend.profile.Student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "institution")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Institution {

    @Id
    private ObjectId id;
    private String schoolId;
    private String schoolName;
    
    private String profIdCode;
    private String studIdCode;

    private List<String> deptNames;
    private List<String> majors;
    private List<String> minors;

    @DocumentReference
    private List<Department> departments;

    @DocumentReference
    private List<Professor> professors;

    @DocumentReference
    private List<Student> students;

}
