package com.academicdashboard.backend.user;

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

import com.academicdashboard.backend.checklist.Checklist;
import com.academicdashboard.backend.checklist.ChecklistRepository;
import com.academicdashboard.backend.checklist.Grouplist;
import com.academicdashboard.backend.checklist.GrouplistRepository;
import com.academicdashboard.backend.config.TestData;
import com.academicdashboard.backend.exception.ApiRequestException;

@Testcontainers
@DataMongoTest
public class UserServiceTest {
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GrouplistRepository grouplistRepository;
    @Autowired
    private ChecklistRepository checklistRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    private TestData testData;
    private UserService userService;


    @BeforeEach
    public void setUp() {
        this.userService = new UserService(
                userRepository, 
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
    @DisplayName("Should Get User's Checklists")
    @WithMockUser(username = "testuser")
    public void shouldGetUsersChecklists() {
        //When
        List<Checklist> checklists = userService.getChecklists("testuser");

        //Then
        Assertions.assertThat(checklists.size()).isEqualTo(2);
        Assertions.assertThat(checklists.get(0).getUsername()).isEqualTo("testuser");
        Assertions.assertThat(checklists.get(0).getListId()).isEqualTo("listIdC1");
        Assertions.assertThat(checklists.get(0).getGroupId()).isEqualTo("");
        Assertions.assertThat(checklists.get(0).getCheckpoints().size()).isEqualTo(4);
        Assertions.assertThat(checklists.get(0).getCompletedPoints().size()).isEqualTo(0);
        Assertions.assertThat(checklists.get(1).getUsername()).isEqualTo("testuser");
        Assertions.assertThat(checklists.get(1).getListId()).isEqualTo("listIdD1");
        Assertions.assertThat(checklists.get(1).getGroupId()).isEqualTo("");
        Assertions.assertThat(checklists.get(1).getCheckpoints().size()).isEqualTo(0);
        Assertions.assertThat(checklists.get(1).getCompletedPoints().size()).isEqualTo(2);
        Assertions.assertThat(checklists.get(1)
                .getCompletedPoints()
                .get(1)
                .getCompletedSubpoints().size())
            .isEqualTo(2);
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Getting Checklists From Non-Existent User")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenGettingChecklistsFromNonExistentUser() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            userService.getChecklists("XXXXXX");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("User Not Found");
    } 

    @Test
    @DisplayName("Should Get User's Grouplists")
    @WithMockUser(username = "testuser")
    public void shouldGetUsersGrouplists() {
        //When
        List<Grouplist> grouplists = userService.getGrouplists("testuser");

        //Then
        Assertions.assertThat(grouplists.size()).isEqualTo(2);
        Assertions.assertThat(grouplists.get(0).getGroupId()).isEqualTo("groupIdA");
        Assertions.assertThat(grouplists.get(0).getTitle()).isEqualTo("Grouplist Title A");
        Assertions.assertThat(grouplists.get(0).getChecklists().size()).isEqualTo(2);
        Assertions.assertThat(grouplists.get(0).getChecklists().get(0).getListId()).isEqualTo("listIdA1");
        Assertions.assertThat(grouplists.get(0).getChecklists().get(1).getListId()).isEqualTo("listIdA2");
        Assertions.assertThat(grouplists.get(1).getGroupId()).isEqualTo("groupIdB");
        Assertions.assertThat(grouplists.get(1).getTitle()).isEqualTo("Grouplist Title B");
        Assertions.assertThat(grouplists.get(1).getChecklists().size()).isEqualTo(2);
        Assertions.assertThat(grouplists.get(1).getChecklists().get(0).getListId()).isEqualTo("listIdB1");
        Assertions.assertThat(grouplists.get(1).getChecklists().get(1).getListId()).isEqualTo("listIdB2");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Getting Grouplists From Non-Existent User")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenGettingGrouplistsFromNonExistentUser() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            userService.getGrouplists("XXXXXXX");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("User Not Found");
    } 

    @Test
    @DisplayName("Should Reorder User's Checklists")
    @WithMockUser(username = "testuser")
    public void shouldReorderUsersChecklists() {
        List<Checklist> editChecklists = new ArrayList<>();
        Checklist checklistD1 = Checklist.builder()
            .username("testuser")
            .listId("listIdD1")
            .title("Checklist Title D1")
            .groupId("")
            .build();
        Checklist checklistC1 = Checklist.builder()
            .username("testuser")
            .listId("listIdC1")
            .title("Checklist Title C1")
            .groupId("")
            .build();
        editChecklists.add(checklistD1);
        editChecklists.add(checklistC1);

        //When
        List<Checklist> checklists = userService.reorderChecklists("testuser", editChecklists);

        //Then
        Assertions.assertThat(checklists.size()).isEqualTo(2);
        Assertions.assertThat(checklists.get(0).getUsername()).isEqualTo("testuser");
        Assertions.assertThat(checklists.get(0).getListId()).isEqualTo("listIdD1");
        Assertions.assertThat(checklists.get(0).getTitle()).isEqualTo("Checklist Title D1");
        Assertions.assertThat(checklists.get(0).getGroupId()).isEqualTo("");
        Assertions.assertThat(checklists.get(0).getCheckpoints().size()).isEqualTo(0);
        Assertions.assertThat(checklists.get(0).getCompletedPoints().size()).isEqualTo(2);
        Assertions.assertThat(checklists.get(1).getUsername()).isEqualTo("testuser");
        Assertions.assertThat(checklists.get(1).getListId()).isEqualTo("listIdC1");
        Assertions.assertThat(checklists.get(1).getTitle()).isEqualTo("Checklist Title C1");
        Assertions.assertThat(checklists.get(1).getGroupId()).isEqualTo("");
        Assertions.assertThat(checklists.get(1).getCheckpoints().size()).isEqualTo(4);
        Assertions.assertThat(checklists.get(1).getCompletedPoints().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Reordering Checklists From Non-Existent User")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenRorderingChecklistsFromNonExistentUser() {
        List<Checklist> checklists = new ArrayList<>();

        //When-Then
        Assertions.assertThatThrownBy(() -> {
            userService.reorderChecklists("XXXXXXXX", checklists);
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("User Not Found");
    } 

    @Test
    @DisplayName("Should Throw an ApiRequestException When Reordering a Checklist that Doesn't Belong to User")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenReorderingChecklistsThatDoesNotBelongToUser() {
        List<Checklist> checklists = new ArrayList<>();

        //Checklist Belongs to "testuser01"
        Checklist checklist = Checklist.builder()
            .username("testuser")
            .listId("listId1")
            .groupId("")
            .build();
        checklists.add(checklist);

        //When-Then
        Assertions.assertThatThrownBy(() -> {
            userService.reorderChecklists("testuser", checklists);
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Checklist Not Found");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Reordering a Grouped Checklist")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenReorderingGroupedChecklists() {
        List<Checklist> checklists = new ArrayList<>();

        //Checklist Belongs to "testuser01"
        Checklist checklist = Checklist.builder()
            .username("testuser")
            .listId("listIdA1")
            .groupId("")
            .build();
        checklists.add(checklist);

        //When-Then
        Assertions.assertThatThrownBy(() -> {
            userService.reorderChecklists("testuser", checklists);
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Checklist Not Found");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Reordering a Checklists That Exceeds User's Checklists Limit of 20")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenReorderingChecklistsThatExceedsLimit() {
        //Checklist Limit Exceeded: 20
        List<Checklist> checklists = new ArrayList<>();
        for(int i = 1; i <= 21; i++) {
            String listId = "listId" + Integer.toString(i);
            String title = "Checklist Title " + Integer.toString(i);

            Checklist checklist = Checklist.builder()
                .username("testuser")
                .listId(listId)
                .title(title)
                .groupId("")
                .checkpoints(new ArrayList<>())
                .completedPoints(new ArrayList<>())
                .build();

            checklists.add(checklist);
        }

        //When-Then
        Assertions.assertThatThrownBy(() -> {
            userService.reorderChecklists("testuser", checklists);
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("User's Checklists Limit Exceeded: 20");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Reordering a Non-Existent Checklist")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenReorderingNonexistentChecklists() {
        List<Checklist> checklists = new ArrayList<>();

        //Checklist Belongs to "testuser01"
        Checklist checklist = Checklist.builder()
            .username("testuser")
            .listId("XXXXXX")
            .groupId("")
            .build();
        checklists.add(checklist);

        //When-Then
        Assertions.assertThatThrownBy(() -> {
            userService.reorderChecklists("testuser", checklists);
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Checklist Not Found");
    }

    @Test
    @DisplayName("Should Reorder User's Grouplists")
    @WithMockUser(username = "testuser")
    public void shouldReorderUsersGrouplist() {
        List<Grouplist> editGrouplists = new ArrayList<>();
        Grouplist grouplistB = Grouplist.builder()
            .username("testuser")
            .groupId("groupIdB")
            .title("Grouplist Title B")
            .build();
        Grouplist grouplistA = Grouplist.builder()
            .username("testuser")
            .groupId("groupIdA")
            .title("Grouplist Title A")
            .build();
        editGrouplists.add(grouplistB);
        editGrouplists.add(grouplistA);

        //When
        List<Grouplist> grouplists = userService.reorderGrouplists("testuser", editGrouplists);

        //Then
        Assertions.assertThat(grouplists.size()).isEqualTo(2);
        Assertions.assertThat(grouplists.get(0).getUsername()).isEqualTo("testuser");
        Assertions.assertThat(grouplists.get(0).getGroupId()).isEqualTo("groupIdB");
        Assertions.assertThat(grouplists.get(0).getTitle()).isEqualTo("Grouplist Title B");
        Assertions.assertThat(grouplists.get(0).getChecklists().size()).isEqualTo(2);
        Assertions.assertThat(grouplists.get(1).getUsername()).isEqualTo("testuser");
        Assertions.assertThat(grouplists.get(1).getGroupId()).isEqualTo("groupIdA");
        Assertions.assertThat(grouplists.get(1).getTitle()).isEqualTo("Grouplist Title A");
        Assertions.assertThat(grouplists.get(1).getChecklists().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Reordering Grouplists From Non-Existent User")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenRorderingGrouplistsFromNonExistentUser() {
        List<Grouplist> grouplists = new ArrayList<>();

        //When-Then
        Assertions.assertThatThrownBy(() -> {
            userService.reorderGrouplists("XXXXXXXX", grouplists);
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("User Not Found");
    } 

    @Test
    @DisplayName("Should Throw an ApiRequestException When Reordering Grouplist That Doesn't Belong to User")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenReorderingGrouplistThatDoesNotBelongToUser() {
        List<Grouplist> grouplists = new ArrayList<>();

        //Grouplist Belongs to "testuser01"
        Grouplist grouplist = Grouplist.builder()
            .username("testuser")
            .groupId("groupId01")
            .build();
        grouplists.add(grouplist);

        //When-Then
        Assertions.assertThatThrownBy(() -> {
            userService.reorderGrouplists("testuser", grouplists);
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Grouplist Not Found");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Reordering Non-Existent Grouplist")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenReorderingNonexistentGrouplist() {
        List<Grouplist> grouplists = new ArrayList<>();
        Grouplist grouplist = Grouplist.builder()
            .username("testuser")
            .groupId("XXXXXX")
            .title("Grouplist Title")
            .build();
        grouplists.add(grouplist);

        //When-Then
        Assertions.assertThatThrownBy(() -> {
            userService.reorderGrouplists("testuser", grouplists);
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Grouplist Not Found");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Reordering a Grouplists That Exceeds User's Grouplists Limit of 20")
    @WithMockUser(username = "testuser")
    public void throwExceptionWhenReorderingGrouplistsThatExceedsLimit() {
        //Grouplist that Exceeds Limit: 20
        List<Grouplist> grouplists = new ArrayList<>();
        for(int i = 1; i <= 21; i++) {
            String groupId = "groupId" + Integer.toString(i);
            String title = "Grouplist Title " + Integer.toString(i);

            Grouplist grouplist = Grouplist.builder()
                .username("testuser")
                .groupId(groupId)
                .title(title)
                .build();
            grouplists.add(grouplist);
        }

        //When-Then
        Assertions.assertThatThrownBy(() -> {
            userService.reorderGrouplists("testuser", grouplists);
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("User's Grouplist Limit Exceeded: 20");
    }

}
