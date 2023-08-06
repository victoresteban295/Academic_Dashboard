package com.academicdashboard.backend.checklist;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.academicdashboard.backend.user.UserRepository;

@Testcontainers 
@DataMongoTest
public class ChecklistRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GrouplistRepository grouplistRepository;
    @Autowired
    private ChecklistRepository checklistRepository;
    @Autowired
    private CheckpointRepository checkpointRepository;

    private TestData testData;

    @BeforeEach
    public void setUp() {
        this.testData = new TestData(
                userRepository, 
                grouplistRepository, 
                checklistRepository, 
                checkpointRepository);
        testData.populateDatabase();
    }

    @AfterEach
    public void cleanup() {
        testData.cleanupDatabase();
    }

    @Test
    @DisplayName("Should Find Checklist Using ListId")
    public void canFindChecklistByListId() {
        //When
        Checklist returnedValue01 = checklistRepository.findChecklistByListId("listIdA1").get();
        Checklist returnedValue02 = checklistRepository.findChecklistByListId("listIdA2").get();
        Checklist returnedValue03 = checklistRepository.findChecklistByListId("listIdB1").get();
        Checklist returnedValue04 = checklistRepository.findChecklistByListId("listIdB2").get();
        Checklist returnedValue05 = checklistRepository.findChecklistByListId("listIdC1").get();
        Checklist returnedValue06 = checklistRepository.findChecklistByListId("listIdD").get();

        //Then
        Assertions.assertThat(returnedValue01.getTitle()).isEqualTo("Checklist TitleA1");
        Assertions.assertThat(returnedValue02.getTitle()).isEqualTo("Checklist TitleA2");
        Assertions.assertThat(returnedValue03.getTitle()).isEqualTo("Checklist TitleB1");
        Assertions.assertThat(returnedValue04.getTitle()).isEqualTo("Checklist TitleB2");
        Assertions.assertThat(returnedValue05.getTitle()).isEqualTo("Checklist TitleC1");
        Assertions.assertThat(returnedValue06.getTitle()).isEqualTo("Checklist TitleD");
    }
}
