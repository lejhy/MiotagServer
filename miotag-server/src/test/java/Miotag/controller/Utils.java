package Miotag.controller;

import Miotag.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class Utils {

    private static Random random = new Random();

    static UserDto generateUserDto() {
        String uid = Integer.toString(random.nextInt());
        UserDto userDto = new UserDto();
        userDto.setEmail("user"+uid+"@domain.com");
        userDto.setFirstName("FirstName"+uid);
        userDto.setLastName("LastName"+uid);
        userDto.setPassword("password"+uid);
        return userDto;
    }

    static UserDto registerUser(MockMvc mockMvc, ObjectMapper objectMapper, UserDto user) throws Exception {
        return objectMapper.readValue(
                mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
                ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString(),
                UserDto.class
        );
    }
}

