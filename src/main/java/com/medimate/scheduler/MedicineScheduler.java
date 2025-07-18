package com.medimate.scheduler;

import com.medimate.entity.Medicine;
import com.medimate.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Medicine Scheduler Class
 * This class contains scheduled tasks for medicine-related operations
 * 
 * @Component: Marks this class as a Spring component
 * Scheduled tasks will automatically run at specified intervals
 */
@Component
public class MedicineScheduler {
    
    /**
     * Dependency injection of MedicineService
     */
    @Autowired
    private MedicineService medicineService;
    
    /**
     * Scheduled task to check and log expired medicines
     * This task runs every day at 9:00 AM
     * 
     * @Scheduled: Annotation to mark this method as a scheduled task
     * cron: Cron expression for scheduling (seconds, minutes, hours, day of month, month, day of week)
     * "0 0 9 * * ?" means: At 9:00 AM every day
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkExpiredMedicines() {
        try {
            System.out.println("=== SCHEDULED TASK: Checking expired medicines ===");
            System.out.println("Task started at: " + LocalDateTime.now());
            
            // Get all expired medicines across all users
            List<Medicine> expiredMedicines = medicineService.getAllExpiredMedicines();
            
            if (expiredMedicines.isEmpty()) {
                System.out.println("✅ No expired medicines found");
            } else {
                System.out.println("⚠️  Found " + expiredMedicines.size() + " expired medicines:");
                
                // Log details of each expired medicine
                for (Medicine medicine : expiredMedicines) {
                    System.out.println("- Medicine: " + medicine.getName() + 
                                     " | Expired on: " + medicine.getExpiryDate() + 
                                     " | Quantity: " + medicine.getQuantity() + 
                                     " | User ID: " + medicine.getUserId());
                }
                
                // Group by user for better reporting
                expiredMedicines.stream()
                    .collect(java.util.stream.Collectors.groupingBy(Medicine::getUserId))
                    .forEach((userId, medicines) -> {
                        System.out.println("User " + userId + " has " + medicines.size() + " expired medicines");
                    });
            }
            
            System.out.println("Task completed at: " + LocalDateTime.now());
            System.out.println("=== END OF SCHEDULED TASK ===");
            
        } catch (Exception e) {
            System.err.println("❌ Error in scheduled task - checking expired medicines: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Scheduled task to check medicines expiring soon
     * This task runs every day at 9:30 AM
     * 
     * "0 30 9 * * ?" means: At 9:30 AM every day
     */
    @Scheduled(cron = "0 30 9 * * ?")
    public void checkMedicinesExpiringSoon() {
        try {
            System.out.println("=== SCHEDULED TASK: Checking medicines expiring soon ===");
            System.out.println("Task started at: " + LocalDateTime.now());
            
            LocalDate today = LocalDate.now();
            LocalDate thirtyDaysFromNow = today.plusDays(30);
            
            // Note: This is a simplified version. In a real application, you might want to
            // check for each user individually and send them personalized notifications
            
            System.out.println("Checking medicines expiring between " + today + " and " + thirtyDaysFromNow);
            System.out.println("⚠️  This is a reminder to check your medicines expiring soon!");
            System.out.println("Please log into the application to view medicines expiring in the next 30 days");
            
            System.out.println("Task completed at: " + LocalDateTime.now());
            System.out.println("=== END OF SCHEDULED TASK ===");
            
        } catch (Exception e) {
            System.err.println("❌ Error in scheduled task - checking medicines expiring soon: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Scheduled task for general system health check
     * This task runs every hour
     * 
     * "0 0 * * * ?" means: At the beginning of every hour
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void systemHealthCheck() {
        try {
            System.out.println("=== SYSTEM HEALTH CHECK ===");
            System.out.println("System is running healthy at: " + LocalDateTime.now());
            
            // You can add more health checks here, such as:
            // - Database connectivity check
            // - Memory usage check
            // - Application performance metrics
            
            System.out.println("=== END OF HEALTH CHECK ===");
            
        } catch (Exception e) {
            System.err.println("❌ Error in system health check: " + e.getMessage());
        }
    }
    
    /**
     * Scheduled task to run every 10 minutes for testing purposes
     * You can disable this task in production
     * 
     * "0 */10 * * * ?" means: Every 10 minutes
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void testScheduledTask() {
        try {
            System.out.println("⏰ Test scheduled task running at: " + LocalDateTime.now());
            // This is useful for testing that the scheduler is working
            // Remove or comment out this method in production
            
        } catch (Exception e) {
            System.err.println("❌ Error in test scheduled task: " + e.getMessage());
        }
    }
}