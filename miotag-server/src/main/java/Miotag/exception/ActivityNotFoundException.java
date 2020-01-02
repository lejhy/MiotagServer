package Miotag.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ActivityNotFoundException extends ResponseStatusException {
    public ActivityNotFoundException(long id) {
        super(HttpStatus.BAD_REQUEST, "Activity with id " + id + " does not exist");
    }
}
