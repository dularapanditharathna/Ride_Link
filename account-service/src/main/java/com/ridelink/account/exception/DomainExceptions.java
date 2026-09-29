package com.ridelink.account.exception;

public final class DomainExceptions {
  private DomainExceptions() {}

  public static class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String m) {
      super(m);
    }
  }

  public static class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String m) {
      super(m);
    }
  }

  public static class AuthenticationException extends RuntimeException {
    public AuthenticationException(String m) {
      super(m);
    }
  }
}
