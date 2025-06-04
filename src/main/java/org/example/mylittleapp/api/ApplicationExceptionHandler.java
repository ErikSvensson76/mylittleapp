package org.example.mylittleapp.api;

import org.example.mylittleapp.exception.NoEntityFoundException;
import org.example.mylittleapp.exception.RuntimeIOException;
import org.example.mylittleapp.model.dto.ExceptionResponse;
import org.example.mylittleapp.model.dto.ValidationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@ControllerAdvice
public class ApplicationExceptionHandler {

  public static final String VALIDATIONS_FAILED = "One or more validations failed";



  private Supplier<ValidationResponse> getValidationResponse(
    final String message,
    final String error,
    final Integer status,
    final String path,
    final MethodArgumentNotValidException ex
  ){
    return () -> {
      ValidationResponse response = new ValidationResponse();
      response.setMessage(message);
      response.setError(error);
      response.setStatus(status);
      response.setPath(path);

      Map<String, List<String>> errors = new HashMap<>();
      List<String> fields = ex.getBindingResult().getFieldErrors().stream()
        .distinct()
        .map(FieldError::getField)
        .toList();

      for(String field : fields){
        List<String> list  = new ArrayList<>();
        for(FieldError err : ex.getFieldErrors(field)){
          list.add(err.getDefaultMessage());
        }
        errors.put(field, list);
      }

      response.setValidationErrors(errors);
      return response;
    };
  }

  /**
   *
   * @param error String
   * @param status Integer
   * @param path String
   * @param exception Exception
   * @return Supplier
   */
  private Supplier<ExceptionResponse> getExceptionResponse(
    final String error,
    final Integer status,
    final String path,
    final Exception exception
  ){
    return () -> {
      ExceptionResponse exceptionResponse = new ExceptionResponse();

      exceptionResponse.setMessage(exception.getMessage());
      exceptionResponse.setError(error);
      exceptionResponse.setStatus(status);
      exceptionResponse.setPath(path);

      return exceptionResponse;
    };
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ValidationResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex,
      WebRequest request)
  {
    ValidationResponse response =  getValidationResponse(
      VALIDATIONS_FAILED,
      HttpStatus.BAD_REQUEST.name(),
      HttpStatus.BAD_REQUEST.value(),
      request.getDescription(false),
      ex
    ).get();

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NoEntityFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ExceptionResponse> handleNoEntityFoundException(
      NoEntityFoundException ex,
      WebRequest request
  ){
      return new ResponseEntity<>(
          getExceptionResponse(
          HttpStatus.NOT_FOUND.name(),
          HttpStatus.NOT_FOUND.value(),
          request.getDescription(false),
          ex
      ).get(),
        HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(
      IllegalArgumentException ex,
      WebRequest request
  ){
    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(
        getExceptionResponse(
          HttpStatus.BAD_REQUEST.name(),
          HttpStatus.BAD_REQUEST.value(),
          request.getDescription(false),
          ex
        ).get()
      );
  }

  @ExceptionHandler(RuntimeIOException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ExceptionResponse> handleRuntimeIoException(
      RuntimeIOException ex,
      WebRequest request
  ){
    HttpStatus status = HttpStatus.BAD_REQUEST;
    return ResponseEntity.status(status).body(
      getExceptionResponse(
        status.name(),
        status.value(),
        request.getDescription(false),
        ex
      ).get()
    );
  }

}
