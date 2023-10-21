// package com.academicdashboard.backend.checklist;
//
// import org.assertj.core.api.Assertions;
// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
// import org.testcontainers.junit.jupiter.Testcontainers;
//
// import com.academicdashboard.backend.user.UserRepository;
//
//
// @Testcontainers
// @DataMongoTest
// public class CheckpointRepositoryTest {
//
//     @Autowired
//     private UserRepository userRepository;
//     @Autowired
//     private GrouplistRepository grouplistRepository;
//     @Autowired
//     private ChecklistRepository checklistRepository;
//     @Autowired
//     private CheckpointRepository checkpointRepository;
//
//     private TestData testData;
//
//     @BeforeEach
//     public void setUp() {
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
//     @DisplayName("Should Find a Checkpoint Document Using it's pointId")
//     public void shouldFindCheckpointByPointId() {
//         //When
//         //Checkpoints
//         Checkpoint returnedValue01 = checkpointRepository.findCheckpointByPointId("pointIdA11").get();
//         Checkpoint returnedValue02 = checkpointRepository.findCheckpointByPointId("pointIdA12").get();
//         Checkpoint returnedValue03 = checkpointRepository.findCheckpointByPointId("pointIdA21").get();
//         Checkpoint returnedValue04 = checkpointRepository.findCheckpointByPointId("pointIdA22").get();
//         Checkpoint returnedValue05 = checkpointRepository.findCheckpointByPointId("pointIdB11").get();
//         Checkpoint returnedValue06 = checkpointRepository.findCheckpointByPointId("pointIdB12").get();
//         Checkpoint returnedValue07 = checkpointRepository.findCheckpointByPointId("pointIdB21").get();
//         Checkpoint returnedValue08 = checkpointRepository.findCheckpointByPointId("pointIdB22").get();
//         Checkpoint returnedValue09 = checkpointRepository.findCheckpointByPointId("pointIdC11").get();
//         Checkpoint returnedValue10 = checkpointRepository.findCheckpointByPointId("pointIdC12").get();
//         Checkpoint returnedValue11 = checkpointRepository.findCheckpointByPointId("pointIdD1").get();
//         Checkpoint returnedValue12 = checkpointRepository.findCheckpointByPointId("pointIdD2").get();
//         //Subcheckpoints
//         Checkpoint returnedValue13 = checkpointRepository.findCheckpointByPointId("pointIdA11A").get();
//         Checkpoint returnedValue14 = checkpointRepository.findCheckpointByPointId("pointIdA11B").get();
//         Checkpoint returnedValue15 = checkpointRepository.findCheckpointByPointId("pointIdB11A").get();
//         Checkpoint returnedValue16 = checkpointRepository.findCheckpointByPointId("pointIdB11B").get();
//         Checkpoint returnedValue17 = checkpointRepository.findCheckpointByPointId("pointIdC11A").get();
//         Checkpoint returnedValue18 = checkpointRepository.findCheckpointByPointId("pointIdC11B").get();
//
//         //Then
//         //Checkpoints
//         Assertions.assertThat(returnedValue01.getContent()).isEqualTo("ContentA11");
//         Assertions.assertThat(returnedValue02.getContent()).isEqualTo("ContentA12");
//         Assertions.assertThat(returnedValue03.getContent()).isEqualTo("ContentA21");
//         Assertions.assertThat(returnedValue04.getContent()).isEqualTo("ContentA22");
//         Assertions.assertThat(returnedValue05.getContent()).isEqualTo("ContentB11");
//         Assertions.assertThat(returnedValue06.getContent()).isEqualTo("ContentB12");
//         Assertions.assertThat(returnedValue07.getContent()).isEqualTo("ContentB21");
//         Assertions.assertThat(returnedValue08.getContent()).isEqualTo("ContentB22");
//         Assertions.assertThat(returnedValue09.getContent()).isEqualTo("ContentC11");
//         Assertions.assertThat(returnedValue10.getContent()).isEqualTo("ContentC12");
//         Assertions.assertThat(returnedValue11.getContent()).isEqualTo("ContentD1");
//         Assertions.assertThat(returnedValue12.getContent()).isEqualTo("ContentD2");
//         //Subcheckpoints
//         Assertions.assertThat(returnedValue13.getContent()).isEqualTo("ContentA11A");
//         Assertions.assertThat(returnedValue14.getContent()).isEqualTo("ContentA11B");
//         Assertions.assertThat(returnedValue15.getContent()).isEqualTo("ContentB11A");
//         Assertions.assertThat(returnedValue16.getContent()).isEqualTo("ContentB11B");
//         Assertions.assertThat(returnedValue17.getContent()).isEqualTo("ContentC11A");
//         Assertions.assertThat(returnedValue18.getContent()).isEqualTo("ContentC11B");
//     }
// }
