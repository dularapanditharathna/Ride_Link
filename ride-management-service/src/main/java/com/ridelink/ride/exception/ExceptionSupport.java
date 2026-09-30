package com.ridelink.ride.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.stream.Collectors;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

public final class ExceptionSupport {
  private ExceptionSupport() {}

  public record ApiError(
      Instant timestamp, int status, String error, String message, String path) {}

  public static class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String m) {
      super(m);
    }
  }

  public static class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(String m) {
      super(m);
    }
  }

  public static class NoAvailableDriverException extends RuntimeException {
    public NoAvailableDriverException(String m) {
      super(m);
    }
  }

  public static class UnauthorizedOperationException extends RuntimeException {
    public UnauthorizedOperationException(String m) {
      super(m);
    }
  }

  public static class DownstreamServiceException extends RuntimeException {
    public DownstreamServiceException(String m) {
      super(m);
    }
  }

  @RestControllerAdvice
  public static class Handler {
    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ApiError> nf(Exception e, HttpServletRequest r) {
      return out(HttpStatus.NOT_FOUND, e.getMessage(), r);
    }

    @ExceptionHandler({InvalidStatusTransitionException.class, NoAvailableDriverException.class})
    ResponseEntity<ApiError> co(Exception e, HttpServletRequest r) {
      return out(HttpStatus.CONFLICT, e.getMessage(), r);
    }

    @ExceptionHandler({
      UnauthorizedOperationException.class,
      org.springframework.security.access.AccessDeniedException.class
    })
    ResponseEntity<ApiError> fo(Exception e, HttpServletRequest r) {
      return out(HttpStatus.FORBIDDEN, "You do not have permission", r);
    }

    @ExceptionHandler(DownstreamServiceException.class)
    ResponseEntity<ApiError> ds(Exception e, HttpServletRequest r) {
      return out(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage(), r);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> va(MethodArgumentNotValidException e, HttpServletRequest r) {
      return out(
          HttpStatus.BAD_REQUEST,
          e.getBindingResult().getFieldErrors().stream()
              .map(x -> x.getField() + ": " + x.getDefaultMessage())
              .collect(Collectors.joining(", ")),
          r);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> ge(Exception e, HttpServletRequest r) {
      return out(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", r);
    }

    private ResponseEntity<ApiError> out(HttpStatus s, String m, HttpServletRequest r) {
      return ResponseEntity.status(s)
          .body(new ApiError(Instant.now(), s.value(), s.getReasonPhrase(), m, r.getRequestURI()));
    }
  }
}
