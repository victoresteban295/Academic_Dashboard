package com.academicdashboard.backend.institution;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerificationResponse {

    private String schoolName;
    private List<String> depts;
    private List<String> majors;
    private List<String> minors;
    
}
