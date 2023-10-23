package com.academicdashboard.backend.checklist;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Checkpoint {
    
    String content;
    List<Checkpoint> subpoints;

    @JsonProperty(value = "isComplete")
    boolean isComplete;

    @JsonProperty(value = "isSubpoint")
    boolean isSubpoint;

}
