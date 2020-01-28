package Miotag.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PrivateDataException extends ResponseStatusException {
    public PrivateDataException(long id) {
        super(HttpStatus.FORBIDDEN, "Data of the user with id "+id+" is private...");
    }
}
