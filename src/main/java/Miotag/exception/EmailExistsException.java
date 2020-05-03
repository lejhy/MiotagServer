package Miotag.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EmailExistsException extends ResponseStatusException {
    public EmailExistsException (String email) {
        super(HttpStatus.BAD_REQUEST, "User with email " + email + " already exists");
    }
}
