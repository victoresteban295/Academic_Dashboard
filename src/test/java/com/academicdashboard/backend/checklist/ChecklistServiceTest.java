package com.academicdashboard.backend.checklist;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.academicdashboard.backend.exception.ApiRequestException;
import com.academicdashboard.backend.user.UserRepository;

@Testcontainers
@DataMongoTest
public class ChecklistServiceTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GrouplistRepository grouplistRepository;
    @Autowired
    private ChecklistRepository checklistRepository;
    @Autowired
    private CheckpointRepository checkpointRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    private TestData testData;
    private ChecklistService checklistService;

    @BeforeEach
    public void setUp() {
        this.checklistService = new ChecklistService(
                checklistRepository, 
                mongoTemplate);
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
    @DisplayName("Should Create a New Checklist Under User")
    @WithMockUser(username = "testuser")
    public void shouldCreateNewChecklist() {
        Checklist expectedValue = checklistService
            .createChecklist("testuser", "listTitle");

        String expectedValueId = expectedValue.getListId();


        //Then
        /* Assert that Newly Created Checklist Has Been Added As A Checklist Document*/
        Assertions.assertThat(checklistRepository
                .findChecklistByListId(expectedValueId)
                .get())
            .isEqualTo(expectedValue);

        /* Assert that Newly Created Checklist Has Been Added to User's Checklist Reference*/
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getChecklists()
                .contains(expectedValue))
            .isEqualTo(true);
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Creating New Checklist Under Unauthorized Username")
    @WithMockUser(username = "testuser")
    public void throwExceptionCreatingNewChecklistNonExistentUser() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.createChecklist("XXXXX", "newTitle");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Provided Wrong Username");
    } 

    @Test
    @DisplayName("Should Modify an Existing Checklist")
    @WithMockUser(username = "testuser")
    public void modifyExistingChecklistTitle() {
        Checklist expectedValue01 = checklistService.modifyChecklist("testuser", "listIdA1", "newTitle01");
        Checklist expectedValue02 = checklistService.modifyChecklist("testuser", "listIdA2", "newTitle02");
        Checklist expectedValue03 = checklistService.modifyChecklist("testuser", "listIdB1", "newTitle03");
        Checklist expectedValue04 = checklistService.modifyChecklist("testuser", "listIdB2", "newTitle04");
        Checklist expectedValue05 = checklistService.modifyChecklist("testuser", "listIdC1", "newTitle05");
        Checklist expectedValue06 = checklistService.modifyChecklist("testuser", "listIdD", "newTitle06");

        //Then
        Assertions.assertThat(checklistRepository.findChecklistByListId("listIdA1").get()).isEqualTo(expectedValue01);
        Assertions.assertThat(checklistRepository.findChecklistByListId("listIdA2").get()).isEqualTo(expectedValue02);
        Assertions.assertThat(checklistRepository.findChecklistByListId("listIdB1").get()).isEqualTo(expectedValue03);
        Assertions.assertThat(checklistRepository.findChecklistByListId("listIdB2").get()).isEqualTo(expectedValue04);
        Assertions.assertThat(checklistRepository.findChecklistByListId("listIdC1").get()).isEqualTo(expectedValue05);
        Assertions.assertThat(checklistRepository.findChecklistByListId("listIdD").get()).isEqualTo(expectedValue06);
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Modifying New Checklist Under Unauthorized Username")
    @WithMockUser(username = "testuser")
    public void throwExceptionModifyingChecklistNonExistentUser() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.modifyChecklist("XXXXX", "listIdD", "newTitle06");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Provided Wrong Username");
    } 

    @Test
    @DisplayName("Should Throw an ApiRequestException When Modifying Non-existent Checklist")
    @WithMockUser(username = "testuser")
    public void throwExceptionModifyingNonexistentChecklist() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.modifyChecklist("testuser", "XXXXX", "newTitle");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Checklist You Wanted to Modify Doesn't Exist");
    } 

    @Test
    @DisplayName("Should Delete Checklist with its Checkpoints")
    @WithMockUser(username = "testuser")
    public void shouldDeleteChecklistWithCheckpoints() {
        //When
        checklistService.deleteChecklist("testuser", "listIdA1");
        checklistService.deleteChecklist("testuser", "listIdA2");
        checklistService.deleteChecklist("testuser", "listIdB1");
        checklistService.deleteChecklist("testuser", "listIdB2");
        checklistService.deleteChecklist("testuser", "listIdC1");
        checklistService.deleteChecklist("testuser", "listIdD");

        //Then
        Assertions.assertThat(checklistRepository.findAll().isEmpty()).isTrue();
        Assertions.assertThat(checkpointRepository.findAll().isEmpty()).isTrue();
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getChecklists()
                .isEmpty())
            .isTrue();
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .isEmpty())
            .isTrue();
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdB")
                .get()
                .getChecklists()
                .isEmpty())
            .isTrue();
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Deleting Checklist Under Unauthorized Username")
    @WithMockUser(username = "testuser")
    public void throwExceptionDeletingChecklistNonExistentUser() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.deleteChecklist("XXXXX", "listIdD");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Provided Wrong Username");
    } 

    @Test
    @DisplayName("Should Throw an ApiRequestException When Modifying Non-existent Checklist")
    @WithMockUser(username = "testuser")
    public void throwExceptionDeletingNonexistentChecklist() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.deleteChecklist("testuser", "XXXXX");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Checklist You Wanted to Delete Doesn't Exist");
    }
}
