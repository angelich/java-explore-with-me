package ru.practicum.mainservice.error;


import jakarta.validation.ConstraintViolation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
