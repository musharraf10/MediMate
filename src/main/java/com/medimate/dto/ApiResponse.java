package com.medimate.dto;

import java.time.LocalDateTime;

/**
 * API Response DTO (Data Transfer Object)
 * This class provides a standardized format for API responses
 * 
 * DTO classes are used to transfer data between different layers of the application
 * and to provide a consistent response format for REST APIs
 */
public class ApiResponse<T> {
    
    /**
     * Indicates if the operation was successful
     */
    private boolean success;
    
    /**
     * Response message (success or error message)
     */
    private String message;
    
    /**
     * The actual data being returned (generic type T)
     */
    private T data;
    
    /**
     * Timestamp when the response was created
     */
    private LocalDateTime timestamp;
    
    /**
     * Default constructor
     */
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Constructor for successful responses with data
     * @param success success status
     * @param message success message
     * @param data the data to return
     */
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Constructor for responses without data
     * @param success success status
     * @param message response message
     */
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Static method to create a successful response with data
     * @param data the data to return
     * @param message success message
     * @return ApiResponse object
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data);
    }
    
    /**
     * Static method to create a successful response without data
     * @param message success message
     * @return ApiResponse object
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message);
    }
    
    /**
     * Static method to create an error response
     * @param message error message
     * @return ApiResponse object
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message);
    }
    
    // Getter and Setter methods
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                '}';
    }
}