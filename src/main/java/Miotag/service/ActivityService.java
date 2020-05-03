package Miotag.service;

import Miotag.dto.ActivityDto;
import Miotag.dto.ActivityLogDto;
import Miotag.dto.UserDto;
import Miotag.exception.ActivityNotFoundException;
import Miotag.exception.PrivateDataException;
import Miotag.mapper.ActivityLogMapper;
import Miotag.mapper.ActivityMapper;
import Miotag.model.Activity;
import Miotag.model.ActivityLog;
import Miotag.model.User;
import Miotag.repository.ActivityLogRepository;
import Miotag.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityService implements IActivityService {

    private final ActivityRepository activityRepository;
    private final ActivityLogRepository activityLogRepository;
    private final ActivityMapper activityMapper;
    private final ActivityLogMapper activityLogMapper;
    private final IUserService userService;

    @Autowired
    public ActivityService(ActivityRepository activityRepository, ActivityLogRepository activityLogRepository, ActivityMapper activityMapper, ActivityLogMapper activityLogMapper, IUserService userService) {
        this.activityRepository = activityRepository;
        this.activityLogRepository = activityLogRepository;
        this.activityMapper = activityMapper;
        this.activityLogMapper = activityLogMapper;
        this.userService = userService;
    }

    @Override
    public List<ActivityDto> getActivities(User user) {
        List<Activity> activities = activityRepository.findAll();
        return activities.stream().map(activityMapper::map).collect(Collectors.toList());
    }

    @Override
    public ActivityLogDto newActivityLog(ActivityLogDto activityLogDto, User user) {
        long activityId = activityLogDto.getActivity().getId();
        if(!activityRepository.existsById(activityId)) {
            throw new ActivityNotFoundException(activityId);
        }
        ActivityLog activityLog = prepareNewActivityLog(activityLogDto, user);
        ActivityLog savedActivityLog = activityLogRepository.save(activityLog);
        return activityLogMapper.map(savedActivityLog);
    }

    @Override
    public List<ActivityLogDto> getActivityLogs(User user) {
        List<ActivityLog> activityLogs = activityLogRepository.findAllByUser(user);
        return activityLogs.stream().map(activityLogMapper::map).collect(Collectors.toList());
    }

    @Override
    public List<ActivityLogDto> getActivityLogs(User user, long activityId) {
        if(!activityRepository.existsById(activityId)) {
            throw new ActivityNotFoundException(activityId);
        }
        List<ActivityLog> activityLogs = activityLogRepository.findAllByUserAndActivityId(user, activityId);
        return activityLogs.stream().map(activityLogMapper::map).collect(Collectors.toList());
    }

    @Override
    public List<ActivityLogDto> getActivityLogs(long id) {
        UserDto userDto = userService.getUser(id);
        if (userDto.isPrivate()) {
            throw new PrivateDataException(id);
        } else {
            List<ActivityLog> activityLogs = activityLogRepository.findAllByUserId(id);
            return activityLogs.stream().map(activityLogMapper::map).collect(Collectors.toList());
        }
    }

    @Override
    public List<ActivityLogDto> getActivityLogs(long id, long activityId) {
        UserDto userDto = userService.getUser(id);
        if (userDto.isPrivate()) {
            throw new PrivateDataException(id);
        } else {
            if(!activityRepository.existsById(activityId)) {
                throw new ActivityNotFoundException(activityId);
            }
            List<ActivityLog> activityLogs = activityLogRepository.findAllByUserIdAndActivityId(id, activityId);
            return activityLogs.stream().map(activityLogMapper::map).collect(Collectors.toList());
        }
    }

    private ActivityLog prepareNewActivityLog(ActivityLogDto activityLogDto, User user) {
        ActivityLog activityLog = activityLogMapper.map(activityLogDto);
        activityLog.setId(0);
        activityLog.setDate(new Date());
        activityLog.setUser(user);
        return activityLog;
    }
}
