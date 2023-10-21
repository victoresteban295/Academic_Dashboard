// package com.academicdashboard.backend.checklist;
//
// import org.assertj.core.api.Assertions;
// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
// import org.springframework.data.mongodb.core.MongoTemplate;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.testcontainers.junit.jupiter.Testcontainers;
//
// import com.academicdashboard.backend.exception.ApiRequestException;
// import com.academicdashboard.backend.user.UserRepository;
//
// @Testcontainers
// @DataMongoTest
// public class GrouplistServiceTest {
//
//     @Autowired
//     private UserRepository userRepository;
//     @Autowired
//     private GrouplistRepository grouplistRepository;
//     @Autowired
//     private ChecklistRepository checklistRepository;
//     @Autowired
//     private CheckpointRepository checkpointRepository;
//     @Autowired
//     private MongoTemplate mongoTemplate;
//
//     private TestData testData;
//     private GrouplistService grouplistService;
//
//     @BeforeEach
//     public void setUp() {
//         this.grouplistService = new GrouplistService(
//                 grouplistRepository, 
//                 mongoTemplate);
//         this.testData = new TestData(
//                 userRepository, 
//                 grouplistRepository, 
//                 checklistRepository, 
//                 checkpointRepository);
//         testData.populateDatabase();
//     }
//
//     @AfterEach
//     public void cleanup() {
//         testData.cleanupDatabase();
//     }
//
//     @Test
//     @DisplayName("Should Create a New Grouplist Under Student")
//     @WithMockUser(username = "testuser")
//     public void shouldCreateNewGrouplist() {
//         //When
//         Grouplist returnedValue = grouplistService.createGrouplist("testuser", "New Grouplist Title");
//
//         //Then
//         Assertions.assertThat(grouplistRepository
//                 .findGrouplistByGroupId(returnedValue.getGroupId())
//                 .get())
//             .isEqualTo(returnedValue);
//         Assertions.assertThat(userRepository
//                 .findUserByUsername("testuser")
//                 .get()
//                 .getGrouplists()
//                 .contains(returnedValue))
//             .isTrue();
//     }
//
//     @Test
//     @DisplayName("Should Throw an ApiRequestException When Creating New Grouplist Under Non-existent User")
//     @WithMockUser(username = "testuser")
//     public void throwExceptionInsertingGrouplistInNonexistentUser() {
//         Assertions.assertThatThrownBy(() -> {
//             grouplistService.createGrouplist("XXXXX", "New Grouplist Title");
//         }).isInstanceOf(ApiRequestException.class)
//             .hasMessage("Provided Wrong Username");
//     }
//
//     @Test
//     @DisplayName("Should Modify an Existing Grouplist")
//     @WithMockUser(username = "testuser")
//     public void modifyExistingGrouplistTitle() {
//         //When 
//         Grouplist expectedValue01 = grouplistService.modifyGrouplist("testuser", "groupIdA", "New Title For groupIdA");
//         Grouplist expectedValue02 = grouplistService.modifyGrouplist("testuser", "groupIdB", "New Title For groupIdB");
//
//         //Then
//         Assertions.assertThat(grouplistRepository.findGrouplistByGroupId("groupIdA").get()).isEqualTo(expectedValue01);
//         Assertions.assertThat(grouplistRepository.findGrouplistByGroupId("groupIdB").get()).isEqualTo(expectedValue02);
//     }
//
//     @Test
//     @DisplayName("Should Throw an ApiRequestException When Modifying Non-existent Grouplist")
//     @WithMockUser(username = "testuser")
//     public void throwExceptionModifyingNonexistentGrouplist() {
//         //When-Then
//         Assertions.assertThatThrownBy(() -> {
//             grouplistService.modifyGrouplist("testuser", "XXXXX", "New Title For XXXXX");
//         }).isInstanceOf(ApiRequestException.class)
//             .hasMessage("Grouplist You Wanted to Modify Doesn't Exist");
//     }
//
//     @Test
//     @DisplayName("Should Create a New Checklist Under an Existing Grouplist")
//     @WithMockUser(username = "testuser")
//     public void createNewChecklistUnderExistingGrouplist() {
//         //When 
//         grouplistService.addNewToGrouplist("testuser", "groupIdA", "New Checklist Under GrouplistIdA");
//         grouplistService.addNewToGrouplist("testuser", "groupIdB", "New Checklist Under GrouplistIdB");
//
//         //Then
//         Assertions.assertThat(grouplistRepository.findGrouplistByGroupId("groupIdA").get().getChecklists().size()).isEqualTo(3);
//         Assertions.assertThat(grouplistRepository.findGrouplistByGroupId("groupIdB").get().getChecklists().size()).isEqualTo(3);
//         Assertions.assertThat(grouplistRepository.findGrouplistByGroupId("groupIdA").get().getChecklists().get(2).getTitle()).isEqualTo("New Checklist Under GrouplistIdA");
//         Assertions.assertThat(grouplistRepository.findGrouplistByGroupId("groupIdB").get().getChecklists().get(2).getTitle()).isEqualTo("New Checklist Under GrouplistIdB");
//     }
//
//     @Test
//     @DisplayName("Should Throw an ApiRequestException When Adding New Checklist to Non-existent Grouplist")
//     @WithMockUser(username = "testuser")
//     public void throwExceptionAddingNewChecklistToNonExistingGrouplist() {
//         //When-Then
//         Assertions.assertThatThrownBy(() -> {
//             grouplistService.addNewToGrouplist("testuser", "XXXXX", "New Checklist Under GrouplistXXX");
//         }).isInstanceOf(ApiRequestException.class)
//             .hasMessage("Grouplist You Wanted to Modify Doesn't Exist");
//     }
//
//     @Test
//     @DisplayName("Should Add an Existing Checklist Under an Existing Grouplist")
//     @WithMockUser(username = "testuser")
//     public void addExistingChecklistUnderExistingGrouplist() {
//         //When 
//         grouplistService.addExistToGrouplist("testuser", "groupIdA", "listIdC1");
//         grouplistService.addExistToGrouplist("testuser", "groupIdB", "listIdD");
//
//         //Then
//         Assertions.assertThat(grouplistRepository.findGrouplistByGroupId("groupIdA").get().getChecklists().size()).isEqualTo(3);
//         Assertions.assertThat(grouplistRepository.findGrouplistByGroupId("groupIdB").get().getChecklists().size()).isEqualTo(3);
//         Assertions.assertThat(userRepository.findUserByUsername("testuser").get().getChecklists().isEmpty()).isTrue();
//         Assertions.assertThat(grouplistRepository.findGrouplistByGroupId("groupIdA").get().getChecklists().get(2)).isEqualTo(checklistRepository.findChecklistByListId("listIdC1").get());
//         Assertions.assertThat(grouplistRepository.findGrouplistByGroupId("groupIdB").get().getChecklists().get(2)).isEqualTo(checklistRepository.findChecklistByListId("listIdD").get());
//     }
//
//     @Test
//     @DisplayName("Should Throw Exception When Adding Checklist Under Grouplist In Non-Existent User")
//     @WithMockUser(username = "testuser")
//     public void shouldThrowExceptionAddingChecklistUnderGrouplistInNonExistentUser() {
//         //When-Then
//         Assertions.assertThatThrownBy(() -> {
//             grouplistService.addExistToGrouplist("XXXXXXX", "groupIdA", "listIdC1");
//         }).isInstanceOf(ApiRequestException.class)
//             .hasMessage("Provided Wrong Username");
//     }
//
//     @Test
//     @DisplayName("Should Throw Exception When Adding a Non-Existent Checklist Under an Existing Grouplist")
//     @WithMockUser(username = "testuser")
//     public void shouldThrowExceptionAddingNonExistentChecklistUnderExistingGrouplist() {
//         //When-Then
//         Assertions.assertThatThrownBy(() -> {
//             grouplistService.addExistToGrouplist("testuser", "groupIdA", "XXXXX");
//         }).isInstanceOf(ApiRequestException.class)
//             .hasMessage("Checklist You Wanted to Modify Doesn't Exist");
//     }
//
//     @Test
//     @DisplayName("Should Throw Exception When Adding an Existent Checklist Under a Non-Existent Grouplist")
//     @WithMockUser(username = "testuser")
//     public void shouldThrowExceptionAddingExistingChecklistUnderNonExistentGrouplist() {
//         //When-Then
//         Assertions.assertThatThrownBy(() -> {
//             grouplistService.addExistToGrouplist("testuser", "XXXXX", "listIdC1");
//         }).isInstanceOf(ApiRequestException.class)
//             .hasMessage("Grouplist You Wanted to Modify Doesn't Exist");
//     }
//
//     @Test
//     @DisplayName("Should Remove an Existing Checklist Under an Existing Grouplist")
//     @WithMockUser(username = "testuser")
//     public void removeExistingChecklistUnderExistingGrouplist() {
//         //When 
//         grouplistService.removefromGrouplist("testuser", "groupIdA", "listIdA1");
//         grouplistService.removefromGrouplist("testuser", "groupIdB", "listIdB2");
//
//         //Then
//         Assertions.assertThat(grouplistRepository.findGrouplistByGroupId("groupIdA").get().getChecklists().size()).isEqualTo(1);
//         Assertions.assertThat(grouplistRepository.findGrouplistByGroupId("groupIdB").get().getChecklists().size()).isEqualTo(1);
//         Assertions.assertThat(userRepository.findUserByUsername("testuser").get().getChecklists().size()).isEqualTo(4);
//         Assertions.assertThat(userRepository.findUserByUsername("testuser").get().getChecklists().contains(checklistRepository.findChecklistByListId("listIdA1").get())).isTrue();
//         Assertions.assertThat(userRepository.findUserByUsername("testuser").get().getChecklists().contains(checklistRepository.findChecklistByListId("listIdB2").get())).isTrue();
//         Assertions.assertThat(checklistRepository.findAll().size()).isEqualTo(6);
//     }
//
//     @Test
//     @DisplayName("Should Throw Exception When Removing Checklist Under Grouplist Under Non-existent User")
//     @WithMockUser(username = "testuser")
//     public void throwExceptionWhenRemovingChecklistUnderGrouplistUnderNonexistentUser() {
//         //When-Then
//         Assertions.assertThatThrownBy(() -> {
//             grouplistService.removefromGrouplist("XXXXXXX", "groupIdA", "listIdA1");
//         }).isInstanceOf(ApiRequestException.class)
//             .hasMessage("Provided Wrong Username");
//     }
//
//     @Test
//     @DisplayName("Should Throw Exception When Removing a Non-Existent Checklist Under an Existing Grouplist")
//     @WithMockUser(username = "testuser")
//     public void throwExceptionWhenRemovingNonExistentChecklistUnderExistingGrouplist() {
//         //When-Then
//         Assertions.assertThatThrownBy(() -> {
//             grouplistService.removefromGrouplist("testuser", "groupIdA", "XXXXX");
//         }).isInstanceOf(ApiRequestException.class)
//             .hasMessage("Checklist You Wanted to Modify Doesn't Exist");
//     }
//
//     @Test
//     @DisplayName("Should Throw Exception When Removing an Existing Checklist Under a Non-Existent Grouplist")
//     @WithMockUser(username = "testuser")
//     public void throwExceptionWhenRemovingExistingChecklistUnderNonExistentGrouplist() {
//         //When-Then
//         Assertions.assertThatThrownBy(() -> {
//             grouplistService.removefromGrouplist("testuser", "XXXXX", "listIdA1");
//         }).isInstanceOf(ApiRequestException.class)
//             .hasMessage("Grouplist You Wanted to Modify Doesn't Exist");
//     }
//
//     @Test
//     @DisplayName("Should Delete Grouplist without Deleteing it's Checklists")
//     @WithMockUser(username = "testuser")
//     public void deleteGrouplistWithoutDeletingChecklists() {
//         //When 
//         grouplistService.deleteGrouplist("testuser", "groupIdA", false);
//         grouplistService.deleteGrouplist("testuser", "groupIdB", false);
//
//         //Then
//         Assertions.assertThat(userRepository.findUserByUsername("testuser").get().getGrouplists().isEmpty()).isTrue();
//         Assertions.assertThat(userRepository.findUserByUsername("testuser").get().getChecklists().size()).isEqualTo(6);
//         Assertions.assertThat(checklistRepository.findChecklistByListId("listIdA1")).isNotNull();
//         Assertions.assertThat(checklistRepository.findChecklistByListId("listIdA2")).isNotNull();
//         Assertions.assertThat(checklistRepository.findChecklistByListId("listIdB1")).isNotNull();
//         Assertions.assertThat(checklistRepository.findChecklistByListId("listIdB2")).isNotNull();
//     }
//
//     @Test
//     @DisplayName("Should Delete Grouplist And Delete it's Checklists")
//     @WithMockUser(username = "testuser")
//     public void deleteGrouplistAndDeleteChecklists() {
//         //When 
//         grouplistService.deleteGrouplist("testuser", "groupIdA", true);
//         grouplistService.deleteGrouplist("testuser", "groupIdB", true);
//
//         //Then
//         Assertions.assertThat(userRepository.findUserByUsername("testuser").get().getGrouplists().isEmpty()).isTrue();
//         Assertions.assertThat(userRepository.findUserByUsername("testuser").get().getChecklists().size()).isEqualTo(2);
//         Assertions.assertThat(checklistRepository.findAll().size()).isEqualTo(2);
//     }
//
//     @Test
//     @DisplayName("Should Throw Exception When Deleting Grouplist In Non-Existent User")
//     @WithMockUser(username = "testuser")
//     public void throwExceptionDeletingGrouplistInNonExistentUser() {
//         //When-Then
//         Assertions.assertThatThrownBy(() -> {
//             grouplistService.deleteGrouplist("XXXXXXXX", "groupIdA", true);
//         }).isInstanceOf(ApiRequestException.class)
//             .hasMessage("Provided Wrong Username");
//     }
//
//     @Test
//     @DisplayName("Should Throw Exception When Deleting Non-Existent Grouplist")
//     @WithMockUser(username = "testuser")
//     public void throwExceptionDeletingNonExistentGrouplist() {
//         //When-Then
//         Assertions.assertThatThrownBy(() -> {
//             grouplistService.deleteGrouplist("testuser", "XXXXX", true);
//         }).isInstanceOf(ApiRequestException.class)
//             .hasMessage("Grouplist You Wanted to Delete Doesn't Exist");
//
//         Assertions.assertThatThrownBy(() -> {
//             grouplistService.deleteGrouplist("testuser", "XXXXX", false);
//         }).isInstanceOf(ApiRequestException.class)
//             .hasMessage("Grouplist You Wanted to Delete Doesn't Exist");
//     }
// }
