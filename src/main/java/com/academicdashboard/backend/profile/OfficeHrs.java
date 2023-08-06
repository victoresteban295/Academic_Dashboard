package com.academicdashboard.backend.profile;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfficeHrs {

    private String building;
    private String room;
    private String startTime;
    private String endTime;
    private List<String> days;
}
