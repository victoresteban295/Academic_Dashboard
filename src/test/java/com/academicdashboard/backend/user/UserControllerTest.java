package com.academicdashboard.backend.user;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Before;
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

import com.academicdashboard.backend.checklist.Checklist;
import com.academicdashboard.backend.checklist.Grouplist;
import com.academicdashboard.backend.config.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .addFilters(jwtAuthenticationFilter)
            .build();
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    public void shouldGetUsersChecklists() throws Exception {
        List<Checklist> checklists = new ArrayList<>();

        Checklist checklistA = Checklist.builder()
            .username("testuser")
            .listId("listIdA")
            .title("Checklist Title A")
            .checkpoints(new ArrayList<>())
            .completedPoints(new ArrayList<>())
            .build();

        Checklist checklistB = Checklist.builder()
            .username("testuser")
            .listId("listIdB")
            .title("Checklist Title B")
            .checkpoints(new ArrayList<>())
            .completedPoints(new ArrayList<>())
            .build();

        checklists.add(checklistA);
        checklists.add(checklistB);

        Mockito.when(userService.getChecklists("testuser"))
            .thenReturn(checklists);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/users/testuser/checklists"))
            .andExpect(MockMvcResultMatchers.status().is(200))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].username", Matchers.is("testuser")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].listId", Matchers.is("listIdA")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].title", Matchers.is("Checklist Title A")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].username", Matchers.is("testuser")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].listId", Matchers.is("listIdB")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].title", Matchers.is("Checklist Title B")));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    public void shouldGetUsersGrouplists() throws Exception {
        List<Grouplist> grouplists = new ArrayList<>();

        Grouplist grouplistA = Grouplist.builder()
            .username("testuser")
            .groupId("groupIdA")
            .title("Grouplist Title A")
            .checklists(new ArrayList<>())
            .build();
        Grouplist grouplistB = Grouplist.builder()
            .username("testuser")
            .groupId("groupIdB")
            .title("Grouplist Title B")
            .checklists(new ArrayList<>())
            .build();

        grouplists.add(grouplistA);
        grouplists.add(grouplistB);

        Mockito.when(userService.getGrouplists("testuser"))
            .thenReturn(grouplists);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/users/testuser/grouplists"))
            .andExpect(MockMvcResultMatchers.status().is(200))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].username", Matchers.is("testuser")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].groupId", Matchers.is("groupIdA")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].title", Matchers.is("Grouplist Title A")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].username", Matchers.is("testuser")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].groupId", Matchers.is("groupIdB")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].title", Matchers.is("Grouplist Title B")));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    public void shouldReorderUsersChecklists() throws Exception {
        List<Checklist> checklists = new ArrayList<>();

        Checklist checklistA = Checklist.builder()
            .username("testuser")
            .listId("listIdA")
            .title("Checklist Title A")
            .checkpoints(new ArrayList<>())
            .completedPoints(new ArrayList<>())
            .build();

        Checklist checklistB = Checklist.builder()
            .username("testuser")
            .listId("listIdB")
            .title("Checklist Title B")
            .checkpoints(new ArrayList<>())
            .completedPoints(new ArrayList<>())
            .build();

        checklists.add(checklistA);
        checklists.add(checklistB);

        //Mock Service Class
        Mockito.when(userService.reorderChecklists("testuser", checklists))
            .thenReturn(checklists);

        //Request Body
        record ReqBody (List<Checklist> checklists) {}
        ReqBody reqBody = new ReqBody(checklists);

        //Convert Request Body to Byte[]
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] reqBodyAsByte = objectMapper.writeValueAsBytes(reqBody);

        mockMvc.perform(MockMvcRequestBuilders.patch("/v1.0/users/testuser/checklists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqBodyAsByte))
            .andExpect(MockMvcResultMatchers.status().is(200))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].username", Matchers.is("testuser")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].listId", Matchers.is("listIdA")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].title", Matchers.is("Checklist Title A")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].username", Matchers.is("testuser")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].listId", Matchers.is("listIdB")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].title", Matchers.is("Checklist Title B")));
    }
    
    @Test
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    public void shouldReorderUsersGrouplists() throws Exception {
        List<Grouplist> grouplists = new ArrayList<>();

        Grouplist grouplistA = Grouplist.builder()
            .username("testuser")
            .groupId("groupIdA")
            .title("Grouplist Title A")
            .checklists(new ArrayList<>())
            .build();
        Grouplist grouplistB = Grouplist.builder()
            .username("testuser")
            .groupId("groupIdB")
            .title("Grouplist Title B")
            .checklists(new ArrayList<>())
            .build();

        grouplists.add(grouplistA);
        grouplists.add(grouplistB);

        //Mock Service Class
        Mockito.when(userService.reorderGrouplists("testuser", grouplists))
            .thenReturn(grouplists);

        //Request Body
        record ReqBody (List<Grouplist> grouplists) {}
        ReqBody reqBody = new ReqBody(grouplists);

        //Convert Request Body to Byte[]
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] reqBodyAsByte = objectMapper.writeValueAsBytes(reqBody);

        mockMvc.perform(MockMvcRequestBuilders.patch("/v1.0/users/testuser/grouplists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqBodyAsByte))
            .andExpect(MockMvcResultMatchers.status().is(200))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].username", Matchers.is("testuser")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].groupId", Matchers.is("groupIdA")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].title", Matchers.is("Grouplist Title A")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].username", Matchers.is("testuser")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].groupId", Matchers.is("groupIdB")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].title", Matchers.is("Grouplist Title B")));
    }
}
