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
    @DisplayName("Should Return Newly Created Checklist When Making POST request to endpoints - /v1.0/users/{username}/checklists")
    public void shouldCreateNewChecklist() throws Exception {
        //Mocked Response
        Checklist response = Checklist.builder()
            .username("testuser")
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

        mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/users/testuser/checklists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqBodyAsByte))
            .andExpect(MockMvcResultMatchers.status().is(201))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is("testuser")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.listId", Matchers.is("0123456789")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is("Checklist Title")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.groupId", Matchers.is("")));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    @DisplayName("Should Return Modified Checklist When Making PATCH request to endpoints - /v1.0/checklists/{listId}")
    public void shouldModifyChecklistTitle() throws Exception {
        Checklist response = Checklist.builder()
            .username("username")
            .listId("0123456789")
            .title("New Checklist Title")
            .groupId("")
            .checkpoints(new ArrayList<>())
            .completedPoints(new ArrayList<>())
            .build();

        Mockito.when(checklistService.editTitle("0123456789", "New Checklist Title"))
            .thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.patch("/v1.0/checklists/0123456789")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"New Checklist Title\"}"))
            .andExpect(MockMvcResultMatchers.status().is(200))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is(response.getUsername())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.listId", Matchers.is(response.getListId())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is(response.getTitle())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.groupId", Matchers.is(response.getGroupId())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints", Matchers.is(response.getCheckpoints())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.completedPoints", Matchers.is(response.getCompletedPoints())));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    @DisplayName("Should Return Checklist with Added Checkpoint When Makinga PATCH request to endpoints - /v1.0/checklists/{listId}/checkpoints")
    public void shouldAddCheckpointsToChecklist() throws Exception {

        /* ********** Checkpoint A11 ********** */
        Checkpoint subpointA11A = Checkpoint.builder()
                .index("0")
                .content("ContentA11A")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();
        Checkpoint subpointA11B = Checkpoint.builder()
                .index("1")
                .content("ContentA11B")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();

        List<Checkpoint> A11Subpoints = new ArrayList<>();
        A11Subpoints.add(subpointA11A);
        A11Subpoints.add(subpointA11B);

        Checkpoint subpointA11C = Checkpoint.builder()
                .index("0")
                .content("ContentA11C")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();
        Checkpoint subpointA11D = Checkpoint.builder()
                .index("1")
                .content("ContentA11D")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();

        List<Checkpoint> A11CompletedSubpoints = new ArrayList<>();
        A11CompletedSubpoints.add(subpointA11C);
        A11CompletedSubpoints.add(subpointA11D);

        Checkpoint pointA11 = Checkpoint.builder()
                .index("0")
                .content("ContentA11")
                .subpoints(A11Subpoints)
                .completedSubpoints(A11CompletedSubpoints)
                .build();

        /* ********** Checkpoint A12 ********** */
        Checkpoint pointA12 = Checkpoint.builder()
                .index("1")
                .content("ContentA12")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();

        /* ********** Checkpoint A13 ********** */
        Checkpoint pointA13 = Checkpoint.builder()
                .index("0")
                .content("ContentA13")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();

        /* ********** Checkpoint A14 ********** */
        Checkpoint subpointA14A = Checkpoint.builder()
                .index("0")
                .content("ContentA14A")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();

        Checkpoint subpointA14B = Checkpoint.builder()
                .index("1")
                .content("ContentA14B")
                .subpoints(new ArrayList<>())
                .completedSubpoints(new ArrayList<>())
                .build();

        List<Checkpoint> A14CompletedSubpoints = new ArrayList<>();
        A14CompletedSubpoints.add(subpointA14A);
        A14CompletedSubpoints.add(subpointA14B);

        Checkpoint pointA14 = Checkpoint.builder()
                .index("1")
                .content("ContentA14")
                .subpoints(new ArrayList<>())
                .completedSubpoints(A14CompletedSubpoints)
                .build();

        /**************************************/
        /* ********** Checklist A1 ********** */
        /**************************************/
        List<Checkpoint> A1Checkpoints = new ArrayList<>();
        A1Checkpoints.add(pointA11);
        A1Checkpoints.add(pointA12);

        List<Checkpoint> A1CompletedPoints = new ArrayList<>();
        A1CompletedPoints.add(pointA13);
        A1CompletedPoints.add(pointA14);

        Checklist checklistA1 = Checklist.builder()
                    .username("testuser")
                    .listId("listIdA1")
                    .title("Checklist Title A1")
                    .groupId("groupIdA")
                    .checkpoints(A1Checkpoints)
                    .completedPoints(A1CompletedPoints)
                    .build();

        //Request Body
        record ReqBody (List<Checkpoint> checkpoints, List<Checkpoint> completedPoints) {}
        ReqBody reqBody = new ReqBody(A1Checkpoints, A1CompletedPoints);

        //Convert Request Body to Byte[]
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] reqBodyAsByte = objectMapper.writeValueAsBytes(reqBody);

        Mockito.when(checklistService.editCheckpoints("listIdA1", A1Checkpoints, A1CompletedPoints))
            .thenReturn(checklistA1);

        mockMvc.perform(MockMvcRequestBuilders.patch("/v1.0/checklists/listIdA1/checkpoints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqBodyAsByte))
            .andExpect(MockMvcResultMatchers.status().is(200))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is("testuser")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.listId", Matchers.is("listIdA1")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is("Checklist Title A1")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.groupId", Matchers.is("groupIdA")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints[0].index", Matchers.is("0")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints[0].content", Matchers.is("ContentA11")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints[0].subpoints[0].index", Matchers.is("0")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints[0].subpoints[0].content", Matchers.is("ContentA11A")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints[0].subpoints[1].index", Matchers.is("1")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints[0].subpoints[1].content", Matchers.is("ContentA11B")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints[0].completedSubpoints[0].index", Matchers.is("0")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints[0].completedSubpoints[0].content", Matchers.is("ContentA11C")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints[0].completedSubpoints[1].index", Matchers.is("1")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints[0].completedSubpoints[1].content", Matchers.is("ContentA11D")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints[1].index", Matchers.is("1")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checkpoints[1].content", Matchers.is("ContentA12")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.completedPoints[0].index", Matchers.is("0")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.completedPoints[0].content", Matchers.is("ContentA13")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.completedPoints[1].index", Matchers.is("1")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.completedPoints[1].content", Matchers.is("ContentA14")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.completedPoints[1].completedSubpoints[0].index", Matchers.is("0")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.completedPoints[1].completedSubpoints[0].content", Matchers.is("ContentA14A")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.completedPoints[1].completedSubpoints[1].index", Matchers.is("1")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.completedPoints[1].completedSubpoints[1].content", Matchers.is("ContentA14B")));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    @DisplayName("Should Return Checklist with New Grouplist When Making PATCH Request to endpoitns - /v1.0/checklists/{listId}/grouplists")
    public void shouldEditChecklistsGrouplist() throws Exception {
        Checklist checklist = Checklist.builder()
            .username("testuser")
            .listId("listId01")
            .title("Checklist Title 01")
            .groupId("groupIdA")
            .checkpoints(new ArrayList<>())
            .completedPoints(new ArrayList<>())
            .build();

        Mockito.when(checklistService.editGrouplist("listId01", "groupIdA"))
            .thenReturn(checklist);

        //Request Body
        record ReqBody (String groupId) {}
        ReqBody reqBody = new ReqBody("groupIdA");

        //Convert Request Body to Byte[]
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] reqBodyAsByte = objectMapper.writeValueAsBytes(reqBody);

        mockMvc.perform(MockMvcRequestBuilders.patch("/v1.0/checklists/listId01/grouplists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqBodyAsByte))
            .andExpect(MockMvcResultMatchers.status().is(200))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is("testuser")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.listId", Matchers.is("listId01")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is("Checklist Title 01")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.groupId", Matchers.is("groupIdA")));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    @DisplayName("Should Only Return 204 Status Code When Making DELETE request to endpoints - /v1.0/checklists/{listId}")
    public void shouldDeleteExistingChecklist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1.0/checklists/listId01"))
            .andExpect(MockMvcResultMatchers.status().is(204));
    }
}
