package com.medimate.exception;

import com.medimate.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler
 * This class handles exceptions globally across the entire application
 * 
 * @RestControllerAdvice: Combines @ControllerAdvice and @ResponseBody
 * This allows us to handle exceptions globally and return JSON responses
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Handle validation errors
     * This method is called when @Valid annotation validation fails
     * @param ex the validation exception
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        System.err.println("Validation error occurred at: " + LocalDateTime.now());
        
        Map<String, String> errors = new HashMap<>();
        
        // Extract field errors from the exception
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ApiResponse<Map<String, String>> response = new ApiResponse<>(
            false, 
            "Validation failed", 
            errors
        );
        
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * Handle illegal argument exceptions
     * @param ex the illegal argument exception
     * @return ResponseEntity with error message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        System.err.println("Illegal argument error: " + ex.getMessage() + " at: " + LocalDateTime.now());
        
        ApiResponse<String> response = ApiResponse.error(ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * Handle runtime exceptions
     * @param ex the runtime exception
     * @return ResponseEntity with error message
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException ex) {
        System.err.println("Runtime error: " + ex.getMessage() + " at: " + LocalDateTime.now());
        
        ApiResponse<String> response = ApiResponse.error(ex.getMessage());
        
        // Check if it's a "not found" error
        if (ex.getMessage().contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * Handle all other exceptions
     * @param ex the general exception
     * @return ResponseEntity with generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGeneralException(Exception ex) {
        System.err.println("Unexpected error: " + ex.getMessage() + " at: " + LocalDateTime.now());
        ex.printStackTrace();
        
        ApiResponse<String> response = ApiResponse.error("An unexpected error occurred. Please try again later.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}