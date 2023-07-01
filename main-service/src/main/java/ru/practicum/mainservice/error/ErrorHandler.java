package ru.practicum.mainservice.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

import static java.util.Collections.emptySet;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({
            IllegalArgumentException.class, MissingServletRequestParameterException.class, MethodArgumentNotValidException.class
    })
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleValidationException(RuntimeException e) {
        log.warn("Bad request, message={}", e.getMessage());
        return new ErrorResponse(
                emptySet(),
                e.getLocalizedMessage(),
                "For the requested operation the conditions are not met.",
                BAD_REQUEST.getReasonPhrase());
    }

    @ExceptionHandler
    @ResponseStatus(FORBIDDEN)
    public ErrorResponse handleForbiddenException(ForbiddenException e) {
        log.warn("Forbidden request, message={}", e.getMessage());
        return new ErrorResponse(
                emptySet(),
                e.getMessage(),
                "Certain rights are required for this action.",
                FORBIDDEN.getReasonPhrase());
    }

    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        log.warn("Not found, message={}", e.getMessage());
        return new ErrorResponse(
                emptySet(),
                e.getMessage(),
                "The required object was not found.",
                NOT_FOUND.getReasonPhrase());
    }

    @ExceptionHandler({ConstraintViolationException.class, DataIntegrityViolationException.class})
    @ResponseStatus(CONFLICT)
    public ErrorResponse handleConstraintViolationException(RuntimeException e) {
        log.warn("Constraint validation exception, message={}", e.getMessage());
        return new ErrorResponse(
                emptySet(),
                e.getMessage(),
                "Integrity constraint has been violated.",
                CONFLICT.getReasonPhrase());
    }

    @ExceptionHandler
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handleOtherExceptions(Throwable e) {
        log.error("Internal error, message={}", e.getMessage());
        return new ErrorResponse(
                emptySet(),
                e.getLocalizedMessage(),
                "Error occurred.",
                INTERNAL_SERVER_ERROR.getReasonPhrase());
    }
}
