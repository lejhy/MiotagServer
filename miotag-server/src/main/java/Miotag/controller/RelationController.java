package Miotag.controller;

import Miotag.dto.UserDto;
import Miotag.model.User;
import Miotag.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(path = "/relations")
public class RelationController {

    private final IUserService userService;

    @Autowired
    public RelationController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getUsersFollowed(@AuthenticationPrincipal User user) {
        return userService.getUsersFollowed(user);
    }

    @PostMapping("/{id}")
    public boolean follow(@PathVariable("id") long targetId, @AuthenticationPrincipal User user) {
        return userService.followUser(user, targetId);
    }

    @DeleteMapping("/{id}")
    public boolean unfollow(@PathVariable("id") long targetId, @AuthenticationPrincipal User user) {
        return userService.unfollowUser(user, targetId);
    }
}
