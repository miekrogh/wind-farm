package app.windfarm.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/** Responsible for mapping service-level exceptions to HTTP responses. */
@ControllerAdvice
public class ControllerExceptionHandler {

  /**
   * Converts IllegalArgumentException into a 404 Bad Request response.
   *
   * @param e the exception
   * @return 404 Bad Request response with error message
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
    return ResponseEntity.badRequest().body(e.getMessage());
  }
}
