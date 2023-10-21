// package com.academicdashboard.backend.checklist;
//
// import java.util.ArrayList;
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
// @WebMvcTest(controllers = GrouplistController.class)
// @AutoConfigureMockMvc(addFilters = false)
// public class GrouplistControllerTest {
//
//     @Autowired
//     private MockMvc mockMvc;
//
//     @Autowired
//     private WebApplicationContext webApplicationContext;
//
//     @MockBean
//     private GrouplistService grouplistService;
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
//     @DisplayName("Should Return Newly Created Grouplist When Making a Post Request to endpoint - /api/stud/grouplist/{username}/new")
//     public void shouldCreateNewGrouplist() throws Exception {
//         Grouplist grouplist = Grouplist.builder()
//             .groupId("id001")
//             .title("Grouplist Title")
//             .checklists(new ArrayList<>())
//             .build();
//
//         Mockito.when(grouplistService.createGrouplist("testuser", "Grouplist Title"))
//             .thenReturn(grouplist);
//
//         mockMvc.perform(MockMvcRequestBuilders.post("/api/stud/grouplist/testuser/new")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content("{\"title\": \"Grouplist Title\"}"))
//             .andExpect(MockMvcResultMatchers.status().is(201))
//             .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.groupId", Matchers.is(grouplist.getGroupId())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is(grouplist.getTitle())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.checklists", Matchers.is(grouplist.getChecklists())));
//     }
//
//     @Test
//     @WithMockUser(username = "testuser", roles = {"STUDENT"})
//     @DisplayName("Should Return Modified Grouplist When Making a Put Request to endpoint - /api/stud/grouplist/{username}/modify/{groupId}")
//     public void shouldModifyGrouplist() throws Exception {
//         Grouplist grouplist = Grouplist.builder()
//             .groupId("id001")
//             .title("New Grouplist Title")
//             .checklists(new ArrayList<>())
//             .build();
//
//         Mockito.when(grouplistService.modifyGrouplist("testuser", "id001", "New Grouplist Title"))
//             .thenReturn(grouplist);
//
//         mockMvc.perform(MockMvcRequestBuilders.put("/api/stud/grouplist/testuser/modify/id001")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content("{\"title\": \"New Grouplist Title\"}"))
//             .andExpect(MockMvcResultMatchers.status().is(200))
//             .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.groupId", Matchers.is(grouplist.getGroupId())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is(grouplist.getTitle())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.checklists", Matchers.is(grouplist.getChecklists())));
//     }
//
//     @Test
//     @WithMockUser(username = "testuser", roles = {"STUDENT"})
//     @DisplayName("Should Return Grouplist With Added New Checklist When Making a Put Request to endpoint - /api/stud/grouplist/{username}/addnew/{groupId}")
//     public void shouldAddNewChecklistToGrouplist() throws Exception {
//         Grouplist grouplist = Grouplist.builder()
//             .groupId("id001")
//             .title("Grouplist Title")
//             .checklists(new ArrayList<>())
//             .build();
//
//         Mockito.when(grouplistService.addNewToGrouplist("testuser", "id001", "Checklist Title"))
//             .thenReturn(grouplist);
//
//         mockMvc.perform(MockMvcRequestBuilders.put("/api/stud/grouplist/testuser/addnew/id001")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content("{\"title\": \"Checklist Title\"}"))
//             .andExpect(MockMvcResultMatchers.status().is(200))
//             .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.groupId", Matchers.is(grouplist.getGroupId())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is(grouplist.getTitle())));
//     }
//
//     @Test
//     @WithMockUser(username = "testuser", roles = {"STUDENT"})
//     @DisplayName("Should Return Grouplist With Added Checklist When Making a Put Request to endpoint - /api/stud/grouplist/{username}/addexist")
//     public void shouldAddChecklistToGrouplist() throws Exception {
//         Grouplist grouplist = Grouplist.builder()
//             .groupId("id001")
//             .title("Grouplist Title")
//             .checklists(new ArrayList<>())
//             .build();
//
//         Mockito.when(grouplistService.addExistToGrouplist("testuser", "id001", "listId01"))
//             .thenReturn(grouplist);
//
//         mockMvc.perform(MockMvcRequestBuilders.put("/api/stud/grouplist/testuser/addexist")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content("{\"groupId\": \"id001\", \"listId\": \"listId01\"}"))
//             .andExpect(MockMvcResultMatchers.status().is(200))
//             .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.groupId", Matchers.is(grouplist.getGroupId())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is(grouplist.getTitle())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.checklists", Matchers.is(grouplist.getChecklists())));
//     }
//
//     @Test
//     @WithMockUser(username = "testuser", roles = {"STUDENT"})
//     @DisplayName("Should Return Grouplist Whose Checklist Got Removed When Making a Put Request to endpoint - /api/stud/grouplist/{username}/removefrom")
//     public void shouldremoveChecklistFromGrouplist() throws Exception {
//         Grouplist grouplist = Grouplist.builder()
//             .groupId("id001")
//             .title("Grouplist Title")
//             .checklists(new ArrayList<>())
//             .build();
//
//         Mockito.when(grouplistService.removefromGrouplist("testuser", "id001", "listId01"))
//             .thenReturn(grouplist);
//
//         mockMvc.perform(MockMvcRequestBuilders.put("/api/stud/grouplist/testuser/removefrom")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content("{\"groupId\": \"id001\", \"listId\": \"listId01\"}"))
//             .andExpect(MockMvcResultMatchers.status().is(200))
//             .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.groupId", Matchers.is(grouplist.getGroupId())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is(grouplist.getTitle())))
//             .andExpect(MockMvcResultMatchers.jsonPath("$.checklists", Matchers.is(grouplist.getChecklists())));
//     }
//
//     @Test
//     @WithMockUser(username = "testuser", roles = {"STUDENT"})
//     @DisplayName("Should Return Only 204 Status Code When Making a Delete Request to endpoint - /api/stud/grouplist/{username}/delete")
//     public void shouldDeleteGrouplist() throws Exception {
//         mockMvc.perform(MockMvcRequestBuilders.delete("/api/stud/grouplist/testuser/delete")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content("{\"groupId\": \"id001\", \"deleteAll\": false}"))
//             .andExpect(MockMvcResultMatchers.status().is(204));
//     }
// }
