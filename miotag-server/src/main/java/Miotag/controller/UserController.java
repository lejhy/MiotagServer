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

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public UserDto registerUser(@Valid UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getFieldErrors().forEach(e ->
                    errorMessage.append(e.getField()).append(": ").append(e.getDefaultMessage()).append("\n")
            );
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage.toString());
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
    public String postUser() {
        return "Get User";
    }
}
