package Miotag.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserNotFoundException extends ResponseStatusException {
    public UserNotFoundException(long id) {
        super(HttpStatus.BAD_REQUEST, "User with id " + id + " does not exist");
    }
}
