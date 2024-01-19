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
                .get().getUsername())
            .isEqualTo("testuser");
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdC")
                .get().getGroupId())
            .isEqualTo("groupIdC");
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdC")
                .get().getUsername())
            .isEqualTo("testuser");
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
    @DisplayName("Should Edit Grouplist's Title")
    @WithMockUser(username = "testuser")
    public void editGrouplistTitle() {
        //When 
        Grouplist expectedValue01 = grouplistService.editTitle("groupIdA", "New Group Title A");
        Grouplist expectedValue02 = grouplistService.editTitle("groupIdB", "  New Group Title B      ");

        //Then
        Assertions.assertThat(grouplistRepository.findGrouplistByGroupId("groupIdA").get()).isEqualTo(expectedValue01);
        Assertions.assertThat(grouplistRepository.findGrouplistByGroupId("groupIdA").get().getTitle()).isEqualTo("New Group Title A");
        Assertions.assertThat(grouplistRepository.findGrouplistByGroupId("groupIdB").get()).isEqualTo(expectedValue02);
        Assertions.assertThat(grouplistRepository.findGrouplistByGroupId("groupIdB").get().getTitle()).isEqualTo("New Group Title B");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Modifying Grouplist's Title Under a Non-existent User")
    @WithMockUser(username = "XXXXXXXX")
    public void throwExceptionModifyingGrouplistInNonexistentUser() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.editTitle("groupIdA", "New Group Title A");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("User Not Found");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When New Grouplist's Title Exceed 20 Characters")
    @WithMockUser(username = "testuser")
    public void throwExceptionModifyingGrouplistTitleWithExceededLimit() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.editTitle("groupIdA", "012345678901234567890");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Grouplist's Title Cannot Exceed 20 Characters");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When New Grouplist's Title Is Empty")
    @WithMockUser(username = "testuser")
    public void throwExceptionModifyingGrouplistWithEmptyTitle() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.editTitle("groupIdA", "    ");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Empty Grouplist's Title");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Modifying Non-Existent Grouplist's Title")
    @WithMockUser(username = "testuser")
    public void throwExceptionModifyingNonexistentGrouplistTitle() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.editTitle("groupIdC", "New Group Title C");
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

        //When
        Grouplist returnedValue = grouplistService.editChecklists("groupIdA", reorderChecklists);

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
    @WithMockUser(username = "XXXXXXX")
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
            grouplistService.editChecklists("groupIdA", reorderChecklists);
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("User Not Found");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Reordering Non-Existent Grouplist's Checklists")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenReorderNonexistentGrouplistChecklists() {
        List<Checklist> checklists = grouplistRepository.findGrouplistByGroupId("groupIdA").get().getChecklists();

        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.editChecklists("groupIdC", checklists);
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Grouplist Not Found");
    }

    @Test
    @DisplayName("Should Create a New Checklist Under an Existing Grouplist")
    @WithMockUser(username = "testuser")
    public void createNewChecklistUnderExistingGrouplist() {
        //When 
        grouplistService.createChecklist("groupIdA", "listIdA3", "Checklist Title A3");
        grouplistService.createChecklist("groupIdB", "listIdB3", "    Checklist Title B3   ");

        //Then
        Assertions.assertThat(checklistRepository
                .findChecklistByListId("listIdA3")
                .get()
                .getUsername())
            .isEqualTo("testuser");
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
                .getUsername())
            .isEqualTo("testuser");
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
    @WithMockUser(username = "XXXXXXX")
    public void throwExceptionWhenCreateingNewChecklistUnderGrouplistUnderNonExistentUser() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.createChecklist("groupIdA", "listIdA3", "Checklist Title A3");
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
            grouplistService.createChecklist("groupIdA", listId, title);
        }
        //When-Then
        Assertions.assertThat(grouplistRepository
                .findGrouplistByGroupId("groupIdA")
                .get()
                .getChecklists()
                .size())
            .isEqualTo(20);
        Assertions.assertThatThrownBy(() -> {
            grouplistService.createChecklist("groupIdA", "listIdA21", "Checklist Title A21");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Grouplist's Checklists Limit Exceeded: 20");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Creating a New Checklist Whose Title Exceed Character Limit Under a Grouplist")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenNewChecklistTitleCharactersLimitExceeded() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.createChecklist("groupIdA", "listIdA3", "012345678901234567890123456789012345678901234567890000");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Checklist's Title Cannot Exceed 50 Characters");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Creating a New Checklist Whose Title is Empty Under a Grouplist")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenNewChecklistWithEmptyTitle() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.createChecklist("groupIdA", "listIdA3", "       ");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Empty Checklist's Title");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Creating a New Checklist Under a Non-Existent Grouplist")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenCreatingNewChecklistUnderNonExistentChecklist() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.createChecklist("groupIdX", "listIdA3", "Checklist Title A3");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Grouplist Not Found");
    }

    @Test
    @DisplayName("Should Delete Grouplist And Delete it's Checklists")
    @WithMockUser(username = "testuser")
    public void deleteGrouplistAndDeleteChecklists() {
        //When 
        grouplistService.deleteGrouplist("groupIdA");
        grouplistService.deleteGrouplist("groupIdB");

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
    @WithMockUser(username = "XXXXXXX")
    public void throwExceptionDeletingGrouplistInNonExistentUser() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.deleteGrouplist("groupIdA");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("User Not Found");
    }

    @Test
    @DisplayName("Should Throw Exception When Deleting Non-Existent Grouplist")
    @WithMockUser(username = "testuser")
    public void throwExceptionDeletingNonExistentGrouplist() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            grouplistService.deleteGrouplist("groupIdXX");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Grouplist Not Found");
    }
}
