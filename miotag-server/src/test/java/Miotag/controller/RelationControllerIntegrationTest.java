package Miotag.controller;

import Miotag.dto.UserDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;

import static Miotag.controller.Utils.generateUserDto;
import static Miotag.controller.Utils.registerUser;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@RunWith(SpringRunner.class)
public class RelationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private final int N_USERS = 8;
    private UserDto users[];

    @Before
    public void setUp() throws Exception {
        users = new UserDto[N_USERS];
        for (int i = 0; i < N_USERS; i++) {
            UserDto user = generateUserDto();
            UserDto responseUser = registerUser(mockMvc, objectMapper, user);
            user.setId(responseUser.getId());
            users[i] = user;
        }
    }

    @Test
    public void followMultiple() throws Exception{
        UserDto user = users[0];
        List<UserDto> targets = new ArrayList<>(N_USERS - 1);
        targets.addAll(Arrays.asList(users).subList(1, users.length));
        targets.sort(Comparator.comparingInt(o -> (int) o.getId()));

        for(UserDto target: targets) postRelations(user, target, true);

        List<UserDto> relations = getRelations(user);
        relations.sort(Comparator.comparingInt(o -> (int) o.getId()));

        assertEquals(relations.size(), targets.size());
        for(int i = 0; i < targets.size(); i++) {
            assertEquals(targets.get(i).getId(), relations.get(i).getId());
        }
    }

    @Test
    public void followSingle() throws Exception{
        UserDto user = users[0];
        UserDto target = users[1];

        postRelations(user, target, true);
        postRelations(user, target, false);

        List<UserDto> relations = getRelations(user);

        assertEquals(relations.size(), 1);
        assertEquals(relations.get(0).getId(), target.getId());
    }

    @Test
    public void followUnfollow() throws Exception{
        UserDto user = users[0];
        UserDto target = users[1];

        postRelations(user, target, true);
        deleteRelations(user, target, true);
        deleteRelations(user, target, false);

        List<UserDto> relations = getRelations(user);

        assertEquals(relations.size(), 0);
    }

    private List<UserDto> getRelations(UserDto user) throws Exception{
        MvcResult response = mockMvc.perform(get("/relations")
                .with(httpBasic(user.getEmail(), user.getPassword()))
        ).andExpect(status().isOk()).andReturn();
        return objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<List<UserDto>>(){});
    }

    private void postRelations(UserDto user, UserDto target, boolean expected) throws Exception{
        MvcResult response = mockMvc.perform(post("/relations")
                .with(httpBasic(user.getEmail(), user.getPassword()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(target))
        ).andExpect(status().isOk()).andReturn();
        assertEquals(expected, objectMapper.readValue(response.getResponse().getContentAsString(), boolean.class));
    }

    private void deleteRelations(UserDto user, UserDto target, boolean expected) throws Exception {
        MvcResult response = mockMvc.perform(delete("/relations")
                .with(httpBasic(user.getEmail(), user.getPassword()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(target))
        ).andExpect(status().isOk()).andReturn();
        assertEquals(expected, objectMapper.readValue(response.getResponse().getContentAsString(), boolean.class));
    }
}