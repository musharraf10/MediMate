package com.medimate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for MediMate
 * This is the entry point of our Spring Boot application
 * 
 * @SpringBootApplication: This annotation combines three important annotations:
 * - @Configuration: Marks this class as a source of bean definitions
 * - @EnableAutoConfiguration: Enables Spring Boot's auto-configuration mechanism
 * - @ComponentScan: Enables component scanning for the current package and sub-packages
 * 
 * @EnableScheduling: Enables Spring's scheduled task execution capability
 */
@SpringBootApplication
@EnableScheduling
public class MediMateApplication {

    /**
     * Main method - the starting point of the application
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // Start the Spring Boot application
        SpringApplication.run(MediMateApplication.class, args);
        System.out.println("MediMate Application Started Successfully!");
    }
}