package com.ing.javaexercise.exception;

public class TweetNotFoundException extends Exception {

  public TweetNotFoundException(final String message) {
    super(message);
  }

  public TweetNotFoundException(final String message, final Throwable t) {
    super(message, t);
  }

}
