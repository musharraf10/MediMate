package com.medimate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Main application test class
 * This class contains basic tests for the MediMate application
 * 
 * @SpringBootTest: Loads the complete Spring application context for testing
 */
@SpringBootTest
class MediMateApplicationTests {

    /**
     * Test that the Spring application context loads successfully
     * This test will fail if there are any configuration issues
     */
    @Test
    void contextLoads() {
        // This test will pass if the application context loads without any errors
        // If there are any configuration issues, this test will fail
        System.out.println("Application context loaded successfully!");
    }
}