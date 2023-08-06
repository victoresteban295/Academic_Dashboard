package com.academicdashboard.backend.checklist;

import java.util.ArrayList;

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
    @DisplayName("Should Return Newly Created Checklist When Making POST request to endpoints - /api/stud/checklist/{username}/new")
    public void shouldCreateNewChecklist() throws Exception {
        Checklist checklist = Checklist.builder()
            .listId("12345")
            .title("Checklist Title")
            .checkpoints(new ArrayList<>())
            .build();

        Mockito.when(checklistService.createChecklist("testuser", "Checklist Title"))
            .thenReturn(checklist);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/stud/checklist/testuser/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Checklist Title\"}"))
            .andExpect(MockMvcResultMatchers.status().is(201))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.jsonPath("$.listId", Matchers.is(checklist.getListId())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is(checklist.getTitle())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints", Matchers.is(checklist.getCheckpoints())));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    @DisplayName("Should Return Modified Checklist When Making PUT request to endpoints - /api/stud/checklist/{username}/modify/{listId}")
    public void shouldModifyExistingChecklist() throws Exception {
        Checklist response = Checklist.builder()
            .listId("12345")
            .title("New Checklist Title")
            .checkpoints(new ArrayList<>())
            .build();

        Mockito.when(checklistService.modifyChecklist("testuser", "12345", "New Checklist Title"))
            .thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/stud/checklist/testuser/modify/12345")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"New Checklist Title\"}"))
            .andExpect(MockMvcResultMatchers.status().is(200))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.jsonPath("$.listId", Matchers.is(response.getListId())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is(response.getTitle())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints", Matchers.is(response.getCheckpoints())));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    @DisplayName("Should Only Return 204 Status Code When Making DELETE request to endpoints - /api/stud/checklist/{username}/delete/{listId}")
    public void shouldDeleteExistingChecklist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/stud/checklist/testuser/delete/12345"))
            .andExpect(MockMvcResultMatchers.status().is(204));
    }
}
