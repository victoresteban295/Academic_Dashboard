package com.academicdashboard.backend.checklist;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.academicdashboard.backend.config.TestData;
import com.academicdashboard.backend.user.UserRepository;

@Testcontainers 
@DataMongoTest
public class GrouplistRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GrouplistRepository grouplistRepository;
    @Autowired
    private ChecklistRepository checklistRepository;

    private TestData testData;

    @BeforeEach
    public void setUp() {
        this.testData = new TestData(
                userRepository, 
                grouplistRepository, 
                checklistRepository);
        testData.populateDatabase();
    }

    @AfterEach
    public void cleanup() {
        testData.cleanupDatabase();
    }

    @Test
    @DisplayName("Should Find a Grouplist Document Using it's groupId")
    public void shouldFindGrouplistByGrouId() {
        //When
        Grouplist returnedValue01 = grouplistRepository.findGrouplistByGroupId("groupIdA").get();
        Grouplist returnedValue02 = grouplistRepository.findGrouplistByGroupId("groupIdB").get();

        //Then
        Assertions.assertThat(returnedValue01.getTitle()).isEqualTo("Grouplist Title A");
        Assertions.assertThat(returnedValue02.getTitle()).isEqualTo("Grouplist Title B");
    }
}
