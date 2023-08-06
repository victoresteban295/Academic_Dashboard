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
public class CheckpointServiceTest {

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
    private CheckpointService checkpointService;

    @BeforeEach
    public void setUp() {
        this.checkpointService = new CheckpointService(
                checkpointRepository, 
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
    @DisplayName("Should Create a New Checkpoint Under an Existing Checklist")
    @WithMockUser(username = "testuser")
    public void shouldCreateNewCheckpointUnderExistingChecklist() {
        //When
        Checklist checklist01 = checkpointService.addCheckpoint("testuser", "listIdA1", "New Checkpoint Under listIdA1");
        int size01 = checklist01.getCheckpoints().size() - 1;
        Checkpoint expectedValue01 = checklist01.getCheckpoints().get(size01);

        Checklist checklist02 = checkpointService.addCheckpoint("testuser", "listIdA2", "New Checkpoint Under listIdA2");
        int size02 = checklist02.getCheckpoints().size() - 1;
        Checkpoint expectedValue02 = checklist02.getCheckpoints().get(size02);

        Checklist checklist03 = checkpointService.addCheckpoint("testuser", "listIdB1", "New Checkpoint Under listIdB1");
        int size03 = checklist03.getCheckpoints().size() - 1;
        Checkpoint expectedValue03 = checklist03.getCheckpoints().get(size03);

        Checklist checklist04 = checkpointService.addCheckpoint("testuser", "listIdB2", "New Checkpoint Under listIdB2");
        int size04 = checklist04.getCheckpoints().size() - 1;
        Checkpoint expectedValue04 = checklist04.getCheckpoints().get(size04);

        Checklist checklist05 = checkpointService.addCheckpoint("testuser", "listIdC1", "New Checkpoint Under listIdC1");
        int size05 = checklist05.getCheckpoints().size() - 1;
        Checkpoint expectedValue05 = checklist05.getCheckpoints().get(size05);

        Checklist checklist06 = checkpointService.addCheckpoint("testuser", "listIdD", "New Checkpoint Under listIdD");
        int size06 = checklist06.getCheckpoints().size() - 1;
        Checkpoint expectedValue06 = checklist06.getCheckpoints().get(size06);

        //Then
        Assertions.assertThat(checkpointRepository
                .findCheckpointByPointId(expectedValue01.getPointId())
                .get())
            .isEqualTo(expectedValue01);
        Assertions.assertThat(checkpointRepository
                .findCheckpointByPointId(expectedValue02.getPointId())
                .get())
            .isEqualTo(expectedValue02);
        Assertions.assertThat(checkpointRepository
                .findCheckpointByPointId(expectedValue03.getPointId())
                .get())
            .isEqualTo(expectedValue03);
        Assertions.assertThat(checkpointRepository
                .findCheckpointByPointId(expectedValue04.getPointId())
                .get())
            .isEqualTo(expectedValue04);
        Assertions.assertThat(checkpointRepository
                .findCheckpointByPointId(expectedValue05.getPointId())
                .get())
            .isEqualTo(expectedValue05);
        Assertions.assertThat(checkpointRepository
                .findCheckpointByPointId(expectedValue06.getPointId())
                .get())
            .isEqualTo(expectedValue06);
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Creating a New Checkpoint Under a Non-Existent Checklist")
    @WithMockUser(username = "testuser")
    public void throwExceptionCreatingNewCheckpointUnderNonExistentChecklist() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checkpointService.addCheckpoint("testuser", "XXXXX", "Checkpoint Content");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Checklist You Provided Doesn't Exist");
    }

    @Test
    @DisplayName("Should Modify an Existing Checkpoint")
    @WithMockUser(username = "testuser")
    public void shouldModifyExistingCheckpoint() {
        //When
        //Subcheckpoints
        Checkpoint expectedValue13 = checkpointService.modifyCheckpoint("testuser", "pointIdA11A", "New ContentA11A");
        Checkpoint expectedValue14 = checkpointService.modifyCheckpoint("testuser", "pointIdA11B", "New ContentA11B");
        Checkpoint expectedValue15 = checkpointService.modifyCheckpoint("testuser", "pointIdB11A", "New ContentB11A");
        Checkpoint expectedValue16 = checkpointService.modifyCheckpoint("testuser", "pointIdB11B", "New ContentB11B");
        Checkpoint expectedValue17 = checkpointService.modifyCheckpoint("testuser", "pointIdC11A", "New ContentC11A");
        Checkpoint expectedValue18 = checkpointService.modifyCheckpoint("testuser", "pointIdC11B", "New ContentC11B");
        //Checkpoints
        Checkpoint expectedValue01 = checkpointService.modifyCheckpoint("testuser", "pointIdA11", "New ContentA11");
        Checkpoint expectedValue02 = checkpointService.modifyCheckpoint("testuser", "pointIdA12", "New ContentA12");
        Checkpoint expectedValue03 = checkpointService.modifyCheckpoint("testuser", "pointIdA21", "New ContentA21");
        Checkpoint expectedValue04 = checkpointService.modifyCheckpoint("testuser", "pointIdA22", "New ContentA22");
        Checkpoint expectedValue05 = checkpointService.modifyCheckpoint("testuser", "pointIdB11", "New ContentB11");
        Checkpoint expectedValue06 = checkpointService.modifyCheckpoint("testuser", "pointIdB12", "New ContentB12");
        Checkpoint expectedValue07 = checkpointService.modifyCheckpoint("testuser", "pointIdB21", "New ContentB21");
        Checkpoint expectedValue08 = checkpointService.modifyCheckpoint("testuser", "pointIdB22", "New ContentB22");
        Checkpoint expectedValue09 = checkpointService.modifyCheckpoint("testuser", "pointIdC11", "New ContentC11");
        Checkpoint expectedValue10 = checkpointService.modifyCheckpoint("testuser", "pointIdC12", "New ContentC12");
        Checkpoint expectedValue11 = checkpointService.modifyCheckpoint("testuser", "pointIdD1", "New ContentD1");
        Checkpoint expectedValue12 = checkpointService.modifyCheckpoint("testuser", "pointIdD2", "New ContentD2");

        //Then
        //Checkpoints
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdA11").get()).isEqualTo(expectedValue01);
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdA12").get()).isEqualTo(expectedValue02);
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdA21").get()).isEqualTo(expectedValue03);
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdA22").get()).isEqualTo(expectedValue04);
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdB11").get()).isEqualTo(expectedValue05);
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdB12").get()).isEqualTo(expectedValue06);
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdB21").get()).isEqualTo(expectedValue07);
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdB22").get()).isEqualTo(expectedValue08);
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdC11").get()).isEqualTo(expectedValue09);
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdC12").get()).isEqualTo(expectedValue10);
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdD1").get()).isEqualTo(expectedValue11);
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdD2").get()).isEqualTo(expectedValue12);
        //SubCheckpoints
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdA11A").get()).isEqualTo(expectedValue13);
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdA11B").get()).isEqualTo(expectedValue14);
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdB11A").get()).isEqualTo(expectedValue15);
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdB11B").get()).isEqualTo(expectedValue16);
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdC11A").get()).isEqualTo(expectedValue17);
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdC11B").get()).isEqualTo(expectedValue18);
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Modifying a Non-Existent Checkpoint")
    @WithMockUser(username = "testuser")
    public void throwExceptionModifyingNonExistentCheckpoint() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checkpointService.modifyCheckpoint("testuser", "XXXXX", "New Content");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Checkpoint You Provided Doesn't Exist");
    }

    @Test
    @DisplayName("Should Delete Checkpoint and Subcheckpoints")
    @WithMockUser(username = "testuser")
    public void shouldDeleteCheckpointAndSubcheckpoints() {
        //When 
        checkpointService.deleteCheckpoint("testuser", "pointIdA11");
        checkpointService.deleteCheckpoint("testuser", "pointIdA12");
        checkpointService.deleteCheckpoint("testuser", "pointIdA21");
        checkpointService.deleteCheckpoint("testuser", "pointIdA22");
        checkpointService.deleteCheckpoint("testuser", "pointIdB11");
        checkpointService.deleteCheckpoint("testuser", "pointIdB12");
        checkpointService.deleteCheckpoint("testuser", "pointIdB21");
        checkpointService.deleteCheckpoint("testuser", "pointIdB22");
        checkpointService.deleteCheckpoint("testuser", "pointIdC11");
        checkpointService.deleteCheckpoint("testuser", "pointIdC12");
        checkpointService.deleteCheckpoint("testuser", "pointIdD1");
        checkpointService.deleteCheckpoint("testuser", "pointIdD2");

        //Then
        Assertions.assertThat(checkpointRepository.findAll().isEmpty()).isTrue();
        Assertions.assertThat(checklistRepository.findChecklistByListId("listIdA1").get().getCheckpoints().isEmpty()).isTrue();
        Assertions.assertThat(checklistRepository.findChecklistByListId("listIdA2").get().getCheckpoints().isEmpty()).isTrue();
        Assertions.assertThat(checklistRepository.findChecklistByListId("listIdB1").get().getCheckpoints().isEmpty()).isTrue();
        Assertions.assertThat(checklistRepository.findChecklistByListId("listIdB2").get().getCheckpoints().isEmpty()).isTrue();
        Assertions.assertThat(checklistRepository.findChecklistByListId("listIdC1").get().getCheckpoints().isEmpty()).isTrue();
        Assertions.assertThat(checklistRepository.findChecklistByListId("listIdD").get().getCheckpoints().isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Should Only Delete Subcheckpoints")
    @WithMockUser(username = "testuser")
    public void shouldDeleteSubcheckpointsOnly() {
        //When
        checkpointService.deleteCheckpoint("testuser", "pointIdA11A");
        checkpointService.deleteCheckpoint("testuser", "pointIdA11B");
        checkpointService.deleteCheckpoint("testuser", "pointIdB11A");
        checkpointService.deleteCheckpoint("testuser", "pointIdB11B");
        checkpointService.deleteCheckpoint("testuser", "pointIdC11A");
        checkpointService.deleteCheckpoint("testuser", "pointIdC11B");

        //Then
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdA11").get().getSubCheckpoints().isEmpty()).isTrue();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdB11").get().getSubCheckpoints().isEmpty()).isTrue();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdC11").get().getSubCheckpoints().isEmpty()).isTrue();
        Assertions.assertThat(checkpointRepository.findAll().size()).isEqualTo(12);
    }

    @Test
    @DisplayName("Should Throw a ApiRequestException When Deleteing Non-existent Checkpoint")
    @WithMockUser(username = "testuser")
    public void throwExceptionNonexistentCheckpoint() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checkpointService.deleteCheckpoint("testuser", "XXXXX");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Checkpoint You Wanted to Delete Doesn't Exist");
    }

    @Test
    @DisplayName("Should Turn an Existing Checkpoint into a SubCheckpoint Under Another Existing Checkpoint")
    @WithMockUser(username = "testuser")
    public void shouldTurnExistingCheckpointToSubCheckpoint() {
        //When 
        checkpointService.turnIntoSubcheckpoint("testuser", "listIdA2", "pointIdA21", "pointIdA22");

        //Then
        Assertions.assertThat(checkpointRepository
                .findCheckpointByPointId("pointIdA21")
                .get()
                .getSubCheckpoints()
                .contains(checkpointRepository
                    .findCheckpointByPointId("pointIdA22")
                    .get()))
            .isTrue();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdA22").get().isSubpoint()).isTrue();
        Assertions.assertThat(checklistRepository.findChecklistByListId("listIdA2").get().getCheckpoints().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should Throw ApiRequestException When Turning an Existing Checkpoint into a SubCheckpoint Under a Non-Existent Checklist")
    @WithMockUser(username = "testuser")
    public void throwExceptionTurningExistingCheckpointToSubCheckpointInNonExistentChecklist() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checkpointService.turnIntoSubcheckpoint("testuser", "XXXXX", "pointIdA21", "pointIdA22");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Checklist You Provided Doesn't Exist");
    }

    @Test
    @DisplayName("Should Throw ApiRequestException When Turning a Non-Existent Checkpoint into a SubCheckpoint")
    @WithMockUser(username = "testuser")
    public void throwExceptionTurningNonExistentCheckpointToSubCheckpoint() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checkpointService.turnIntoSubcheckpoint("testuser", "listIdA2", "pointIdA21", "XXXXX");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("SubCheckpoint You Provided Doesn't Exist");
    }

    @Test
    @DisplayName("Should Throw ApiRequestException When Turning an Existing Checkpoint into a SubCheckpoint Under Non-Existent Parent Checkpoint")
    @WithMockUser(username = "testuser")
    public void throwExceptionTurningCheckpointToSubCheckpointUnderNonExistentParentCheckpoint() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checkpointService.turnIntoSubcheckpoint("testuser", "listIdA2", "XXXXX", "pointIdA22");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Parent Checkpoint You Provided Doesn't Exist");
    }

    @Test
    @DisplayName("Should Add a New SubCheckpoint Under Another Existing Checkpoint")
    @WithMockUser(username = "testuser")
    public void shouldAddNewSubCheckpointUnderExistingCheckpoint() {
        //When 
        checkpointService.newSubcheckpoint("testuser", "pointIdA11", "New Subcheckpoint Under pointIdA11");

        //Then
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdA11").get().getSubCheckpoints().size()).isEqualTo(3);
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdA11").get().getSubCheckpoints().get(2).getContent()).isEqualTo("New Subcheckpoint Under pointIdA11");
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdA11").get().getSubCheckpoints().get(2).isSubpoint()).isTrue();
        Assertions.assertThat(checkpointRepository.findAll().contains(checkpointRepository.findCheckpointByPointId("pointIdA11").get().getSubCheckpoints().get(2))).isTrue();
    }

    @Test
    @DisplayName("Should Throw ApiRequestException Adding New SubCheckpoint Under Non-Existent Checkpoint")
    @WithMockUser(username = "testuser")
    public void throwExceptionAddingNewSubCheckpointUnderNonExistentCheckpoint() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checkpointService.newSubcheckpoint("testuser", "XXXXX", "New Subcheckpoint Under pointIdA11");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Parent Checkpoint You Provided Doesn't Exist");
    }

    @Test
    @DisplayName("Should Turn an Existing SubCheckpoint into a Checkpoint Under an Existing Checklist")
    @WithMockUser(username = "testuser")
    public void shouldTurnExistingSubCheckpointToCheckpoint() {
        //When 
        checkpointService.reverseSubcheckpoint("testuser", "listIdA1", "pointIdA11", "pointIdA11B");

        //Then
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdA11").get().getSubCheckpoints().size()).isEqualTo(1);
        Assertions.assertThat(checklistRepository.findChecklistByListId("listIdA1").get().getCheckpoints().size()).isEqualTo(3);
        Assertions.assertThat(checklistRepository.findChecklistByListId("listIdA1").get().getCheckpoints().get(2).isSubpoint()).isFalse();
        Assertions.assertThat(checkpointRepository.findAll().contains(checkpointRepository.findCheckpointByPointId("pointIdA11B").get())).isTrue();
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Turning a Non-Existent SubCheckpoint into a Checkpoint Under an Existing Checklist")
    @WithMockUser(username = "testuser")
    public void throwExceptionTurningNonExistentSubCheckpointToCheckpoint() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checkpointService.reverseSubcheckpoint("testuser", "listIdA1", "pointIdA11", "XXXXX");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("SubCheckpoint You Provided Doesn't Exist");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Turning an Existing SubCheckpoint into a Checkpoint Under an Non-Existent Checklist")
    @WithMockUser(username = "testuser")
    public void throwExceptionTurningExistingSubCheckpointToCheckpointUnderNonExistentChecklist() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checkpointService.reverseSubcheckpoint("testuser", "XXXXX", "pointIdA11", "pointIdA11B");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Checklist You Provided Doesn't Exist");
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Turning an Existing SubCheckpoint into a Checkpoint Under a Non-Existent Checkpoint")
    @WithMockUser(username = "testuser")
    public void throwExceptionTurningExistingSubCheckpointUnderNonExistentCheckpoint() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checkpointService.reverseSubcheckpoint("testuser", "listIdA1", "XXXXX", "pointIdA11B");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Parent Checkpoint You Provided Doesn't Exist");
    }

    @Test
    @DisplayName("Should Convert a Checkpoint's isComplete to boolean true ")
    @WithMockUser(username = "testuser")
    public void shouldConvertIsCompleteToTrue() {
        //When
        //Checkpoints
        checkpointService.completeCheckpoint("testuser", "pointIdA11");
        checkpointService.completeCheckpoint("testuser", "pointIdA12");
        checkpointService.completeCheckpoint("testuser", "pointIdA21");
        checkpointService.completeCheckpoint("testuser", "pointIdA22");
        checkpointService.completeCheckpoint("testuser", "pointIdB11");
        checkpointService.completeCheckpoint("testuser", "pointIdB12");
        checkpointService.completeCheckpoint("testuser", "pointIdB21");
        checkpointService.completeCheckpoint("testuser", "pointIdB22");
        checkpointService.completeCheckpoint("testuser", "pointIdC11");
        checkpointService.completeCheckpoint("testuser", "pointIdC12");
        checkpointService.completeCheckpoint("testuser", "pointIdD1");
        checkpointService.completeCheckpoint("testuser", "pointIdD2");
        //Subcheckpoints
        checkpointService.completeCheckpoint("testuser", "pointIdA11A");
        checkpointService.completeCheckpoint("testuser", "pointIdA11B");
        checkpointService.completeCheckpoint("testuser", "pointIdB11A");
        checkpointService.completeCheckpoint("testuser", "pointIdB11B");
        checkpointService.completeCheckpoint("testuser", "pointIdC11A");
        checkpointService.completeCheckpoint("testuser", "pointIdC11B");

        //Then
        //Checkpoints
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdA11").get().isComplete()).isTrue();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdA12").get().isComplete()).isTrue();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdA21").get().isComplete()).isTrue();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdA22").get().isComplete()).isTrue();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdB11").get().isComplete()).isTrue();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdB12").get().isComplete()).isTrue();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdB21").get().isComplete()).isTrue();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdB22").get().isComplete()).isTrue();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdC11").get().isComplete()).isTrue();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdC12").get().isComplete()).isTrue();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdD1").get().isComplete()).isTrue();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdD2").get().isComplete()).isTrue();
        //Subcheckpoints
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdA11A").get().isComplete()).isTrue();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdA11B").get().isComplete()).isTrue();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdB11A").get().isComplete()).isTrue();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdB11B").get().isComplete()).isTrue();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdC11A").get().isComplete()).isTrue();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdC11B").get().isComplete()).isTrue();

        //When
        //Checkpoints
        checkpointService.completeCheckpoint("testuser", "pointIdA11");
        checkpointService.completeCheckpoint("testuser", "pointIdA12");
        checkpointService.completeCheckpoint("testuser", "pointIdA21");
        checkpointService.completeCheckpoint("testuser", "pointIdA22");
        checkpointService.completeCheckpoint("testuser", "pointIdB11");
        checkpointService.completeCheckpoint("testuser", "pointIdB12");
        checkpointService.completeCheckpoint("testuser", "pointIdB21");
        checkpointService.completeCheckpoint("testuser", "pointIdB22");
        checkpointService.completeCheckpoint("testuser", "pointIdC11");
        checkpointService.completeCheckpoint("testuser", "pointIdC12");
        checkpointService.completeCheckpoint("testuser", "pointIdD1");
        checkpointService.completeCheckpoint("testuser", "pointIdD2");
        //Subcheckpoints
        checkpointService.completeCheckpoint("testuser", "pointIdA11A");
        checkpointService.completeCheckpoint("testuser", "pointIdA11B");
        checkpointService.completeCheckpoint("testuser", "pointIdB11A");
        checkpointService.completeCheckpoint("testuser", "pointIdB11B");
        checkpointService.completeCheckpoint("testuser", "pointIdC11A");
        checkpointService.completeCheckpoint("testuser", "pointIdC11B");

        //Then
        //Checkpoints
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdA11").get().isComplete()).isFalse();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdA12").get().isComplete()).isFalse();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdA21").get().isComplete()).isFalse();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdA22").get().isComplete()).isFalse();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdB11").get().isComplete()).isFalse();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdB12").get().isComplete()).isFalse();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdB21").get().isComplete()).isFalse();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdB22").get().isComplete()).isFalse();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdC11").get().isComplete()).isFalse();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdC12").get().isComplete()).isFalse();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdD1").get().isComplete()).isFalse();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdD2").get().isComplete()).isFalse();
        //Subcheckpoints
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdA11A").get().isComplete()).isFalse();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdA11B").get().isComplete()).isFalse();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdB11A").get().isComplete()).isFalse();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdB11B").get().isComplete()).isFalse();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdC11A").get().isComplete()).isFalse();
        Assertions.assertThat(checkpointRepository.findCheckpointByPointId("pointIdC11B").get().isComplete()).isFalse();
    }

    @Test
    @DisplayName("Should Throw an ApiRequestException When Converting isComplete attribute in Non-Existent Checkpoint ")
    @WithMockUser(username = "testuser")
    public void throwExceptionConvertingIsCompleteToTrueInNonExistentCheckpoint() {
        //When-Then
        Assertions.assertThatThrownBy(() -> {
            checkpointService.completeCheckpoint("testuser", "XXXXX");
        }).isInstanceOf(ApiRequestException.class)
            .hasMessage("Checkpoint You Provided Doesn't Exist");
    }
}
