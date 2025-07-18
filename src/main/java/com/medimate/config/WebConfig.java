package com.medimate.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web Configuration Class
 * This class configures CORS (Cross-Origin Resource Sharing) settings
 * to allow the frontend to communicate with the backend API
 * 
 * @Configuration: Marks this class as a configuration class
 * WebMvcConfigurer: Interface to customize Spring MVC configuration
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    /**
     * Configure CORS mappings
     * This allows the frontend (running on different port) to access the API
     * 
     * @param registry CORS registry to configure mappings
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Apply CORS to all API endpoints
                .allowedOrigins("http://localhost:8080", "http://127.0.0.1:8080") // Allow frontend origins
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow these HTTP methods
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(true) // Allow credentials (cookies, authorization headers)
                .maxAge(3600); // Cache preflight response for 1 hour
        
        System.out.println("CORS configuration applied for API endpoints");
    }
}