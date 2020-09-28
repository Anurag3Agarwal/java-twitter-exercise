package com.ing.javaexercise.exception;

public class AuthenticationException extends RuntimeException{

  public AuthenticationException() {
    super();
  }

  public AuthenticationException(final String message) {
    super(message);
  }

  public AuthenticationException(final String message, final Throwable t) {
    super(message, t);
  }

}
