package Miotag.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

public class ValidationErrorException extends ResponseStatusException {
    public ValidationErrorException(BindingResult bindingResult) {
        super(HttpStatus.BAD_REQUEST, generateErrorMessage(bindingResult));
    }

    private static String generateErrorMessage(BindingResult bindingResult) {
        StringBuilder errorMessage = new StringBuilder();
        bindingResult.getFieldErrors().forEach(e ->
                errorMessage.append(e.getField()).append(": ").append(e.getDefaultMessage()).append("\n")
        );
        return errorMessage.toString();
    }
}
