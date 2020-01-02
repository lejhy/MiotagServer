package Miotag.controller;

import Miotag.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
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

import static Miotag.controller.Utils.generateUserDto;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@RunWith(SpringRunner.class)
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void registerUser() throws Exception{
        UserDto userDto = generateUserDto();
        UserDto postResponseUserDto = registerUser(userDto);

        assertNotEquals(0, postResponseUserDto.getId());
        assertEquals(userDto.getFirstName(), postResponseUserDto.getFirstName());
        assertEquals(userDto.getLastName(), postResponseUserDto.getLastName());
        assertEquals(userDto.getEmail(), postResponseUserDto.getEmail());
        assertNull(postResponseUserDto.getPassword());
    }

    @Test
    public void getUser4xx() throws Exception{
        mockMvc.perform(get("/user")).andExpect(status().is4xxClientError());
    }

    @Test
    public void getUser() throws Exception{
        UserDto userDto = generateUserDto();
        UserDto postResponseUserDto = registerUser(userDto);

        MvcResult getResult = mockMvc.perform(get("/user")
                .with(httpBasic(userDto.getEmail(), userDto.getPassword()))
        ).andExpect(status().isOk()).andReturn();
        String getResponse = getResult.getResponse().getContentAsString();
        UserDto getResponseUserDto = objectMapper.readValue(getResponse, UserDto.class);

        assertEquals(postResponseUserDto.getId(), getResponseUserDto.getId());
        assertEquals(postResponseUserDto.getEmail(), getResponseUserDto.getEmail());
        assertEquals(postResponseUserDto.getFirstName(), getResponseUserDto.getFirstName());
        assertEquals(postResponseUserDto.getLastName(), getResponseUserDto.getLastName());
        assertNull(getResponseUserDto.getPassword());
    }

    @Test
    public void updateUser() throws Exception{
        UserDto userDto = generateUserDto();
        UserDto postResponseUserDto = registerUser(userDto);

        UserDto patchUserDto = generateUserDto();
        MvcResult patchResult = mockMvc.perform(patch("/user")
                .with(httpBasic(userDto.getEmail(), userDto.getPassword()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchUserDto))
        ).andExpect(status().isOk()).andReturn();
        String patchResponse = patchResult.getResponse().getContentAsString();
        UserDto patchResponseUserDto = objectMapper.readValue(patchResponse, UserDto.class);

        assertEquals(postResponseUserDto.getId(), patchResponseUserDto.getId());
        assertEquals(patchUserDto.getEmail(), patchResponseUserDto.getEmail());
        assertEquals(patchUserDto.getFirstName(), patchResponseUserDto.getFirstName());
        assertEquals(patchUserDto.getLastName(), patchResponseUserDto.getLastName());
        assertNull(patchResponseUserDto.getPassword());
    }

    @Test
    public void updateAndGetUser() throws Exception{
        UserDto userDto = generateUserDto();
        UserDto postResponseUserDto = registerUser(userDto);

        UserDto patchUserDto = generateUserDto();
        MvcResult patchResult = mockMvc.perform(patch("/user")
                .with(httpBasic(userDto.getEmail(), userDto.getPassword()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchUserDto))
        ).andExpect(status().isOk()).andReturn();
        String patchResponse = patchResult.getResponse().getContentAsString();
        UserDto patchResponseUserDto = objectMapper.readValue(patchResponse, UserDto.class);

        MvcResult getResult = mockMvc.perform(get("/user")
                .with(httpBasic(patchUserDto.getEmail(), patchUserDto.getPassword()))
        ).andExpect(status().isOk()).andReturn();
        String getResponse = getResult.getResponse().getContentAsString();
        UserDto getResponseUserDto = objectMapper.readValue(getResponse, UserDto.class);

        assertEquals(postResponseUserDto.getId(), getResponseUserDto.getId());
        assertEquals(patchUserDto.getEmail(), getResponseUserDto.getEmail());
        assertEquals(patchUserDto.getFirstName(), getResponseUserDto.getFirstName());
        assertEquals(patchUserDto.getLastName(), getResponseUserDto.getLastName());
        assertNull(getResponseUserDto.getPassword());
    }

    private UserDto registerUser(UserDto userDto) throws Exception{
        MvcResult postResult = mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto))
        ).andExpect(status().isOk()).andReturn();
        String postReponse = postResult.getResponse().getContentAsString();
        return objectMapper.readValue(postReponse, UserDto.class);
    }
}