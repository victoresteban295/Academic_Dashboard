package com.academicdashboard.backend.checklist;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.academicdashboard.backend.config.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = ChecklistController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ChecklistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private ChecklistService checklistService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .addFilter(jwtAuthenticationFilter)
            .build();
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    @DisplayName("Should Return Newly Created Checklist When Making POST request to endpoints - /api/checklist/{username}/new")
    public void shouldCreateNewChecklist() throws Exception {
        //Mocked Response
        Checklist response = Checklist.builder()
            .listId("0123456789")
            .title("Checklist Title")
            .groupId("")
            .checkpoints(new ArrayList<>())
            .completedPoints(new ArrayList<>())
            .build();

        //Mock Service Class
        Mockito.when(checklistService.createChecklist("testuser", "Checklist Title", "0123456789"))
            .thenReturn(response);

        //Request Body
        record ReqBody (String title, String listId) {}
        ReqBody reqBody = new ReqBody("Checklist Title", "0123456789");

        //Convert Request Body to Byte[]
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] reqBodyAsByte = objectMapper.writeValueAsBytes(reqBody);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/checklist/testuser/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqBodyAsByte))
            .andExpect(MockMvcResultMatchers.status().is(201))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.jsonPath("$.listId", Matchers.is("0123456789")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is("Checklist Title")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.groupId", Matchers.is("")));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    @DisplayName("Should Return Reordered Checklist When Making PUT request to endpoints - /api/checklist/{username}/reorder")
    public void shouldReorderChecklists() throws Exception {
        //Mocked Response
        Checklist checklist01 = Checklist.builder()
            .listId("listId01")
            .title("Checklist Title 01")
            .groupId("")
            .checkpoints(new ArrayList<>())
            .completedPoints(new ArrayList<>())
            .build();
        Checklist checklist02 = Checklist.builder()
            .listId("listId02")
            .title("Checklist Title 02")
            .groupId("")
            .checkpoints(new ArrayList<>())
            .completedPoints(new ArrayList<>())
            .build();
        Checklist checklist03 = Checklist.builder()
            .listId("listId03")
            .title("Checklist Title 03")
            .groupId("")
            .checkpoints(new ArrayList<>())
            .completedPoints(new ArrayList<>())
            .build();
        List<Checklist> checklists = new ArrayList<>();
        checklists.add(checklist01);
        checklists.add(checklist02);
        checklists.add(checklist03);


        //Request Body
        record ReqBody (List<Checklist> checklists) {}
        ReqBody reqBody = new ReqBody(checklists);

        //Convert Request Body to Byte[]
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] reqBodyAsByte = objectMapper.writeValueAsBytes(reqBody);

        //Mock Service Class
        Mockito.when(checklistService.reorderChecklist("testuser", checklists))
            .thenReturn(checklists);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/checklist/testuser/reorder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqBodyAsByte))
            .andExpect(MockMvcResultMatchers.status().is(200))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].listId", Matchers.is("listId01")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].title", Matchers.is("Checklist Title 01")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].groupId", Matchers.is("")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].listId", Matchers.is("listId02")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].title", Matchers.is("Checklist Title 02")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].groupId", Matchers.is("")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[2].listId", Matchers.is("listId03")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[2].title", Matchers.is("Checklist Title 03")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[2].groupId", Matchers.is("")));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    @DisplayName("Should Return Modified Checklist When Making PUT request to endpoints - /api/stud/checklist/{username}/modify/{listId}")
    public void shouldModifyChecklistTitle() throws Exception {
        Checklist response = Checklist.builder()
            .listId("0123456789")
            .title("New Checklist Title")
            .groupId("")
            .checkpoints(new ArrayList<>())
            .completedPoints(new ArrayList<>())
            .build();

        Mockito.when(checklistService.modifyTitle("testuser", "0123456789", "New Checklist Title"))
            .thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/checklist/testuser/modify/title/0123456789")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"New Checklist Title\"}"))
            .andExpect(MockMvcResultMatchers.status().is(200))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.jsonPath("$.listId", Matchers.is(response.getListId())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is(response.getTitle())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.groupId", Matchers.is(response.getGroupId())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints", Matchers.is(response.getCheckpoints())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.completedPoints", Matchers.is(response.getCompletedPoints())));
    }
//
//     @Test
//     @WithMockUser(username = "testuser", roles = {"STUDENT"})
//     @DisplayName("Should Only Return 204 Status Code When Making DELETE request to endpoints - /api/stud/checklist/{username}/delete/{listId}")
//     public void shouldDeleteExistingChecklist() throws Exception {
//         mockMvc.perform(MockMvcRequestBuilders.delete("/api/stud/checklist/testuser/delete/12345"))
//             .andExpect(MockMvcResultMatchers.status().is(204));
//     }
}
