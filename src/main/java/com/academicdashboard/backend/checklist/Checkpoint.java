package com.academicdashboard.backend.checklist;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "checkpoint")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Checkpoint {
    
    @Id
    private ObjectId id;
    
    String pointId;
    String content;

    @JsonProperty(value = "isComplete")
    boolean isComplete;

    @JsonProperty(value = "isSubpoint")
    boolean isSubpoint;

    @DocumentReference
    List<Checkpoint> subCheckpoints;
}
