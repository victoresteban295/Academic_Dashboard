package com.academicdashboard.backend.profile;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Document(collection = "student")
@Data
@SuperBuilder //Experimental
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Student extends Profile {

    //Student Specific Information
    private String gradeLvl;
    private String major;
    private String minor;
    private String concentration;
}
