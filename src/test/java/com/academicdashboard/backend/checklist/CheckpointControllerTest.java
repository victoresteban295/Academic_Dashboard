// package com.academicdashboard.backend.checklist;
//
// import java.util.ArrayList;
// import java.util.List;
//
// import org.hamcrest.Matchers;
// import org.junit.Before;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
// import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;
// import org.springframework.web.context.WebApplicationContext;
//
// import com.academicdashboard.backend.config.JwtAuthenticationFilter;
//
//
// @WebMvcTest(controllers = CheckpointController.class)
// @AutoConfigureMockMvc(addFilters = false)
// public class CheckpointControllerTest {
//
//     @Autowired
//     private MockMvc mockMvc;
//
//     @Autowired
//     private WebApplicationContext webApplicationContext;
//
//     @MockBean
//     private CheckpointService checkpointService;
//
//     @MockBean
//     private JwtAuthenticationFilter jwtAuthenticationFilter;
//
//     @Before
//     public void setUp() {
//         mockMvc = MockMvcBuilders
//             .webAppContextSetup(webApplicationContext)
//             .addFilter(jwtAuthenticationFilter)
//             .build();
//     }
//
//     @Test
//     @WithMockUser(username = "testuser", roles = {"STUDENT"})
//     @DisplayName("Should Return Checklist With Added Checkpoint When Making a Post Request to endpoint - /api/stud/checkpoint/{username}/new/{listId}")
//     public void shouldAddNewCheckpointUnderChecklist() throws Exception {
//         Checklist checklist = Checklist.builder()
//             .listId("listId01")
//             .title("Checklist Title01")
//             .checkpoints(new ArrayList<>())
//             .build();
//         Checkpoint checkpoint = Checkpoint.builder()
//             .pointId("pointId01")
//             .content("Checkpoint Content")
//             .isComplete(false)
//             .isSubpoint(false)
//             .subCheckpoints(new ArrayList<>())
//             .build();
//         List<Checkpoint> checkpoints = new ArrayList<>();
//         checkpoints.add(checkpoint);
//         checklist.setCheckpoints(checkpoints);
//
//         Mockito.when(checkpointService.addCheckpoint("testuser", "listId01", "Checkpoint Content"))
//             .thenReturn(checklist);
//
//         mockMvc.perform(MockMvcRequestBuilders.post("/api/stud/checkpoint/testuser/new/listId01")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content("{\"content\": \"Checkpoint Content\"}"))
//             .andExpect(MockMvcResultMatchers.status().is(201))
//             .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.listId", Matchers.is(checklist.getListId())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is(checklist.getTitle())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints", Matchers.hasSize(1)))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints[0].pointId", Matchers.is(checkpoint.getPointId())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints[0].content", Matchers.is(checkpoint.getContent())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints[0].isComplete", Matchers.is(checkpoint.isComplete())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints[0].isSubpoint", Matchers.is(checkpoint.isSubpoint())));
//     }
//
//     @Test
//     @WithMockUser(username = "testuser", roles = {"STUDENT"})
//     @DisplayName("Should Return the Modified Checkpoint When Making a Put Request to endpoint - /api/stud/checkpoint/{username}/modify/{pointId}")
//     public void shouldModifyCheckpoint() throws Exception {
//         Checkpoint checkpoint = Checkpoint.builder()
//             .pointId("pointId01")
//             .content("Modified Checkpoint Content")
//             .isComplete(false)
//             .isSubpoint(false)
//             .subCheckpoints(new ArrayList<>())
//             .build();
//
//         Mockito.when(checkpointService.modifyCheckpoint("testuser", "pointId01", "Modified Checkpoint Content"))
//             .thenReturn(checkpoint);
//
//         mockMvc.perform(MockMvcRequestBuilders.put("/api/stud/checkpoint/testuser/modify/pointId01")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content("{\"content\": \"Modified Checkpoint Content\"}"))
//             .andExpect(MockMvcResultMatchers.status().is(200))
//             .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.pointId", Matchers.is(checkpoint.getPointId())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.is(checkpoint.getContent())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.isComplete", Matchers.is(checkpoint.isComplete())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.isSubpoint", Matchers.is(checkpoint.isSubpoint())));
//     }
//
//     @Test
//     @WithMockUser(username = "testuser", roles = {"STUDENT"})
//     @DisplayName("Should Return Only 204 Status Code When Making a Delete Request to endpoint - /api/stud/checkpoint/{username}/delete/{pointId}")
//     public void shouldDeleteCheckpoint() throws Exception {
//         mockMvc.perform(MockMvcRequestBuilders.delete("/api/stud/checkpoint/testuser/delete/pointId01"))
//             .andExpect(MockMvcResultMatchers.status().is(204));
//     }
//
//     @Test
//     @WithMockUser(username = "testuser", roles = {"STUDENT"})
//     @DisplayName("Should Return Checkpoint with Added Subcheckpoint When Making a Put Request to endpoint - /api/stud/checkpoint/{username}/make/subpoint/{listId}")
//     public void turnCheckpointToSubcheckpoint() throws Exception {
//         Checkpoint checkpoint = Checkpoint.builder()
//             .pointId("pointId01")
//             .content("Parent Content")
//             .isComplete(false)
//             .isSubpoint(false)
//             .subCheckpoints(new ArrayList<>())
//             .build();
//         Checkpoint subpoint = Checkpoint.builder()
//             .pointId("pointId02")
//             .content("Subpoint Content")
//             .isComplete(false)
//             .isSubpoint(true)
//             .subCheckpoints(new ArrayList<>())
//             .build();
//         List<Checkpoint> subpoints = new ArrayList<>();
//         subpoints.add(subpoint);
//         checkpoint.setSubCheckpoints(subpoints);
//
//         Mockito.when(checkpointService.turnIntoSubcheckpoint("testuser", "listId01", "pointId01", "pointId02"))
//            .thenReturn(checkpoint);
//
//         mockMvc.perform(MockMvcRequestBuilders.put("/api/stud/checkpoint/testuser/make/subpoint/listId01")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content("{\"pointId\": \"pointId01\", \"subpointId\": \"pointId02\"}"))
//             .andExpect(MockMvcResultMatchers.status().is(200))
//             .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.pointId", Matchers.is(checkpoint.getPointId())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.is(checkpoint.getContent())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.isComplete", Matchers.is(checkpoint.isComplete())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.isSubpoint", Matchers.is(checkpoint.isSubpoint())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.subCheckpoints", Matchers.hasSize(1)))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.subCheckpoints[0].pointId", Matchers.is(subpoint.getPointId())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.subCheckpoints[0].content", Matchers.is(subpoint.getContent())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.subCheckpoints[0].isComplete", Matchers.is(subpoint.isComplete())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.subCheckpoints[0].isSubpoint", Matchers.is(subpoint.isSubpoint())));
//     }
//
//     @Test
//     @WithMockUser(username = "testuser", roles = {"STUDENT"})
//     @DisplayName("Should Return Checkpoint with Newly Added Subcheckpoint When Making a Put Request to endpoint - /api/stud/checkpoint/{username}/new/subpoint/{pointId}")
//     public void shouldCreateNewSubCheckpointUnderCheckpoint() throws Exception {
//         Checkpoint checkpoint = Checkpoint.builder()
//             .pointId("pointId01")
//             .content("Parent Content")
//             .isComplete(false)
//             .isSubpoint(false)
//             .subCheckpoints(new ArrayList<>())
//             .build();
//         Checkpoint subpoint = Checkpoint.builder()
//             .pointId("pointId02")
//             .content("New Subpoint Content")
//             .isComplete(false)
//             .isSubpoint(true)
//             .subCheckpoints(new ArrayList<>())
//             .build();
//         List<Checkpoint> subpoints = new ArrayList<>();
//         subpoints.add(subpoint);
//         checkpoint.setSubCheckpoints(subpoints);
//
//         Mockito.when(checkpointService.newSubcheckpoint("testuser", "pointId01", "New Subpoint Content"))
//            .thenReturn(checkpoint);
//
//         mockMvc.perform(MockMvcRequestBuilders.put("/api/stud/checkpoint/testuser/new/subpoint/pointId01")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content("{\"content\": \"New Subpoint Content\"}"))
//             .andExpect(MockMvcResultMatchers.status().is(200))
//             .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.pointId", Matchers.is(checkpoint.getPointId())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.is(checkpoint.getContent())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.isComplete", Matchers.is(checkpoint.isComplete())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.isSubpoint", Matchers.is(checkpoint.isSubpoint())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.subCheckpoints", Matchers.hasSize(1)))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.subCheckpoints[0].pointId", Matchers.is(subpoint.getPointId())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.subCheckpoints[0].content", Matchers.is(subpoint.getContent())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.subCheckpoints[0].isComplete", Matchers.is(subpoint.isComplete())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.subCheckpoints[0].isSubpoint", Matchers.is(subpoint.isSubpoint())));
//     }
//
//     @Test
//     @WithMockUser(username = "testuser", roles = {"STUDENT"})
//     @DisplayName("Should Return Checklist with Checkpoints When Making a Put Request to endpoint - /api/stud/checkpoint/{username}/reverse/subpoint/{listId}")
//     public void shouldReverseSubCheckpointToCheckpoint() throws Exception {
//         Checklist checklist = Checklist.builder()
//             .listId("listId01")
//             .title("Checklist Title")
//             .checkpoints(new ArrayList<>())
//             .build();
//         Checkpoint checkpoint01 = Checkpoint.builder()
//             .pointId("pointId01")
//             .content("Checkpoint Content")
//             .isComplete(false)
//             .isSubpoint(false)
//             .subCheckpoints(new ArrayList<>())
//             .build();
//         Checkpoint checkpoint02 = Checkpoint.builder()
//             .pointId("pointId02")
//             .content("SubCheckpoint to Checkpoint")
//             .isComplete(false)
//             .isSubpoint(true)
//             .subCheckpoints(new ArrayList<>())
//             .build();
//         List<Checkpoint> checkpoints = new ArrayList<>();
//         checkpoints.add(checkpoint01);
//         checkpoints.add(checkpoint02);
//         checklist.setCheckpoints(checkpoints);
//
//         Mockito.when(checkpointService.reverseSubcheckpoint("testuser", "listId01", "pointId01", "pointId02"))
//            .thenReturn(checklist);
//
//         mockMvc.perform(MockMvcRequestBuilders.put("/api/stud/checkpoint/testuser/reverse/subpoint/listId01")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content("{\"pointId\": \"pointId01\", \"subpointId\": \"pointId02\"}"))
//             .andExpect(MockMvcResultMatchers.status().is(200))
//             .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.listId", Matchers.is(checklist.getListId())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is(checklist.getTitle())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints", Matchers.hasSize(2)))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints[0].pointId", Matchers.is(checkpoint01.getPointId())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints[0].content", Matchers.is(checkpoint01.getContent())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints[0].isComplete", Matchers.is(checkpoint01.isComplete())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints[0].isSubpoint", Matchers.is(checkpoint01.isSubpoint())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints[0].subCheckpoints", Matchers.hasSize(0)))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints[1].pointId", Matchers.is(checkpoint02.getPointId())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints[1].content", Matchers.is(checkpoint02.getContent())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints[1].isComplete", Matchers.is(checkpoint02.isComplete())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints[1].isSubpoint", Matchers.is(checkpoint02.isSubpoint())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints[1].subCheckpoints", Matchers.hasSize(0)));
//     }
//
//     @Test
//     @WithMockUser(username = "testuser", roles = {"STUDENT"})
//     @DisplayName("Should Return Checkpoint with isComplete Equal To True When Making a Put Request to endpoint - /api/stud/checkpoint/{username}/complete/{pointId}")
//     public void shouldCompleteCheckpoint() throws Exception {
//         Checkpoint checkpoint = Checkpoint.builder()
//             .pointId("pointId01")
//             .content("Checkpoint Content")
//             .isComplete(true)
//             .isSubpoint(false)
//             .subCheckpoints(new ArrayList<>())
//             .build();
//
//         Mockito.when(checkpointService.completeCheckpoint("testuser", "pointId01"))
//            .thenReturn(checkpoint);
//
//         mockMvc.perform(MockMvcRequestBuilders.put("/api/stud/checkpoint/testuser/complete/pointId01"))
//             .andExpect(MockMvcResultMatchers.status().is(200))
//             .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.pointId", Matchers.is(checkpoint.getPointId())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.is(checkpoint.getContent())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.isComplete", Matchers.is(checkpoint.isComplete())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.isSubpoint", Matchers.is(checkpoint.isSubpoint())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.subCheckpoints", Matchers.hasSize(0)));
//     }
// }
