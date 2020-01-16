package Miotag.controller;

import Miotag.dto.UserDto;
import Miotag.exception.ValidationErrorException;
import Miotag.model.User;
import Miotag.service.IUserService;
import Miotag.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final IUserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto registerUser(@RequestBody @Valid UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationErrorException(bindingResult);
        }
        return userService.registerUser(userDto);
    }

    @GetMapping
    public UserDto getUser(@AuthenticationPrincipal User user) {
        return userService.getUser(user);
    }

    @PatchMapping
    public UserDto updateUser(@RequestBody @Valid UserDto userDto, BindingResult bindingResult, @AuthenticationPrincipal User user) {
        if(bindingResult.hasErrors()) {
            if (bindingResult.getFieldErrors().stream().anyMatch(error -> error.getRejectedValue() != null)) {
                throw new ValidationErrorException(bindingResult);
            }
        }
        return userService.updateUser(user, userDto);
    }

    @GetMapping(params = "q")
    public List<UserDto> queryUsers(@RequestParam(name = "q") String query) {
        return userService.getUsers(query);
    }
}
