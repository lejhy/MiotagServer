package Miotag.controller;

import Miotag.dto.AlertDto;
import Miotag.dto.MessageDto;
import Miotag.dto.UserDto;
import Miotag.model.Alert;
import Miotag.model.User;
import Miotag.service.IAlertService;
import Miotag.service.ISecurityService;
import Miotag.service.IUserService;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;

import static Miotag.controller.Utils.generateUserDto;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@RunWith(SpringRunner.class)
public class AlertControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    IAlertService alertService;

    @Autowired
    ISecurityService securityService;

    private Random random = new Random();

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void getAlerts() throws Exception {
        UserDto userDto = generateUserDto();
        Utils.registerUser(mockMvc, objectMapper, userDto);
        User user = securityService.findUser(userDto.getEmail());
        List<Alert> alerts = new ArrayList<>();
        Date timestampBefore = new Date();
        for(int i = 0; i < 5; i++) {
            alerts.add(alertService.newAlert(generateAlert(user)));
        }
        Date timestampAfter = new Date();

        MvcResult response = mockMvc.perform(get("/alerts")
                .with(httpBasic(userDto.getEmail(), userDto.getPassword()))
        ).andExpect(status().isOk()).andReturn();
        List<AlertDto> receivedAlerts = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<List<AlertDto>>(){});
        receivedAlerts.sort(Comparator.comparingInt(o -> (int) o.getId()));

        for(int i = 0; i < alerts.size(); i++) {
            Alert expected = alerts.get(i);
            AlertDto received = receivedAlerts.get(i);
            assertEquals(expected.getId(), received.getId());
            assertTrue(timestampBefore.compareTo(received.getDate()) <= 0);
            assertTrue(timestampAfter.compareTo(received.getDate()) >= 0);
            assertEquals(expected.getUser().getId(), received.getUser().getId());
            assertEquals(expected.getMessage(), received.getMessage());
        }
    }

    private Alert generateAlert(User user) {
        Alert alert = new Alert();
        alert.setUser(user);
        alert.setMessage("Alert Message"+random.nextInt());
        return alert;
    }
}
