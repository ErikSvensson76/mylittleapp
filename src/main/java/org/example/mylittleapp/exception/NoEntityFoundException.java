package org.example.mylittleapp.exception;

public class NoEntityFoundException extends RuntimeException{

  public NoEntityFoundException(String message) {
    super(message);
  }
}
