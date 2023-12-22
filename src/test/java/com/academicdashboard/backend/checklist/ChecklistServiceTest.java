package com.academicdashboard.backend.checklist;

import java.util.ArrayList;
import java.util.List;

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
                checklistRepository);
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
        //When
        checklistService.createChecklist("testuser", "Checklist Title F", "listIdF");

        //Then
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getChecklists()
                .get(2)
                .getListId())
            .isEqualTo("listIdF");
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getChecklists()
                .get(2)
                .getTitle())
            .isEqualTo("Checklist Title F");
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdF")
                .get()
                .getListId())
            .isEqualTo("listIdF");
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdF")
                .get()
                .getTitle())
            .isEqualTo("Checklist Title F");
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdF")
                .get()
                .getGroupId())
            .isEqualTo("");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Creating New Checklist Under Non-Existent User")
    @WithMockUser(username = "testuser")
    public void throwExceptionCreatingNewChecklistNonExistentUser() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.createChecklist("XXXXX", "listIdF", "Checklist Title F");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("User Not Found");
    } 

    @Test
    @DisplayName("Should Throw an ApiRequestException When Creating New Checklist Under User Who Exceeded Checklists Limit")
    @WithMockUser(username = "testuser01")
    public void throwExceptionCreatingNewChecklistUnderUserWhoseChecklistsLimitExceeded() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.createChecklist("testuser01", "Checklist Title F", "listIdF");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("User's Checklists Limit Exceeded: 20");
    } 

    @Test
    @DisplayName("Should Throw an ApiRequestException When Creating New Checklist Whose Title Exceeds Character Limit")
    @WithMockUser(username = "testuser")
    public void throwExceptionCreatingNewChecklistWhoseTitleExceedsCharacterLimit() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.createChecklist("testuser", "012345678901234567890123456789012345678901234567890000000", "listIdF");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Checklist's Title Cannot Exceed 50 Characters");
    } 

    @Test
    @DisplayName("Should Throw an ApiRequestException When Creating New Checklist Whose Title Is Empty")
    @WithMockUser(username = "testuser")
    public void throwExceptionCreatingNewChecklistWhoseTitleIsEmpty() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.createChecklist("testuser", "      ", "listIdF");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Empty Checklist's Title");
    } 

    @Test
    @DisplayName("Should Create a New Checklist Under User")
    @WithMockUser(username = "testuser")
    public void shouldReorderUserChecklists() {
        //Reorder User's Checklists
        List<Checklist> checklists = userRepository
            .findUserByUsername("testuser")
            .get()
            .getChecklists();
        List<Checklist> reorderChecklist = new ArrayList<>();
        reorderChecklist.add(checklists.get(1));
        reorderChecklist.add(checklists.get(0));

        //When
        checklistService.reorderChecklist("testuser", reorderChecklist);

        //Then
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getChecklists()
                .size())
            .isEqualTo(2);
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getChecklists()
                .get(0)
                .getListId())
            .isEqualTo("listIdD1");
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getChecklists()
                .get(0)
                .getTitle())
            .isEqualTo("Checklist Title D1");
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getChecklists()
                .get(0)
                .getGroupId())
            .isEqualTo("");
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getChecklists()
                .get(1)
                .getListId())
            .isEqualTo("listIdC1");
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getChecklists()
                .get(1)
                .getTitle())
            .isEqualTo("Checklist Title C1");
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getChecklists()
                .get(1)
                .getGroupId())
            .isEqualTo("");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Reordering Checklists Under Non-Existent User")
    @WithMockUser(username = "testuser")
    public void throwExceptionReorderingChecklistsUnderNonexistentUser() {
        //Reorder User's Checklists
        List<Checklist> checklists = userRepository
            .findUserByUsername("testuser")
            .get()
            .getChecklists();
        List<Checklist> reorderChecklist = new ArrayList<>();
        reorderChecklist.add(checklists.get(1));
        reorderChecklist.add(checklists.get(0));

        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.reorderChecklist("XXXXXX", reorderChecklist);
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("User Not Found");
    } 

    @Test
    @DisplayName("Should Throw an ApiRequestException When Reordering a Non-Existent Checklists Under User")
    @WithMockUser(username = "testuser")
    public void throwExceptionReorderingNonexistentChecklistsUnderUser() {
        //Reorder User's Checklists
        List<Checklist> checklists = userRepository
            .findUserByUsername("testuser")
            .get()
            .getChecklists();
        //Non-Existent Checklist
        Checklist nonexistent = Checklist.builder()
            .listId("listIdXX")
            .title("Checklist Title XX")
            .groupId("")
            .checkpoints(new ArrayList<>())
            .completedPoints(new ArrayList<>())
            .build();
        List<Checklist> reorderChecklist = new ArrayList<>();
        reorderChecklist.add(nonexistent);
        reorderChecklist.add(checklists.get(1));
        reorderChecklist.add(checklists.get(0));

        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.reorderChecklist("testuser", reorderChecklist);
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Checklist Not Found");
    } 

    @Test
    @DisplayName("Should Modify an Existing Checklist")
    @WithMockUser(username = "testuser")
    public void modifyExistingChecklistTitle() {
        //When
        checklistService.modifyTitle("testuser", "listIdA1", "New Checklist Title A1"); //Grouped List
        checklistService.modifyTitle("testuser", "listIdC1", "New Checklist Title C1"); //Non-Grouped List

        //Then
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .size())
            .isEqualTo(2);
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .get(0)
                .getListId())
            .isEqualTo("listIdA1");
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .get(0)
                .getTitle())
            .isEqualTo("New Checklist Title A1");
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .get(0)
                .getGroupId())
            .isEqualTo("groupIdA");
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getChecklists()
                .size())
            .isEqualTo(2);
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getChecklists()
                .get(0)
                .getListId())
            .isEqualTo("listIdC1");
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getChecklists()
                .get(0)
                .getTitle())
            .isEqualTo("New Checklist Title C1");
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getChecklists()
                .get(0)
                .getGroupId())
            .isEqualTo("");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Modifying Checklist's Title Under Non-Existent User")
    @WithMockUser(username = "testuser")
    public void throwExceptionModifyingChecklistTitleUnderNonexistentUser() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.modifyTitle("XXXXXX", "listIdC1", "New Checklist Title C1");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("User Not Found");
    } 

    @Test
    @DisplayName("Should Throw an ApiRequestException When Modifying Non-Existent Checklist's Title")
    @WithMockUser(username = "testuser")
    public void throwExceptionModifyingNonexistentChecklistTitle() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.modifyTitle("testuser", "listIdXX", "New Checklist Title C1");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Checklist Not Found");
    } 

    @Test
    @DisplayName("Should Throw an ApiRequestException When New Checklist Title Exceeds Character Limit")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenTitleExceedsLimit() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.modifyTitle("testuser", "listIdC1", "0123456789012345678901234567890123456789012345678900000000");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Checklist's Title Cannot Exceed 50 Characters");
    } 

    @Test
    @DisplayName("Should Throw an ApiRequestException When New Checklist Title Is Empty")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenTitleIsEmpty() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.modifyTitle("testuser", "listIdC1", "     ");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Empty Checklist's Title");
    } 

    @Test
    @DisplayName("Should Modify an Existing Checklist")
    @WithMockUser(username = "testuser")
    public void modifyChecklistCheckpoints() {
        ///Checkpoints
        Checkpoint subpointB21A = Checkpoint.builder()
            .index("0")
            .content("ContentB21A")
            .subpoints(new ArrayList<>())
            .completedSubpoints(new ArrayList<>())
            .build();
        Checkpoint subpointB21B = Checkpoint.builder()
            .index("1")
            .content("ContentB21B")
            .subpoints(new ArrayList<>())
            .completedSubpoints(new ArrayList<>())
            .build();
        List<Checkpoint> B21Subpoints = new ArrayList<>();
        B21Subpoints.add(subpointB21A);
        B21Subpoints.add(subpointB21B);
        Checkpoint pointB21 = Checkpoint.builder()
            .index("0")
            .content("ContentB21")
            .subpoints(B21Subpoints)
            .completedSubpoints(new ArrayList<>())
            .build();
        Checkpoint pointB22 = Checkpoint.builder()
            .index("1")
            .content("ContentB22")
            .subpoints(new ArrayList<>())
            .completedSubpoints(new ArrayList<>())
            .build();
        List<Checkpoint> B2Checkpoints = new ArrayList<>();
        B2Checkpoints.add(pointB21);
        B2Checkpoints.add(pointB22);

        //Completed Checkpoints
        Checkpoint pointB23 = Checkpoint.builder()
            .index("0")
            .content("ContentB23")
            .subpoints(new ArrayList<>())
            .completedSubpoints(new ArrayList<>())
            .build();
        Checkpoint pointB24 = Checkpoint.builder()
            .index("1")
            .content("ContentB24")
            .subpoints(new ArrayList<>())
            .completedSubpoints(new ArrayList<>())
            .build();
        List<Checkpoint> B2CompletedPoints = new ArrayList<>();
        B2CompletedPoints.add(pointB23);
        B2CompletedPoints.add(pointB24);

        //When
        checklistService.modifyCheckpoints("testuser", "listIdB2", B2Checkpoints, B2CompletedPoints);

        //Then
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdB2")
                .get()
                .getCheckpoints()
                .size())
            .isEqualTo(2);
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdB2")
                .get()
                .getCheckpoints()
                .get(0)
                .getContent())
            .isEqualTo("ContentB21");
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdB2")
                .get()
                .getCheckpoints()
                .get(0)
                .getSubpoints()
                .size())
            .isEqualTo(2);
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdB2")
                .get()
                .getCheckpoints()
                .get(0)
                .getSubpoints()
                .get(0)
                .getContent())
            .isEqualTo("ContentB21A");
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdB2")
                .get()
                .getCheckpoints()
                .get(0)
                .getSubpoints()
                .get(1)
                .getContent())
            .isEqualTo("ContentB21B");
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdB2")
                .get()
                .getCheckpoints()
                .get(1)
                .getContent())
            .isEqualTo("ContentB22");
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdB2")
                .get()
                .getCompletedPoints()
                .size())
            .isEqualTo(2);
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdB2")
                .get()
                .getCompletedPoints()
                .get(0)
                .getContent())
            .isEqualTo("ContentB23");
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdB2")
                .get()
                .getCompletedPoints()
                .get(1)
                .getContent())
            .isEqualTo("ContentB24");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Modifying Checklist's Checkpoints Under a Non-Existent User")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenModifyingChecklistCheckpointsUnderNonexistentUser() {
        List<Checkpoint> B2Checkpoints = new ArrayList<>();
        List<Checkpoint> B2CompletedPoints = new ArrayList<>();

        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.modifyCheckpoints("XXXXXXXX", "listIdB2", B2Checkpoints, B2CompletedPoints);
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("User Not Found");
    } 

    @Test
    @DisplayName("Should Throw an ApiRequestException When Modifying Non-Existent Checklist's Checkpoints")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenModifyingNonexistentChecklistCheckpoints() {
        List<Checkpoint> B2Checkpoints = new ArrayList<>();
        List<Checkpoint> B2CompletedPoints = new ArrayList<>();

        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.modifyCheckpoints("testuser", "listIdXX", B2Checkpoints, B2CompletedPoints);
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Checklist Not Found");
    } 

    @Test
    @DisplayName("Should Throw an ApiRequestException When Modifying Checklist's Checkpoints Where Checkpoints Exceeded Limit")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenModifyingChecklistWithExceededLimitOfCheckpoints() {
        List<Checkpoint> B2Checkpoints = new ArrayList<>();
        for(int i = 1; i <= 26; i++) {
            String index = Integer.toString(i-1);
            String content = "Content" + Integer.toString(i);

            Checkpoint subpoint = Checkpoint.builder()
                .index(index)
                .content(content)
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();
            B2Checkpoints.add(subpoint);
        }
        List<Checkpoint> B2CompletedPoints = new ArrayList<>();

        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.modifyCheckpoints("testuser", "listIdB2", B2Checkpoints, B2CompletedPoints);
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Checkpoints Limit Exceeded: 25");
    } 

    @Test
    @DisplayName("Should Throw an ApiRequestException When Modifying Checklist's Checkpoints Where CompletedPoints Exceeded Limit")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenModifyingChecklistWithExceededLimitOfCompletedPoints() {
        List<Checkpoint> B2CompletedPoints = new ArrayList<>();
        for(int i = 1; i <= 26; i++) {
            String index = Integer.toString(i-1);
            String content = "Content" + Integer.toString(i);

            Checkpoint subpoint = Checkpoint.builder()
                .index(index)
                .content(content)
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();
            B2CompletedPoints.add(subpoint);
        }
        List<Checkpoint> B2Checkpoints = new ArrayList<>();

        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.modifyCheckpoints("testuser", "listIdB2", B2Checkpoints, B2CompletedPoints);
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Completed Checkpoints Limit Exceeded: 25");
    } 

    @Test
    @DisplayName("Should Delete Checklist with its Checkpoints")
    @WithMockUser(username = "testuser")
    public void shouldDeleteChecklistWithCheckpoints() {
        //When
        checklistService.deleteChecklist("testuser", "listIdB1"); //Grouped List
        checklistService.deleteChecklist("testuser", "listIdD1"); //Non-Grouped List

        //Then
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getChecklists()
                .size())
            .isEqualTo(1);
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdB")
                .get()
                .getChecklists()
                .size())
            .isEqualTo(1);
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdB1")
                .isPresent())
            .isFalse();
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdD1")
                .isPresent())
            .isFalse();
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Deleting Checklist Under Non-Existent Username")
    @WithMockUser(username = "testuser")
    public void throwExceptionDeletingChecklistNonExistentUser() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.deleteChecklist("XXXXX", "listIdB1");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("User Not Found");
    } 

    @Test
    @DisplayName("Should Throw an ApiRequestException When Modifying Non-existent Checklist")
    @WithMockUser(username = "testuser")
    public void throwExceptionDeletingNonexistentChecklist() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.deleteChecklist("testuser", "listIdXX");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Checklist Not Found");
    }
}
