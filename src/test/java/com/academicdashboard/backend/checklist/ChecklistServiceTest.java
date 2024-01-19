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

import com.academicdashboard.backend.config.TestData;
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
                .getUsername())
            .isEqualTo("testuser");
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
    @DisplayName("Should Edit Checklist's Title")
    @WithMockUser(username = "testuser")
    public void modifyExistingChecklistTitle() {
        //When
        checklistService.editTitle("listIdA1", "New Checklist Title A1"); //Grouped List
        checklistService.editTitle("listIdC1", "New Checklist Title C1"); //Non-Grouped List

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
                .getUsername())
            .isEqualTo("testuser");
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
                .getUsername())
            .isEqualTo("testuser");
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
    @WithMockUser(username = "XXXXXXX")
    public void throwExceptionModifyingChecklistTitleUnderNonexistentUser() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.editTitle("listIdC1", "New Checklist Title C1");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("User Not Found");
    } 

    @Test
    @DisplayName("Should Throw an ApiRequestException When Modifying Non-Existent Checklist's Title")
    @WithMockUser(username = "testuser")
    public void throwExceptionModifyingNonexistentChecklistTitle() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.editTitle("listIdXX", "New Checklist Title C1");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Checklist Not Found");
    } 

    @Test
    @DisplayName("Should Throw an ApiRequestException When New Checklist Title Exceeds Character Limit")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenTitleExceedsLimit() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.editTitle("listIdC1", "0123456789012345678901234567890123456789012345678900000000");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Checklist's Title Cannot Exceed 50 Characters");
    } 

    @Test
    @DisplayName("Should Throw an ApiRequestException When New Checklist Title Is Empty")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenTitleIsEmpty() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.editTitle("listIdC1", "     ");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Empty Checklist's Title");
    } 

    @Test
    @DisplayName("Should Edit Checklist's Checkpoints")
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
        checklistService.editCheckpoints("listIdB2", B2Checkpoints, B2CompletedPoints);

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
    @WithMockUser(username = "XXXXXXXX")
    public void throwExceptionWhenModifyingChecklistCheckpointsUnderNonexistentUser() {
        List<Checkpoint> B2Checkpoints = new ArrayList<>();
        List<Checkpoint> B2CompletedPoints = new ArrayList<>();

        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.editCheckpoints("listIdB2", B2Checkpoints, B2CompletedPoints);
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
            checklistService.editCheckpoints("listIdXX", B2Checkpoints, B2CompletedPoints);
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
            checklistService.editCheckpoints("listIdB2", B2Checkpoints, B2CompletedPoints);
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
            checklistService.editCheckpoints("listIdB2", B2Checkpoints, B2CompletedPoints);
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Completed Checkpoints Limit Exceeded: 25");
    } 

    @Test
    @DisplayName("Should Move Checklist From One Grouplist to Another Grouplist")
    @WithMockUser(username = "testuser")
    public void shouldMoveChecklistFromGrouptoGroup() {
        //When
        checklistService.editGrouplist("listIdB2", "groupIdA");

        //Then
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdB")
                .get()
                .getChecklists()
                .size())
            .isEqualTo(1);
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .size())
            .isEqualTo(3);
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .get(2).getListId())
            .isEqualTo("listIdB2");
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .get(2).getTitle())
            .isEqualTo("Checklist Title B2");
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .get(2).getGroupId())
            .isEqualTo("groupIdA");
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdB2")
                .get()
                .getTitle())
            .isEqualTo("Checklist Title B2");
    }

    @Test
    @DisplayName("Should Move Non-Grouped Checklist to Grouplist")
    @WithMockUser(username = "testuser")
    public void shouldMoveNonGroupedChecklistToGroup() {
        //When
        checklistService.editGrouplist("listIdC1", "groupIdA");
        checklistService.editGrouplist("listIdD1", "groupIdB");

        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .size())
            .isEqualTo(3);
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdB")
                .get()
                .getChecklists()
                .size())
            .isEqualTo(3);
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getChecklists()
                .size())
            .isEqualTo(0);
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .get(2).getListId())
            .isEqualTo("listIdC1");
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .get(2).getTitle())
            .isEqualTo("Checklist Title C1");
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .get(2).getGroupId())
            .isEqualTo("groupIdA");
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .get(2).getCheckpoints().size())
            .isEqualTo(4);
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .get(2).getCompletedPoints().size())
            .isEqualTo(0);
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdB")
                .get()
                .getChecklists()
                .get(2).getListId())
            .isEqualTo("listIdD1");
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdB")
                .get()
                .getChecklists()
                .get(2).getTitle())
            .isEqualTo("Checklist Title D1");
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdB")
                .get()
                .getChecklists()
                .get(2).getGroupId())
            .isEqualTo("groupIdB");
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdB")
                .get()
                .getChecklists()
                .get(2).getCheckpoints().size())
            .isEqualTo(0);
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdB")
                .get()
                .getChecklists()
                .get(2).getCompletedPoints().size())
            .isEqualTo(2);
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdC1")
                .get()
                .getTitle())
            .isEqualTo("Checklist Title C1");
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdC1")
                .get()
                .getGroupId())
            .isEqualTo("groupIdA");
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdD1")
                .get()
                .getTitle())
            .isEqualTo("Checklist Title D1");
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdD1")
                .get()
                .getGroupId())
            .isEqualTo("groupIdB");
    }

    @Test
    @DisplayName("Should Remove Checklist From Grouplist")
    @WithMockUser(username = "testuser")
    public void shouldRemoveChecklistFromGroup() {
        //When
        checklistService.editGrouplist("listIdA1", "");

        //Then
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .size())
            .isEqualTo(1);
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get().getChecklists()
                .size())
            .isEqualTo(3);
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get().getChecklists()
                .get(2).getListId())
            .isEqualTo("listIdA1");
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get().getChecklists()
                .get(2).getTitle())
            .isEqualTo("Checklist Title A1");
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get().getChecklists()
                .get(2).getGroupId())
            .isEqualTo("");
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get().getChecklists()
                .get(2).getCheckpoints().size())
            .isEqualTo(2);
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get().getChecklists()
                .get(2).getCompletedPoints().size())
            .isEqualTo(2);
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdA1")
                .get().getTitle())
            .isEqualTo("Checklist Title A1");
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdA1")
                .get().getGroupId())
            .isEqualTo("");
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdA1")
                .get().getCheckpoints().size())
            .isEqualTo(2);
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdA1")
                .get().getCompletedPoints().size())
            .isEqualTo(2);
    }

    @Test
    @DisplayName("Should throw an ApiRequestException When Editing a Non-Existent Checklist's Grouplist")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenEditingNonexistentChecklistsGrouplist() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.editGrouplist("listIdXX", "groupIdB");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Checklist Not Found");
    }

    @Test
    @DisplayName("Should throw an ApiRequestException When Checklist is Already Grouped in Grouplist")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenChecklistAlreadyGroupedInGrouplist() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.editGrouplist("listIdB1", "groupIdB");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Checklist Already Grouped in GroupId Provided");
    }

    @Test
    @DisplayName("Should throw an ApiRequestException When Editing a Checklist's Grouplist With Non-Existent User")
    @WithMockUser(username = "XXXXXXX")
    public void throwExceptionWhenEditingChecklistsGrouplistWithNonexistentUser() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.editGrouplist("listIdB1", "groupIdA");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("User Not Found");
    }

    @Test
    @DisplayName("Should throw an ApiRequestException When Moving Grouped Checklist to Non-Existent Grouplist")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenMovingGroupedChecklistToNonexistentGrouplist() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.editGrouplist("listIdB1", "groupIdXX");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Grouplist Not Found");
    }

    @Test
    @DisplayName("Should throw an ApiRequestException When Moving Grouped Checklist to Grouplist that Reached Checklists Limit")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenMovingGroupedChecklistToGrouplistThatReachedLimit() {
        //Create Checklist to Fill Grouplist 
        for(int i = 3; i <= 20; i++) {
            String title = "Checklist Title A" + Integer.toString(i);
            String listId = "listIdA" + Integer.toString(i);

            checklistService.createChecklist("testuser", title, listId);
        }
        //Populate Grouplist's Checklist to Limit
        for(int i = 3; i <= 20; i++) {
            String listId = "listIdA" + Integer.toString(i);
            checklistService.editGrouplist(listId, "groupIdA");
        }

        //When-Then
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .size())
            .isEqualTo(20);
        Assertions.assertThatThrownBy(() -> {
            checklistService.editGrouplist("listIdB1", "groupIdA");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Grouplist's Checklists Limit Exceeded: 20");
    }

    @Test
    @DisplayName("Should throw an ApiRequestException When Moving Non-Grouped Checklist to Non-Existent Grouplist")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenMovingNongroupedChecklistToNonexistentGrouplist() {
        Assertions.assertThatThrownBy(() -> {
            checklistService.editGrouplist("listIdC1", "groupIdXX");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Grouplist Not Found");
    }

    @Test
    @DisplayName("Should throw an ApiRequestException When Moving Non-Grouped Checklist to Grouplist that Reached Checklists Limit")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenMovingNongroupedChecklistToGrouplistThatReachedLimit() {
        //Create Checklist to Fill Grouplist 
        for(int i = 3; i <= 20; i++) {
            String title = "Checklist Title A" + Integer.toString(i);
            String listId = "listIdA" + Integer.toString(i);

            checklistService.createChecklist("testuser", title, listId);
        }
        //Populate Grouplist's Checklist to Limit
        for(int i = 3; i <= 20; i++) {
            String listId = "listIdA" + Integer.toString(i);
            checklistService.editGrouplist(listId, "groupIdA");
        }

        //When-Then
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .size())
            .isEqualTo(20);
        Assertions.assertThatThrownBy(() -> {
            checklistService.editGrouplist("listIdC1", "groupIdA");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Grouplist's Checklists Limit Exceeded: 20");
    }

    @Test
    @DisplayName("Should throw an ApiRequestException When Removing Checklist's From Grouplist to a User That Reached Checklists Limit")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenRemovingChecklistFromGroupToUserThatReachedLimit() {
        //Create Checklist to Fill Grouplist 
        for(int i = 1; i <= 18; i++) {
            String title = "Checklist Title A" + Integer.toString(i);
            String listId = "listId" + Integer.toString(i);

            checklistService.createChecklist("testuser", title, listId);
        }

        //When-Then
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get().getChecklists()
                .size())
            .isEqualTo(20);
        Assertions.assertThatThrownBy(() -> {
            checklistService.editGrouplist("listIdB1", "");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("User's Checklists Limit Exceeded: 20");
    }

    @Test
    @DisplayName("Should Delete Checklist with its Checkpoints")
    @WithMockUser(username = "testuser")
    public void shouldDeleteChecklistWithCheckpoints() {
        //When
        checklistService.deleteChecklist("listIdB1"); //Grouped List
        checklistService.deleteChecklist("listIdD1"); //Non-Grouped List

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
    @WithMockUser(username = "XXXXXXX")
    public void throwExceptionDeletingChecklistNonExistentUser() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.deleteChecklist("listIdB1");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("User Not Found");
    } 

    @Test
    @DisplayName("Should Throw an ApiRequestException When Modifying Non-existent Checklist")
    @WithMockUser(username = "testuser")
    public void throwExceptionDeletingNonexistentChecklist() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checklistService.deleteChecklist("listIdXX");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Checklist Not Found");
    }
}
