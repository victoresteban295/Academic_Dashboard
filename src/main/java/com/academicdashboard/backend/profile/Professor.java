package com.academicdashboard.backend.profile;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Document(collection = "professor")
@Data
@SuperBuilder //Experimental
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Professor extends Profile {

    //Professor Specific Information
    private String department;
    private String academicRole; //View Google Doc for all options
    private int apptYear; //Appointed Year
    private String officeBuilding;
    private String officeRoom;
    private String researchArea;
    private List<OfficeHrs> officeHrs;
}
