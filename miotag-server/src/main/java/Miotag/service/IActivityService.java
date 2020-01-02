package Miotag.service;

import Miotag.dto.ActivityDto;
import Miotag.dto.ActivityLogDto;
import Miotag.model.User;

import java.util.List;

public interface IActivityService {
    List<ActivityDto> getActivities(User user);
    ActivityLogDto newActivityLog(ActivityLogDto activityLogDto, User user);
    List<ActivityLogDto> getActivityLogs(User user);
    List<ActivityLogDto> getActivityLogs(User user, long activityId);
}
