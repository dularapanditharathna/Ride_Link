package com.ridelink.account.exception;

import static com.ridelink.account.exception.DomainExceptions.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.stream.Collectors;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(DuplicateEmailException.class)
  ResponseEntity<ApiError> duplicate(Exception e, HttpServletRequest r) {
    return error(HttpStatus.CONFLICT, e.getMessage(), r);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  ResponseEntity<ApiError> missing(Exception e, HttpServletRequest r) {
    return error(HttpStatus.NOT_FOUND, e.getMessage(), r);
  }

  @ExceptionHandler(AuthenticationException.class)
  ResponseEntity<ApiError> auth(Exception e, HttpServletRequest r) {
    return error(HttpStatus.UNAUTHORIZED, e.getMessage(), r);
  }

  @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
  ResponseEntity<ApiError> denied(Exception e, HttpServletRequest r) {
    return error(HttpStatus.FORBIDDEN, "You do not have permission", r);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ApiError> validation(MethodArgumentNotValidException e, HttpServletRequest r) {
    String m =
        e.getBindingResult().getFieldErrors().stream()
            .map(x -> x.getField() + ": " + x.getDefaultMessage())
            .collect(Collectors.joining(", "));
    return error(HttpStatus.BAD_REQUEST, m, r);
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<ApiError> generic(Exception e, HttpServletRequest r) {
    return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", r);
  }

  private ResponseEntity<ApiError> error(HttpStatus s, String m, HttpServletRequest r) {
    return ResponseEntity.status(s)
        .body(new ApiError(Instant.now(), s.value(), s.getReasonPhrase(), m, r.getRequestURI()));
  }
}
