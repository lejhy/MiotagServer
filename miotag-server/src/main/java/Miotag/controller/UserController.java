package Miotag.controller;

import Miotag.dto.UserDto;
import Miotag.exception.EmailExistsException;
import Miotag.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/register")
    public UserDto registerUser(@Valid UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, getValidationErrorMessage(bindingResult));
        }
        try {
            return userService.registerUser(userDto);
        } catch (EmailExistsException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User with email " + userDto.getEmail() + " already exists"
            );
        }
    }

    @GetMapping
    public UserDto getUser(Principal principal) {
        return userService.getUserByEmail(principal.getName());
    }

    @PatchMapping
    public UserDto updateUser(@Valid UserDto userDto, BindingResult bindingResult, Principal principal) {
        if(bindingResult.hasErrors()) {
            if (bindingResult.getFieldErrors().stream().anyMatch(error -> error.getRejectedValue() != null)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, getValidationErrorMessage(bindingResult));
            }
        }
        try {
            return userService.updateUser(userDto, principal.getName());
        } catch (EmailExistsException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User with email " + userDto.getEmail() + " already exists"
            );
        }
    }

    private String getValidationErrorMessage(BindingResult bindingResult) {
        StringBuilder errorMessage = new StringBuilder();
        bindingResult.getFieldErrors().forEach(e ->
                errorMessage.append(e.getField()).append(": ").append(e.getDefaultMessage()).append("\n")
        );
        return errorMessage.toString();
    }
}
