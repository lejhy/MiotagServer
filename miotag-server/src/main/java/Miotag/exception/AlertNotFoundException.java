package Miotag.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AlertNotFoundException extends ResponseStatusException {
    public AlertNotFoundException(long id) {
        super(HttpStatus.BAD_REQUEST, "Alert with id " + id + " does not exist");
    }
}
