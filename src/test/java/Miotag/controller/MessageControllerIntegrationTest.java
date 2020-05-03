package Miotag.controller;

import Miotag.dto.MessageDto;
import Miotag.dto.UserDto;
import com.fasterxml.jackson.core.type.TypeReference;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static Miotag.controller.Utils.generateUserDto;
import static Miotag.controller.Utils.registerUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@RunWith(SpringRunner.class)
public class MessageControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private Random random = new Random();

    @Test
    public void sendSingle() throws Exception {
        UserDto sender = generateUserDto();
        sender.setId(registerUser(mockMvc, objectMapper, sender).getId());
        UserDto receiver = generateUserDto();
        receiver.setId(registerUser(mockMvc, objectMapper, receiver).getId());

        MessageDto messageToSend = generateMessageDto(receiver);
        Date timestampBefore = new Date();
        postMessages(sender, messageToSend);
        Date timestampAfter = new Date();
        List<MessageDto> messagesReceived = getMessages(receiver);

        assertEquals(1, messagesReceived.size());
        MessageDto messageReceived = messagesReceived.get(0);

        assertEquals(sender.getId(), messageReceived.getFrom().getId());
        assertEquals(receiver.getId(), messageReceived.getTo().getId());
        assertTrue(timestampBefore.compareTo(messageReceived.getDate()) <= 0);
        assertTrue(timestampAfter.compareTo(messageReceived.getDate()) >= 0);
        assertEquals(messageToSend.getSubject(), messageReceived.getSubject());
        assertEquals(messageToSend.getContent(), messageReceived.getContent());
    }

    @Test
    public void sendAndReceiveMultiple() throws Exception {
        UserDto userUnderTest = generateUserDto();
        userUnderTest.setId(registerUser(mockMvc, objectMapper, userUnderTest).getId());
        UserDto otherUser = generateUserDto();
        otherUser.setId(registerUser(mockMvc, objectMapper, otherUser).getId());
        List<MessageDto> messagesSent = new ArrayList<>();
        List<MessageDto> messagesReceived = new ArrayList<>();

        for(int i = 0; i < 5; i++) {
            messagesSent.add(postMessages(userUnderTest, generateMessageDto(otherUser)));
            messagesReceived.add(postMessages(otherUser, generateMessageDto(userUnderTest)));
        }

        List<MessageDto> messagesReturned = getMessages(userUnderTest);
        messagesReturned.sort((o1, o2) -> o1.getFrom().getId() == o2.getFrom().getId() ? (int)o1.getId() - (int)o2.getId() : o1.getFrom().getId() == userUnderTest.getId() ? -1 : 1);
        for (int i = 0; i < messagesSent.size()*2; i++) {
            MessageDto expectedMessage = i < messagesSent.size() ? messagesSent.get(i) : messagesReceived.get(i - messagesSent.size());
            MessageDto message = messagesReturned.get(i);

            assertEquals(expectedMessage.getId(), message.getId());
            assertEquals(expectedMessage.getTo().getId(), message.getTo().getId());
            assertEquals(expectedMessage.getDate(), message.getDate());
            assertEquals(expectedMessage.getFrom().getId(), message.getFrom().getId());
            assertEquals(expectedMessage.getSubject(), message.getSubject());
            assertEquals(expectedMessage.getContent(), message.getContent());
        }
    }

    private List<MessageDto> getMessages(UserDto user) throws Exception {
        MvcResult response = mockMvc.perform(get("/messages")
                .with(httpBasic(user.getEmail(), user.getPassword()))
        ).andExpect(status().isOk()).andReturn();
        return objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<List<MessageDto>>(){});
    }

    private MessageDto postMessages(UserDto user, MessageDto message) throws Exception {
        MvcResult response = mockMvc.perform(post("/messages")
                .with(httpBasic(user.getEmail(), user.getPassword()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(message))
        ).andExpect(status().isOk()).andReturn();
        return objectMapper.readValue(response.getResponse().getContentAsString(), MessageDto.class);
    }

    private MessageDto generateMessageDto(UserDto to) {
        MessageDto messageDto = new MessageDto();
        messageDto.setTo(to);
        messageDto.setSubject("Send Single Test Subject"+ random.nextInt());
        messageDto.setContent("Send Single Test Content"+ random.nextInt());
        return messageDto;
    }
}
