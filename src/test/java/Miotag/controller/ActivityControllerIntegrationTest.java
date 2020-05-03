package Miotag.controller;

import Miotag.dto.ActivityDto;
import Miotag.dto.ActivityLogDto;
import Miotag.dto.UserDto;
import Miotag.mapper.ActivityMapper;
import Miotag.model.Activity;
import Miotag.repository.ActivityRepository;
import Miotag.service.IActivityService;
import Miotag.service.ISecurityService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@RunWith(SpringRunner.class)
public class ActivityControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ISecurityService securityService;

    @Autowired
    IActivityService activityService;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    ActivityMapper activityMapper;

    private Random random = new Random();

    private UserDto userUnderTest;
    private ActivityDto activityDto;

    @Before
    public  void setUp() throws Exception {
        activityDto = activityMapper.map(activityRepository.save(generateActivity()));
        userUnderTest = generateUserDto();
        userUnderTest.setId(registerUser(mockMvc, objectMapper, userUnderTest).getId());
    }

    @Test
    public void getActivities() throws Exception {
        List<Activity> activities = new ArrayList<>();
        for(int i = 0; i < 8; i++) {
            activities.add(activityRepository.save(generateActivity()));
        }

        MvcResult response = mockMvc.perform(get("/activities")
                .with(httpBasic(userUnderTest.getEmail(), userUnderTest.getPassword()))
        ).andExpect(status().isOk()).andReturn();
        List<ActivityDto> activitiesReceived = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<List<ActivityDto>>(){});
        activitiesReceived.sort(Comparator.comparingLong(ActivityDto::getId));
        int activitiesToAssertStartIndex = 0;
        for(int i = 0; i < activitiesReceived.size(); i++) {
            if (activitiesReceived.get(i).getId() == activities.get(0).getId()) {
                activitiesToAssertStartIndex = i;
                break;
            }
        }
        List<ActivityDto> activitiesToAssert = activitiesReceived.subList(activitiesToAssertStartIndex, activitiesReceived.size());

        assertEquals(activities.size(), activitiesToAssert.size());
        for(int i = 0; i < activities.size(); i++) {
            Activity expected = activities.get(i);
            ActivityDto received = activitiesToAssert.get(i);
            assertEquals(expected.getId(), received.getId());
            assertEquals(expected.getName(), received.getName());
            assertEquals(expected.getDescription(), received.getDescription());
        }
    }

    @Test
    public void newAndGetActivityLog() throws Exception {
        ActivityLogDto activityLogToSend = generateActivityLogDto(activityDto);
        Date timestampBefore = new Date();
        ActivityLogDto activityLogSent = postActivityLog(userUnderTest, activityLogToSend);
        Date timestampAfter = new Date();
        List<ActivityLogDto> activityLogsReceived = getActivityLog(userUnderTest, null, activityDto);

        assertEquals(1, activityLogsReceived.size());
        ActivityLogDto activityLogReceived = activityLogsReceived.get(0);

        assertEquals(activityLogSent.getId(), activityLogReceived.getId());
        assertEquals(activityLogToSend.getActivity().getId(), activityLogSent.getActivity().getId());
        assertEquals(activityLogSent.getActivity().getId(), activityLogReceived.getActivity().getId());
        assertTrue(timestampBefore.compareTo(activityLogSent.getDate()) <= 0);
        assertTrue(timestampAfter.compareTo(activityLogSent.getDate()) >= 0);
        assertEquals(activityLogSent.getDate(), activityLogReceived.getDate());
        assertEquals(activityLogToSend.getScore(), activityLogSent.getScore());
        assertEquals(activityLogSent.getScore(), activityLogReceived.getScore());
        assertEquals(activityLogToSend.getLength(), activityLogSent.getLength());
        assertEquals(activityLogSent.getLength(), activityLogReceived.getLength());
    }

    @Test
    public void newAndGetActivityLogMultiple() throws Exception {
        List<ActivityLogDto> activityLogsSent = new ArrayList<>();

        for(int i = 0; i < 5; i++) {
            activityLogsSent.add(postActivityLog(userUnderTest, generateActivityLogDto(activityDto)));
        }

        List<ActivityLogDto> activityLogsReceived = getActivityLog(userUnderTest, null, activityDto);
        activityLogsReceived.sort(Comparator.comparingLong(ActivityLogDto::getId));
        for(int i = 0; i < activityLogsSent.size(); i++) {
            assertActivityLogEquality(activityLogsSent.get(i), activityLogsReceived.get(i));
        }
    }

    @Test
    public void getActivityLogEmpty() throws Exception {
        List<ActivityLogDto> activityLogsReceived = getActivityLog(userUnderTest, null, activityDto);

        assertEquals(0, activityLogsReceived.size());
    }

    @Test
    public void newActivityLog4xx() throws Exception {
        ActivityLogDto activityLogDto = generateActivityLogDto(activityDto);
        activityLogDto.getActivity().setId(9999);

        mockMvc.perform(post("/activities/logs")
                .with(httpBasic(userUnderTest.getEmail(), userUnderTest.getPassword()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(activityLogDto))
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void getActivityLogsOfAnotherUser() throws Exception {
        UserDto otherUser = generateUserDto();
        otherUser.setId(registerUser(mockMvc, objectMapper, otherUser).getId());

        List<ActivityLogDto> activityLogsSent = new ArrayList<>();

        for(int i = 0; i < 5; i++) {
            activityLogsSent.add(postActivityLog(otherUser, generateActivityLogDto(activityDto)));
        }

        List<ActivityLogDto> activityLogsReceived = getActivityLog(userUnderTest, otherUser.getId(), activityDto);
        activityLogsReceived.sort(Comparator.comparingLong(ActivityLogDto::getId));
        for(int i = 0; i < activityLogsSent.size(); i++) {
            assertActivityLogEquality(activityLogsSent.get(i), activityLogsReceived.get(i));
        }
    }

    @Test
    public void getActivityLogsEmptyOfAnotherUser() throws Exception {
        UserDto otherUser = generateUserDto();
        otherUser.setId(registerUser(mockMvc, objectMapper, otherUser).getId());
        List<ActivityLogDto> activityLogsReceived = getActivityLog(userUnderTest, otherUser.getId(), activityDto);

        assertEquals(0, activityLogsReceived.size());
    }

    @Test
    public void getActivityLogsEmptyOfAnotherUserPrivate() throws Exception {
        UserDto otherUser = generateUserDto();
        otherUser.setPrivate(true);
        otherUser.setId(registerUser(mockMvc, objectMapper, otherUser).getId());
        mockMvc.perform(get("/activities/" + otherUser.getId() + "/logs")
                .with(httpBasic(userUnderTest.getEmail(), userUnderTest.getPassword()))
        ).andExpect(status().isForbidden());
    }

    private void assertActivityLogEquality(ActivityLogDto expectedActivityLog, ActivityLogDto receivedActivityLog) {
        assertEquals(expectedActivityLog.getId(), receivedActivityLog.getId());
        assertEquals(expectedActivityLog.getActivity().getId(), receivedActivityLog.getActivity().getId());
        assertEquals(expectedActivityLog.getDate(), receivedActivityLog.getDate());
        assertEquals(expectedActivityLog.getScore(), receivedActivityLog.getScore());
        assertEquals(expectedActivityLog.getLength(), receivedActivityLog.getLength());
    }

    private ActivityLogDto postActivityLog(UserDto userDto, ActivityLogDto activityLogDto) throws Exception {
        MvcResult response = mockMvc.perform(post("/activities/logs")
                .with(httpBasic(userDto.getEmail(), userDto.getPassword()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(activityLogDto))
        ).andExpect(status().isOk()).andReturn();
        return objectMapper.readValue(response.getResponse().getContentAsString(), ActivityLogDto.class);
    }

    private List<ActivityLogDto> getActivityLog(UserDto userDto, Long userId, ActivityDto activityDto) throws Exception {
        MvcResult response = mockMvc.perform(get("/activities" + (userId == null ? "" : "/"+userId) + "/logs" + (activityDto == null ? "" : "/"+activityDto.getId()))
                .with(httpBasic(userDto.getEmail(), userDto.getPassword()))
        ).andExpect(status().isOk()).andReturn();
        return objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<List<ActivityLogDto>>(){});
    }

    private ActivityLogDto generateActivityLogDto(ActivityDto activityDto) {
        ActivityLogDto activityLogDto = new ActivityLogDto();
        activityLogDto.setActivity(activityDto);
        activityLogDto.setLength(random.nextInt());
        activityLogDto.setScore(random.nextInt());
        return activityLogDto;
    }

    private Activity generateActivity() {
        Activity activity = new Activity();
        activity.setName("activityName"+ random.nextInt());
        activity.setDescription("activityDescription"+ random.nextInt());
        activity.setEnabled(true);
        return activity;
    }
}
