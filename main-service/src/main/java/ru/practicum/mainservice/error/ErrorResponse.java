package ru.practicum.mainservice.error;


import javax.validation.ConstraintViolation;
import java.time.Instant;
import java.util.Set;

public class ErrorResponse {
    private Set<ConstraintViolation<?>> errors;
    private String message;
    private String reason;
    private String status;
    private Instant timestamp = Instant.now();


    public ErrorResponse(Set<ConstraintViolation<?>> errors, String message, String reason, String status) {
        this.errors = errors;
        this.message = message;
        this.reason = reason;
        this.status = status;
    }
}
