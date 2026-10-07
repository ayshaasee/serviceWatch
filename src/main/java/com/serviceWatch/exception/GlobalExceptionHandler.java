package com.serviceWatch.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(InvalidIncidentStatusException.class)
	public ResponseEntity<Map<String, String>> handleInvalidIncidentStatus(
	        InvalidIncidentStatusException ex) {

	    Map<String, String> error = new HashMap<>();
	    error.put("message", ex.getMessage());

	    return ResponseEntity
	            .status(HttpStatus.BAD_REQUEST)
	            .body(error);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> handleValidationErrors(
	        MethodArgumentNotValidException ex) {

	    Map<String, String> errors = new HashMap<>();

	    ex.getBindingResult()
	            .getFieldErrors()
	            .forEach(error ->
	                    errors.put(error.getField(), error.getDefaultMessage())
	            );

	    return errors;
	}
	
	@ExceptionHandler(NotificationAccessDeniedException.class)
	public ResponseEntity<Map<String, String>> handleNotificationAccessDenied(
	        NotificationAccessDeniedException ex) {

	    Map<String, String> error = new HashMap<>();
	    error.put("message", ex.getMessage());

	    return ResponseEntity
	            .status(HttpStatus.FORBIDDEN)
	            .body(error);
	}
	@ExceptionHandler(IncidentNotFoundException.class)
	public ResponseEntity<Map<String, String>> handleIncidentNotFound(
	        IncidentNotFoundException ex) {

	    Map<String, String> error = new HashMap<>();
	    error.put("message", ex.getMessage());

	    return ResponseEntity
	            .status(HttpStatus.NOT_FOUND)
	            .body(error);
	}
	@ExceptionHandler(ServiceNotFoundException.class)
	public ResponseEntity<Map<String, String>> handleServiceNotFound(
	        ServiceNotFoundException ex) {

	    Map<String, String> error = new HashMap<>();
	    error.put("message", ex.getMessage());

	    return ResponseEntity
	            .status(HttpStatus.NOT_FOUND)
	            .body(error);
	}
	@ExceptionHandler(ServiceRequiredException.class)
	public ResponseEntity<Map<String, String>> handleServiceRequired(
	        ServiceRequiredException ex) {

	    Map<String, String> error = new HashMap<>();
	    error.put("message", ex.getMessage());

	    return ResponseEntity
	            .status(HttpStatus.BAD_REQUEST)
	            .body(error);
	}
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<Map<String, String>> handleUserNotFound(
	        UserNotFoundException ex) {

	    Map<String, String> error = new HashMap<>();
	    error.put("message", ex.getMessage());

	    return ResponseEntity
	            .status(HttpStatus.NOT_FOUND)
	            .body(error);
	}
}