package com.academicdashboard.backend.token;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "token")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {

    @Id
    private ObjectId id; //MongoDB ObjectId

    @Indexed(unique = true)
    public String token;

    private TokenType tokenType; //ACCESS || REFRESH
    private String username;
    private boolean revoked; 
    private boolean expired;

}
