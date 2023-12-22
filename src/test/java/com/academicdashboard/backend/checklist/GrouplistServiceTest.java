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
public class GrouplistServiceTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GrouplistRepository grouplistRepository;
    @Autowired
    private ChecklistRepository checklistRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    private TestData testData;
    private GrouplistService grouplistService;

    @BeforeEach
    public void setUp() {
        this.grouplistService = new GrouplistService(
                grouplistRepository, 
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
    @DisplayName("Should Create a New Grouplist Under User")
    @WithMockUser(username = "testuser")
    public void shouldCreateNewGrouplist() {
        //When
        Grouplist returnedValue = grouplistService.createGrouplist("testuser", "Group Title C", "groupIdC");

        //Then
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdC")
                .get())
            .isEqualTo(returnedValue);
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getGrouplists()
                .contains(returnedValue))
            .isTrue();
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getGrouplists()
                .size())
            .isEqualTo(3);
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Creating New Grouplist Under Non-existent User")
    @WithMockUser(username = "testuser")
    public void throwExceptionInsertingGrouplistInNonexistentUser() {
        Assertions.assertThatThrownBy(() -> {
            grouplistService.createGrouplist("XXXXX", "Grouplist Title C", "groupIdC");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("User Not Found");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Exceeding User's Grouplists Limit")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenReachingUsersGrouplistsLimit() {
        //Populate User's Grouplist To Limit 
        for(int i = 1; i <= 18; i++) {
            String title = "Group Title " + Integer.toString(i);
            String groupId = "groupId" + Integer.toString(i);
            grouplistService.createGrouplist("testuser", title, groupId);
        }
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getGrouplists()
                .size())
            .isEqualTo(20);
        Assertions.assertThatThrownBy(() -> {
            grouplistService.createGrouplist("testuser", "Group Title 21", "groupId21");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("User's Grouplists Limit Exceeded: 20");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Grouplist Title Exceeds 20 Characters")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenGrouplistTitleExceeds20Characters() {
        Assertions.assertThatThrownBy(() -> {
            grouplistService.createGrouplist("testuser", "012345678901234567890", "groupIdC");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Grouplist's Title Cannot Exceed 20 Characters");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Grouplist Title Is Empty")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenGrouplistTitleIsEmpty() {
        Assertions.assertThatThrownBy(() -> {
            grouplistService.createGrouplist("testuser", "   ", "groupIdC");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Empty Grouplist's Title");
    }

    @Test
    @DisplayName("Should Modify an Existing Grouplist's Title")
    @WithMockUser(username = "testuser")
    public void modifyExistingGrouplistTitle() {
        //When 
        Grouplist expectedValue01 = grouplistService.modifyTitle("testuser", "groupIdA", "New Group Title A");
        Grouplist expectedValue02 = grouplistService.modifyTitle("testuser", "groupIdB", "  New Group Title B      ");

        //Then
        Assertions.assertThat(grouplistRepository.findGrouplistByGroupId("groupIdA").get()).isEqualTo(expectedValue01);
        Assertions.assertThat(grouplistRepository.findGrouplistByGroupId("groupIdA").get().getTitle()).isEqualTo("New Group Title A");
        Assertions.assertThat(grouplistRepository.findGrouplistByGroupId("groupIdB").get()).isEqualTo(expectedValue02);
        Assertions.assertThat(grouplistRepository.findGrouplistByGroupId("groupIdB").get().getTitle()).isEqualTo("New Group Title B");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Modifying Grouplist's Title Under a Non-existent User")
    @WithMockUser(username = "testuser")
    public void throwExceptionModifyingGrouplistInNonexistentUser() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.modifyTitle("XXXXXXXXXX", "groupIdA", "New Group Title A");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("User Not Found");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When New Grouplist's Title Exceed 20 Characters")
    @WithMockUser(username = "testuser")
    public void throwExceptionModifyingGrouplistTitleWithExceededLimit() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.modifyTitle("testuser", "groupIdA", "012345678901234567890");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Grouplist's Title Cannot Exceed 20 Characters");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When New Grouplist's Title Is Empty")
    @WithMockUser(username = "testuser")
    public void throwExceptionModifyingGrouplistWithEmptyTitle() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.modifyTitle("testuser", "groupIdA", "    ");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Empty Grouplist's Title");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Modifying Non-Existent Grouplist's Title")
    @WithMockUser(username = "testuser")
    public void throwExceptionModifyingNonexistentGrouplistTitle() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.modifyTitle("testuser", "groupIdC", "New Group Title C");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Grouplist Not Found");
    }

    @Test
    @DisplayName("Should Reorder User's Grouplists")
    @WithMockUser(username = "testuser")
    public void shouldReorderUserGrouplists() {
        //Reorder Grouplist
        Grouplist groupA = grouplistRepository.findGrouplistByGroupId("groupIdA").get(); 
        Grouplist groupB = grouplistRepository.findGrouplistByGroupId("groupIdB").get(); 
        List<Grouplist> reorderGrouplist = new ArrayList<>();
        reorderGrouplist.add(groupB);
        reorderGrouplist.add(groupA);

        //When
        List<Grouplist> returnedValue = grouplistService.reorderGrouplists("testuser", reorderGrouplist);

        //Then
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getGrouplists()
                .get(0)
                .getGroupId())
            .isEqualTo("groupIdB");
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getGrouplists()
                .get(0)
                .getTitle())
            .isEqualTo("Grouplist Title B");
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getGrouplists()
                .get(1)
                .getGroupId())
            .isEqualTo("groupIdA");
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getGrouplists()
                .get(1)
                .getTitle())
            .isEqualTo("Grouplist Title A");
        Assertions.assertThat(returnedValue
                .get(0)
                .getGroupId())
            .isEqualTo("groupIdB");
        Assertions.assertThat(returnedValue
                .get(0)
                .getTitle())
            .isEqualTo("Grouplist Title B");
        Assertions.assertThat(returnedValue
                .get(1)
                .getGroupId())
            .isEqualTo("groupIdA");
        Assertions.assertThat(returnedValue
                .get(1)
                .getTitle())
            .isEqualTo("Grouplist Title A");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Reordering Non-Existent User's Grouplists")
    @WithMockUser(username = "testuser")
    public void throwExceptionReorderingNonexistentUserGrouplists() {
        //Reorder Grouplist
        Grouplist groupA = grouplistRepository.findGrouplistByGroupId("groupIdA").get(); 
        Grouplist groupB = grouplistRepository.findGrouplistByGroupId("groupIdB").get(); 
        List<Grouplist> reorderGrouplist = new ArrayList<>();
        reorderGrouplist.add(groupB);
        reorderGrouplist.add(groupA);

        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.reorderGrouplists("XXXXXXXXX", reorderGrouplist);
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("User Not Found");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Reordering User's Non-existent Grouplists")
    @WithMockUser(username = "testuser")
    public void throwExceptionReorderingUserNonexistentGrouplists() {
        //Reorder Grouplist
        Grouplist groupC = Grouplist.builder()
            .groupId("groupIdC") 
            .title("Grouplist Title C")
            .checklists(new ArrayList<>())
            .build();
        Grouplist groupD = Grouplist.builder()
            .groupId("groupIdD") 
            .title("Grouplist Title D")
            .checklists(new ArrayList<>())
            .build();
        List<Grouplist> reorderGrouplist = new ArrayList<>();
        reorderGrouplist.add(groupC);
        reorderGrouplist.add(groupD);

        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.reorderGrouplists("testuser", reorderGrouplist);
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Grouplist Not Found");
    }

    @Test
    @DisplayName("Should Reorder Grouplist's Checklists")
    @WithMockUser(username = "testuser")
    public void shouldReorderGrouplistChecklists() {
        //Reorder Checklists
        Grouplist grouplist = grouplistRepository.findGrouplistByGroupId("groupIdA").get();
        List<Checklist> checklists = grouplist.getChecklists();
        List<Checklist> reorderChecklists = new ArrayList<>();
        //Reverse Checklists
        for(int i=checklists.size()-1; i>=0; i--) {
            reorderChecklists.add(checklists.get(i)); 
        }
        grouplist.setChecklists(reorderChecklists);

        //When
        Grouplist returnedValue = grouplistService.reorderGroupChecklists("testuser", grouplist);

        //Then
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .get(0)
                .getListId())
            .isEqualTo("listIdA2");
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .get(0)
                .getTitle())
            .isEqualTo("Checklist Title A2");
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .get(1)
                .getListId())
            .isEqualTo("listIdA1");
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .get(1)
                .getTitle())
            .isEqualTo("Checklist Title A1");
        Assertions.assertThat(returnedValue
                .getChecklists()
                .get(0)
                .getListId())
            .isEqualTo("listIdA2");
        Assertions.assertThat(returnedValue
                .getChecklists()
                .get(0)
                .getTitle())
            .isEqualTo("Checklist Title A2");
        Assertions.assertThat(returnedValue
                .getChecklists()
                .get(1)
                .getTitle())
            .isEqualTo("Checklist Title A1");
        Assertions.assertThat(returnedValue
                .getChecklists()
                .get(1)
                .getListId())
            .isEqualTo("listIdA1");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Reorder Grouplist's Checklists In Non-Existent User")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenReorderGrouplistChecklistsInNonexistentUser() {
        //Reorder Checklists
        Grouplist grouplist = grouplistRepository.findGrouplistByGroupId("groupIdA").get();
        List<Checklist> checklists = grouplist.getChecklists();
        List<Checklist> reorderChecklists = new ArrayList<>();
        //Reverse Checklists
        for(int i=checklists.size()-1; i>=0; i--) {
            reorderChecklists.add(checklists.get(i)); 
        }
        grouplist.setChecklists(reorderChecklists);

        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.reorderGroupChecklists("XXXXX", grouplist);
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("User Not Found");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Reordering Non-Existent Grouplist's Checklists")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenReorderNonexistentGrouplistChecklists() {
        List<Checklist> checklists = grouplistRepository.findGrouplistByGroupId("groupIdA").get().getChecklists();
        Grouplist grouplist = Grouplist.builder()
            .groupId("groupIdC")
            .title("Group Title C")
            .checklists(checklists)
            .build();

        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.reorderGroupChecklists("testuser", grouplist);
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Grouplist Not Found");
    }

    @Test
    @DisplayName("Should Create a New Checklist Under an Existing Grouplist")
    @WithMockUser(username = "testuser")
    public void createNewChecklistUnderExistingGrouplist() {
        //When 
        grouplistService.createChecklist("testuser", "groupIdA", "listIdA3", "Checklist Title A3");
        grouplistService.createChecklist("testuser", "groupIdB", "listIdB3", "    Checklist Title B3   ");

        //Then
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdA3")
                .get()
                .getListId())
            .isEqualTo("listIdA3");
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdA3")
                .get()
                .getTitle())
            .isEqualTo("Checklist Title A3");
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdB3")
                .get()
                .getListId())
            .isEqualTo("listIdB3");
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdB3")
                .get()
                .getTitle())
            .isEqualTo("Checklist Title B3");
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
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists() 
                .get(2)
                .getListId())
            .isEqualTo("listIdA3");
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists() 
                .get(2)
                .getGroupId())
            .isEqualTo("groupIdA");
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists() 
                .get(2)
                .getTitle())
            .isEqualTo("Checklist Title A3");
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdB")
                .get()
                .getChecklists()
                .get(2)
                .getListId())
            .isEqualTo("listIdB3");
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdB")
                .get()
                .getChecklists()
                .get(2)
                .getGroupId())
            .isEqualTo("groupIdB");
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdB")
                .get()
                .getChecklists()
                .get(2)
                .getTitle())
            .isEqualTo("Checklist Title B3");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Creating a New Checklist Under a Grouplist Under a Non-Existent User")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenCreateingNewChecklistUnderGrouplistUnderNonExistentUser() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.createChecklist("XXXXXXXXXX", "groupIdA", "listIdA3", "Checklist Title A3");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("User Not Found");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Grouplist's Checklists Limit Exceeded")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenGrouplistChecklistsLimitExceeded() {
        //Populate User's Grouplist To Limit 
        for(int i = 1; i <= 18; i++) {
            String title = "Checklist Title A" + Integer.toString(i);
            String listId = "listId" + Integer.toString(i);
            grouplistService.createChecklist("testuser", "groupIdA", listId, title);
        }
        //When-Then
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .size())
            .isEqualTo(20);
        Assertions.assertThatThrownBy(() -> {
            grouplistService.createChecklist("testuser", "groupIdA", "listIdA21", "Checklist Title A21");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Grouplist's Checklists Limit Exceeded: 20");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Creating a New Checklist Whose Title Exceed Character Limit Under a Grouplist")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenNewChecklistTitleCharactersLimitExceeded() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.createChecklist("testuser", "groupIdA", "listIdA3", "012345678901234567890123456789012345678901234567890000");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Checklist's Title Cannot Exceed 50 Characters");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Creating a New Checklist Whose Title is Empty Under a Grouplist")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenNewChecklistWithEmptyTitle() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.createChecklist("testuser", "groupIdA", "listIdA3", "       ");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Empty Checklist's Title");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Creating a New Checklist Under a Non-Existent Grouplist")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenCreatingNewChecklistUnderNonExistentChecklist() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.createChecklist("testuser", "groupIdD", "listIdA3", "Checklist Title A3");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Grouplist Not Found");
    }

    @Test
    @DisplayName("Should Add Existing Non-Grouped Checklist Under an Existing Grouplist")
    @WithMockUser(username = "testuser")
    public void addNonGroupedChecklistToGrouplist() {
        //When 
        grouplistService.addChecklist("testuser", "listIdC1", "groupIdA");
        grouplistService.addChecklist("testuser", "listIdD1", "groupIdA");

        //Then
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
                .size())
            .isEqualTo(4);
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .get(2)
                .getListId())
            .isEqualTo("listIdC1");
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .get(2)
                .getTitle())
            .isEqualTo("Checklist Title C1");
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .get(2)
                .getGroupId())
            .isEqualTo("groupIdA");
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .get(3)
                .getListId())
            .isEqualTo("listIdD1");
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .get(3)
                .getTitle())
            .isEqualTo("Checklist Title D1");
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .get(3)
                .getGroupId())
            .isEqualTo("groupIdA");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Adding a Non-Grouped Checklist Under a Grouplist Under a Non-Existent User")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenAddingChecklistToGrouplistUnderNonExistentUser() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.addChecklist("XXXXXXXXX", "listIdC1", "groupIdA");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("User Not Found");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Adding a Non-Existent Checklist Under a Grouplist")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenAddingNonexistentChecklistToGrouplist() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.addChecklist("testuser", "listIdF1", "groupIdA");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Checklist Not Found");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Adding a Non-Grouped Checklist Under a Non-Existent Grouplist")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenAddingChecklistToNonexistentGrouplist() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.addChecklist("testuser", "listIdC1", "groupIdD");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Grouplist Not Found");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Adding a Non-Grouped Checklist Under a Grouplist Whose Checklists Limit is Exceeded")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenAddChecklistToAGrouplistWhoseChecklistsLimitExceeded() {
        //Populate Grouplist's Checklists To Limit
        for(int i = 1; i <= 18; i++) {
            String listId = "listId" + Integer.toString(i);
            String title = "Checklist Title " + Integer.toString(i);
            grouplistService.createChecklist("testuser", "groupIdA", title, listId);
        }

        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.addChecklist("testuser", "listIdC1", "groupIdA");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Grouplist's Checklist Limit Exceeded: 20");
    }

    @Test
    @DisplayName("Should Move Checklist From Grouplist to Another Grouplist")
    @WithMockUser(username = "testuser")
    public void addExistingChecklistUnderExistingGrouplist() {
        //When 
        grouplistService.moveChecklist("testuser", "listIdB1", "groupIdB", "groupIdA");
        grouplistService.moveChecklist("testuser", "listIdB2", "groupIdB", "groupIdA");

        //Then
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .size())
            .isEqualTo(4);
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdB")
                .get()
                .getChecklists()
                .isEmpty())
            .isTrue();
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .get(2)
                .getListId())
            .isEqualTo("listIdB1");
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .get(2)
                .getTitle())
            .isEqualTo("Checklist Title B1");
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .get(2)
                .getGroupId())
            .isEqualTo("groupIdA");
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .get(3)
                .getListId())
            .isEqualTo("listIdB2");
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .get(3)
                .getTitle())
            .isEqualTo("Checklist Title B2");
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .get(3)
                .getGroupId())
            .isEqualTo("groupIdA");
    }

    @Test
    @DisplayName("Should Throw ApiRequestException When Adding Checklist Under Grouplist In Non-Existent User")
    @WithMockUser(username = "testuser")
    public void shouldThrowExceptionWhenMovingChecklistToAnotherGrouplistInNonExistentUser() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.moveChecklist("XXXXXXX", "listIdB1", "groupIdB", "groupIdA");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("User Not Found");
    }

    @Test
    @DisplayName("Should Throw ApiRequestException When Moving Checklist To Another Non-Existent Grouplist")
    @WithMockUser(username = "testuser")
    public void shouldThrowExceptionWhenMovingChecklistToAnotherNonExistentGrouplist() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.moveChecklist("testuser", "listIdB1", "groupIdB", "groupIdD");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Grouplist Not Found");
    }

    @Test
    @DisplayName("Should Throw ApiRequestException When Moving A Non-Existent Checklist To Another Grouplist")
    @WithMockUser(username = "testuser")
    public void shouldThrowExceptionWhenMovingNonExistentChecklistToAnotherGrouplist() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.moveChecklist("testuser", "listIdF1", "groupIdB", "groupIdA");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Checklist Not Found");
    }

    @Test
    @DisplayName("Should Throw ApiRequestException When Moving A Non-Existent Checklist To Another Grouplist")
    @WithMockUser(username = "testuser")
    public void shouldThrowExceptionWhenMovingChecklistFromNonExistentGrouplistToAnotherGrouplist() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.moveChecklist("testuser", "listIdB1", "groupIdV", "groupIdA");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Grouplist Not Found");
    }

    @Test
    @DisplayName("Should Throw ApiRequestException When Moving A Checklist To Another Grouplist Who Exceeded Checklists Limit")
    @WithMockUser(username = "testuser")
    public void shouldThrowExceptionWhenMovingChecklistToAnotherGrouplistWhoseChecklistsLimitExceeded() {
        //Populate Grouplist's Checklists To Limit
        for(int i = 1; i <= 18; i++) {
            String listId = "listId" + Integer.toString(i);
            String title = "Checklist Title " + Integer.toString(i);
            grouplistService.createChecklist("testuser", "groupIdA", title, listId);
        }
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.moveChecklist("testuser", "listIdB1", "groupIdB", "groupIdA");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Grouplist's Checklist Limit Exceeded: 20");
    }

    @Test
    @DisplayName("Should Remove an Existing Checklist Under an Existing Grouplist")
    @WithMockUser(username = "testuser")
    public void removeExistingChecklistUnderExistingGrouplist() {
        //When 
        grouplistService.removeChecklist("testuser", "listIdA1", "groupIdA");
        grouplistService.removeChecklist("testuser", "listIdA2", "groupIdA");

        //Then
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdA1")
                .get()
                .getListId())
            .isEqualTo("listIdA1");
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdA1")
                .get()
                .getTitle())
            .isEqualTo("Checklist Title A1");
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdA2")
                .get()
                .getListId())
            .isEqualTo("listIdA2");
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdA2")
                .get()
                .getTitle())
            .isEqualTo("Checklist Title A2");
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getChecklists()
                .size())
            .isEqualTo(4);
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .isEmpty())
            .isTrue();
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getChecklists()
                .get(2)
                .getListId())
            .isEqualTo("listIdA1");
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getChecklists()
                .get(2)
                .getTitle())
            .isEqualTo("Checklist Title A1");
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getChecklists()
                .get(2)
                .getGroupId())
            .isEqualTo("");
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getChecklists()
                .get(3)
                .getListId())
            .isEqualTo("listIdA2");
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getChecklists()
                .get(3)
                .getTitle())
            .isEqualTo("Checklist Title A2");
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getChecklists()
                .get(3)
                .getGroupId())
            .isEqualTo("");
    }

    @Test
    @DisplayName("Should Throw ApiRequestException When Removing Checklist Under Grouplist Under Non-existent User")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenRemovingChecklistUnderGrouplistUnderNonexistentUser() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.removeChecklist("XXXXXXX", "listIdA1", "groupIdA");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("User Not Found");
    }

    @Test
    @DisplayName("Should Throw ApiRequestException When Removing a Non-Existent Checklist Under a Grouplist")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenRemovingNonExistentChecklistUnderGrouplist() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.removeChecklist("testuser", "listIdW1", "groupIdA");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Checklist Not Found");
    }

    @Test
    @DisplayName("Should Throw ApiRequestException When Removing a Checklist Under a Non-Existent Grouplist")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenRemovingChecklistUnderNonExistentGrouplist() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.removeChecklist("testuser", "listIdA1", "groupIdX");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Grouplist Not Found");
    }

    @Test
    @DisplayName("Should Throw ApiRequestException When Removing a Checklist Under a Grouplist Where User's Checklists Limit is Exceeded")
    @WithMockUser(username = "testuser01")
    public void throwExceptionWhenRemovingChecklistUnderGrouplistWhoseUserChecklistsLimitExceeded() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.removeChecklist("testuser01", "listIdA1", "groupIdA");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("User's Checklists Limit Exceeded: 20");
    }

    @Test
    @DisplayName("Should Delete Grouplist And Delete it's Checklists")
    @WithMockUser(username = "testuser")
    public void deleteGrouplistAndDeleteChecklists() {
        //When 
        grouplistService.deleteGrouplist("testuser", "groupIdA");
        grouplistService.deleteGrouplist("testuser", "groupIdB");

        //Then
        Assertions.assertThat(userRepository
                .findUserByUsername("testuser")
                .get()
                .getGrouplists()
                .isEmpty())
            .isTrue();
        Assertions.assertThat(grouplistRepository
                .findAll()
                .isEmpty())
            .isTrue();
    }

    @Test
    @DisplayName("Should Throw ApiRequestException When Deleting Grouplist Under Non-Existent User")
    @WithMockUser(username = "testuser")
    public void throwExceptionDeletingGrouplistInNonExistentUser() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.deleteGrouplist("XXXXXXXX", "groupIdA");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("User Not Found");
    }

    @Test
    @DisplayName("Should Throw Exception When Deleting Non-Existent Grouplist")
    @WithMockUser(username = "testuser")
    public void throwExceptionDeletingNonExistentGrouplist() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.deleteGrouplist("testuser", "groupIdXX");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Grouplist Not Found");
    }
}
