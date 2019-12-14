package Miotag.controller;

import Miotag.dto.UserDto;
import Miotag.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
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
    public List<UserDto> getUsersFollowed(Principal principal) {
        return userService.getUsersFollowed(principal.getName());
    }

    @PostMapping
    public boolean follow(@RequestBody UserDto userDto, Principal principal) {
        if (userDto.getId() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please provide the target user id");
        }
        return userService.followUser(principal.getName(), userDto);
    }

    @DeleteMapping
    public boolean unfollow(@RequestBody UserDto userDto, Principal principal) {
        if (userDto.getId() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please provide the target user id");
        }
        return userService.unfollowUser(principal.getName(), userDto);
    }
}
