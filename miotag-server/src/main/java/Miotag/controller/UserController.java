package Miotag.controller;

import Miotag.dto.UserDto;
import Miotag.exception.ValidationErrorException;
import Miotag.service.IUserService;
import Miotag.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping(value = "/user")
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
    public UserDto getUser(Principal principal) {
        return userService.getUserByEmail(principal.getName());
    }

    @PatchMapping
    public UserDto updateUser(@RequestBody @Valid UserDto userDto, BindingResult bindingResult, Principal principal) {
        if(bindingResult.hasErrors()) {
            if (bindingResult.getFieldErrors().stream().anyMatch(error -> error.getRejectedValue() != null)) {
                throw new ValidationErrorException(bindingResult);
            }
        }
        return userService.updateUser(userDto, principal.getName());
    }
}
