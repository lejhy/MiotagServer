package Miotag.controller;

import Miotag.dto.ActivityDto;
import Miotag.dto.ActivityLogDto;
import Miotag.model.Activity;
import Miotag.model.User;
import Miotag.service.IActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    @Autowired
    private IActivityService activityService;

    @GetMapping
    public List<ActivityDto> getActivities(@AuthenticationPrincipal User user) {
        return activityService.getActivities(user);
    }

    @PostMapping("/logs")
    public ActivityLogDto newActivityLog(@RequestBody @Valid ActivityLogDto activityLogDto, BindingResult bindingResult, @AuthenticationPrincipal User user) {
        return activityService.newActivityLog(activityLogDto, user); //TODO binding check
    }

    @GetMapping("/logs")
    public List<ActivityLogDto> getActivityLogs(@AuthenticationPrincipal User user) {
        return activityService.getActivityLogs(user);
    }

    @GetMapping("/logs/{activityId}")
    public List<ActivityLogDto> getActivityLogs(@PathVariable("activityId") long activityId, @AuthenticationPrincipal User user) {
        return activityService.getActivityLogs(user, activityId);
    }
}
