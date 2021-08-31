package com.reddit.exception;

public class SpringRedditException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = -698356756131343403L;

  public SpringRedditException(String exMessage, Exception exception) {
    super(exMessage, exception);
  }

  public SpringRedditException(String exMessage) {
    super(exMessage);
  }

}
