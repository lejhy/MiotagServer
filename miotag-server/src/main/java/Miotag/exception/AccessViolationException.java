package Miotag.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AccessViolationException extends ResponseStatusException {
    public AccessViolationException() {
        super(HttpStatus.FORBIDDEN, "You shall not pass!");
    }
}