package com.academicdashboard.backend.checklist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefList {
    String title; //Checklist's Title
    String listId; //Checklist's listId
}
