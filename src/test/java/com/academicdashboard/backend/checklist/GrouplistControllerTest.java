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

@WebMvcTest(controllers = GrouplistController.class)
@AutoConfigureMockMvc(addFilters = false)
public class GrouplistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private GrouplistService grouplistService;

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
    @DisplayName("Should Return Newly Created Grouplist When Making a Post Request to endpoint - /api/grouplist/{username}/new")
    public void shouldCreateNewGrouplist() throws Exception {
        //Mocked Response
        Grouplist grouplist = Grouplist.builder()
            .groupId("groupId01")
            .title("Grouplist Title 01")
            .checklists(new ArrayList<>())
            .build();

        //Mock Service class
        Mockito.when(grouplistService.createGrouplist("testuser", "Grouplist Title 01", "groupId01"))
            .thenReturn(grouplist);

        //Request Body 
        record ReqBody (String title, String groupId) {}
        ReqBody reqBody = new ReqBody("Grouplist Title 01", "groupId01");

        //Convert Request Body to Byte[]
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] reqBodyAsByte = objectMapper.writeValueAsBytes(reqBody);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/grouplist/testuser/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqBodyAsByte))
            .andExpect(MockMvcResultMatchers.status().is(201))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.jsonPath("$.groupId", Matchers.is("groupId01")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is("Grouplist Title 01")));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    @DisplayName("Should Return Modified Grouplist When Making a Put Request to endpoint - /api/grouplist/{username}/modify/title/{groupId}")
    public void shouldModifyGrouplistTitle() throws Exception {
        //Mocked Response
        Grouplist grouplist = Grouplist.builder()
            .groupId("groupId01")
            .title("New Grouplist Title")
            .checklists(new ArrayList<>())
            .build();

        //Mocked Service Class
        Mockito.when(grouplistService.modifyTitle("testuser", "groupId01", "New Grouplist Title"))
            .thenReturn(grouplist);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/grouplist/testuser/modify/title/groupId01")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"New Grouplist Title\"}"))
            .andExpect(MockMvcResultMatchers.status().is(200))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.jsonPath("$.groupId", Matchers.is("groupId01")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is("New Grouplist Title")));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    @DisplayName("Should Return List of Reordered Grouplists When Making a PUT Request to endpoint - /api/grouplist/{username}/reorder")
    public void shouldReorderUsersGrouplists() throws Exception {
        //Mocked Response
        Grouplist grouplist01 = Grouplist.builder()
            .groupId("groupId01")
            .title("Grouplist Title 01")
            .checklists(new ArrayList<>())
            .build();
        Grouplist grouplist02 = Grouplist.builder()
            .groupId("groupId02")
            .title("Grouplist Title 02")
            .checklists(new ArrayList<>())
            .build();
        Grouplist grouplist03 = Grouplist.builder()
            .groupId("groupId03")
            .title("Grouplist Title 03")
            .checklists(new ArrayList<>())
            .build();
        List<Grouplist> grouplists = new ArrayList<>();
        grouplists.add(grouplist01);
        grouplists.add(grouplist02);
        grouplists.add(grouplist03);

        //Mock Service class
        Mockito.when(grouplistService.reorderGrouplists("testuser", grouplists))
            .thenReturn(grouplists);

        //Request Body 
        record ReqBody (List<Grouplist> grouplists) {}
        ReqBody reqBody = new ReqBody(grouplists);

        //Convert Request Body to Byte[]
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] reqBodyAsByte = objectMapper.writeValueAsBytes(reqBody);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/grouplist/testuser/reorder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqBodyAsByte))
            .andExpect(MockMvcResultMatchers.status().is(200))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].groupId", Matchers.is("groupId01")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].title", Matchers.is("Grouplist Title 01")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].groupId", Matchers.is("groupId02")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].title", Matchers.is("Grouplist Title 02")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[2].groupId", Matchers.is("groupId03")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[2].title", Matchers.is("Grouplist Title 03")));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    @DisplayName("Should Return Grouplist with Reordered Checklists When Making a PUT Request to endpoint - /api/grouplist/{username}/reorder/checklists")
    public void shouldReorderGrouplistChecklists() throws Exception {
        //Mocked Response
        Checklist checklist01 = Checklist.builder()
            .listId("listId01")
            .title("Checklist Title 01")
            .groupId("groupId01")
            .build();
        Checklist checklist02 = Checklist.builder()
            .listId("listId02")
            .title("Checklist Title 02")
            .groupId("groupId01")
            .build();
        Checklist checklist03 = Checklist.builder()
            .listId("listId03")
            .title("Checklist Title 03")
            .groupId("groupId01")
            .build();
        List<Checklist> checklists = new ArrayList<>();
        checklists.add(checklist01);
        checklists.add(checklist02);
        checklists.add(checklist03);
        Grouplist grouplist = Grouplist.builder()
            .groupId("groupId01")
            .title("Grouplist Title 01")
            .checklists(checklists)
            .build();

        //Mock Service class
        Mockito.when(grouplistService.reorderGroupChecklists("testuser", grouplist))
            .thenReturn(grouplist);

        //Request Body 
        record ReqBody (Grouplist grouplist) {}
        ReqBody reqBody = new ReqBody(grouplist);

        //Convert Request Body to Byte[]
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] reqBodyAsByte = objectMapper.writeValueAsBytes(reqBody);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/grouplist/testuser/reorder/checklists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqBodyAsByte))
            .andExpect(MockMvcResultMatchers.status().is(200))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.jsonPath("$.groupId", Matchers.is("groupId01")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is("Grouplist Title 01")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checklists[0].listId", Matchers.is("listId01")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checklists[0].title", Matchers.is("Checklist Title 01")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checklists[0].groupId", Matchers.is("groupId01")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checklists[1].listId", Matchers.is("listId02")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checklists[1].title", Matchers.is("Checklist Title 02")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checklists[1].groupId", Matchers.is("groupId01")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checklists[2].listId", Matchers.is("listId03")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checklists[2].title", Matchers.is("Checklist Title 03")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checklists[2].groupId", Matchers.is("groupId01")));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    @DisplayName("Should Return Grouplist With New Checklist When Making a Put Request to endpoint - /api/grouplist/{username}/new/checklist/{groupId}")
    public void shouldCreateNewChecklistUnderGrouplist() throws Exception {
        //Mocked Response
        Checklist checklist01 = Checklist.builder()
            .listId("listId01")
            .title("Checklist Title 01")
            .groupId("groupId01")
            .build();
        Checklist checklist02 = Checklist.builder()
            .listId("listId02")
            .title("New Checklist Title 02")
            .groupId("groupId01")
            .build();
        List<Checklist> checklists = new ArrayList<>();
        checklists.add(checklist01);
        checklists.add(checklist02);
        Grouplist grouplist = Grouplist.builder()
            .groupId("groupId01")
            .title("Grouplist Title 01")
            .checklists(checklists)
            .build();

        //Request Body 
        record ReqBody (String listId, String title) {}
        ReqBody reqBody = new ReqBody("listId02", "New Checklist Title 02");

        //Convert Request Body to Byte[]
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] reqBodyAsByte = objectMapper.writeValueAsBytes(reqBody);

        //Mock Service Class
        Mockito.when(grouplistService.createChecklist("testuser", "groupId01", "listId02", "New Checklist Title 02"))
            .thenReturn(grouplist);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/grouplist/testuser/new/checklist/groupId01")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqBodyAsByte))
            .andExpect(MockMvcResultMatchers.status().is(200))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.jsonPath("$.groupId", Matchers.is("groupId01")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is("Grouplist Title 01")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checklists[0].listId", Matchers.is("listId01")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checklists[0].title", Matchers.is("Checklist Title 01")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checklists[0].groupId", Matchers.is("groupId01")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checklists[1].listId", Matchers.is("listId02")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checklists[1].title", Matchers.is("New Checklist Title 02")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checklists[1].groupId", Matchers.is("groupId01")));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    @DisplayName("Should Return Grouplist With Added Checklist When Making a Put Request to endpoint - /api/grouplist/{username}/add/{listId}/to/{groupId}")
    public void shouldAddChecklistToGrouplist() throws Exception {
        //Mocked Response
        Checklist checklist01 = Checklist.builder()
            .listId("listId01")
            .title("Checklist Title 01")
            .groupId("groupId01")
            .build();
        Checklist checklist02 = Checklist.builder()
            .listId("listId02")
            .title("Added Checklist Title 02")
            .groupId("groupId01")
            .build();
        List<Checklist> checklists = new ArrayList<>();
        checklists.add(checklist01);
        checklists.add(checklist02);
        Grouplist grouplist = Grouplist.builder()
            .groupId("groupId01")
            .title("Grouplist Title 01")
            .checklists(checklists)
            .build();

        //Mock Service Class
        Mockito.when(grouplistService.addChecklist("testuser", "listId02", "groupId01"))
            .thenReturn(grouplist);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/grouplist/testuser/add/listId02/to/groupId01"))
            .andExpect(MockMvcResultMatchers.status().is(200))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.jsonPath("$.groupId", Matchers.is("groupId01")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is("Grouplist Title 01")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checklists[0].listId", Matchers.is("listId01")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checklists[0].title", Matchers.is("Checklist Title 01")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checklists[0].groupId", Matchers.is("groupId01")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checklists[1].listId", Matchers.is("listId02")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checklists[1].title", Matchers.is("Added Checklist Title 02")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checklists[1].groupId", Matchers.is("groupId01")));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    @DisplayName("Should Return Grouplist With The Moved Checklist When Making a Put Request to endpoint - /api/grouplist/{username}/move/{listId}/from/{fromGroupId}/to/{toGroupId}")
    public void shouldMoveChecklistFromGrouplistToGrouplist() throws Exception {
        //Mocked Response
        Checklist checklist01 = Checklist.builder()
            .listId("listId01")
            .title("Checklist Title 01")
            .groupId("groupId01")
            .build();
        Checklist checklist02 = Checklist.builder()
            .listId("listId02")
            .title("Moved Checklist Title 02")
            .groupId("groupId01")
            .build();
        List<Checklist> checklists = new ArrayList<>();
        checklists.add(checklist01);
        checklists.add(checklist02);
        Grouplist grouplist = Grouplist.builder()
            .groupId("groupId01")
            .title("Grouplist Title 01")
            .checklists(checklists)
            .build();

        //Mock Service Class
        Mockito.when(grouplistService.moveChecklist("testuser", "listId02", "fromGroupId", "groupId01"))
            .thenReturn(grouplist);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/grouplist/testuser/move/listId02/from/fromGroupId/to/groupId01"))
            .andExpect(MockMvcResultMatchers.status().is(200))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.jsonPath("$.groupId", Matchers.is("groupId01")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is("Grouplist Title 01")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checklists[0].listId", Matchers.is("listId01")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checklists[0].title", Matchers.is("Checklist Title 01")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checklists[0].groupId", Matchers.is("groupId01")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checklists[1].listId", Matchers.is("listId02")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checklists[1].title", Matchers.is("Moved Checklist Title 02")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.checklists[1].groupId", Matchers.is("groupId01")));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    @DisplayName("Should Return Grouplist Whose Checklist Got Removed When Making a Put Request to endpoint - /api/grouplist/{username}/remove/{listId}/from/{groupId}")
    public void shouldremoveChecklistFromGrouplist() throws Exception {
        //Mocked Response
        Grouplist grouplist = Grouplist.builder()
            .groupId("groupId01")
            .title("Grouplist Title 01")
            .checklists(new ArrayList<>())
            .build();

        //Mock Service Class
        Mockito.when(grouplistService.removeChecklist("testuser", "listId01", "groupId01"))
            .thenReturn(grouplist);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/grouplist/testuser/remove/listId01/from/groupId01"))
            .andExpect(MockMvcResultMatchers.status().is(200))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.jsonPath("$.groupId", Matchers.is("groupId01")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is("Grouplist Title 01")));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    @DisplayName("Should Return Only 204 Status Code When Making a Delete Request to endpoint - /api/grouplist/{username}/delete/{groupId}")
    public void shouldDeleteGrouplist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/grouplist/testuser/delete/groupId01"))
            .andExpect(MockMvcResultMatchers.status().is(204));
    }
}
