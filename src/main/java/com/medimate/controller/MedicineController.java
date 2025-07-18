package com.medimate.controller;

import com.medimate.entity.Medicine;
import com.medimate.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Medicine Controller Class
 * This class handles HTTP requests for medicine operations
 * 
 * @RestController: Combines @Controller and @ResponseBody
 * @RequestMapping: Base URL mapping for all endpoints in this controller
 * @Validated: Enables validation for request parameters
 */
@RestController
@RequestMapping("/api/medicines")
@Validated
public class MedicineController {
    
    /**
     * Dependency injection of MedicineService
     * @Autowired: Spring automatically injects the service instance
     */
    @Autowired
    private MedicineService medicineService;
    
    /**
     * Add a new medicine
     * POST /api/medicines
     * @param medicine the medicine object from request body
     * @return ResponseEntity with created medicine and HTTP status
     */
    @PostMapping
    public ResponseEntity<?> addMedicine(@Valid @RequestBody Medicine medicine) {
        try {
            System.out.println("Received request to add medicine: " + medicine);
            
            Medicine savedMedicine = medicineService.addMedicine(medicine);
            
            // Return success response with created medicine
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMedicine);
            
        } catch (IllegalArgumentException e) {
            // Return bad request for validation errors
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
            
        } catch (Exception e) {
            // Return internal server error for other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding medicine: " + e.getMessage());
        }
    }
    
    /**
     * Get all medicines for a user
     * GET /api/medicines?userId=123
     * @param userId the user ID from query parameter
     * @return ResponseEntity with list of medicines
     */
    @GetMapping
    public ResponseEntity<?> getAllMedicines(@RequestParam Long userId) {
        try {
            System.out.println("Received request to get all medicines for user: " + userId);
            
            List<Medicine> medicines = medicineService.getAllMedicinesByUserId(userId);
            
            // Return success response with medicines list
            return ResponseEntity.ok(medicines);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving medicines: " + e.getMessage());
        }
    }
    
    /**
     * Get a specific medicine by ID
     * GET /api/medicines/123
     * @param id the medicine ID from path variable
     * @return ResponseEntity with medicine object
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getMedicineById(@PathVariable Long id) {
        try {
            System.out.println("Received request to get medicine with ID: " + id);
            
            Optional<Medicine> medicine = medicineService.getMedicineById(id);
            
            if (medicine.isPresent()) {
                return ResponseEntity.ok(medicine.get());
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving medicine: " + e.getMessage());
        }
    }
    
    /**
     * Update an existing medicine
     * PUT /api/medicines/123
     * @param id the medicine ID from path variable
     * @param medicine the updated medicine data from request body
     * @return ResponseEntity with updated medicine
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMedicine(@PathVariable Long id, @Valid @RequestBody Medicine medicine) {
        try {
            System.out.println("Received request to update medicine with ID: " + id);
            
            Medicine updatedMedicine = medicineService.updateMedicine(id, medicine);
            
            return ResponseEntity.ok(updatedMedicine);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating medicine: " + e.getMessage());
        }
    }
    
    /**
     * Delete a medicine
     * DELETE /api/medicines/123
     * @param id the medicine ID from path variable
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMedicine(@PathVariable Long id) {
        try {
            System.out.println("Received request to delete medicine with ID: " + id);
            
            medicineService.deleteMedicine(id);
            
            return ResponseEntity.ok("Medicine deleted successfully");
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting medicine: " + e.getMessage());
        }
    }
    
    /**
     * Get expired medicines for a user
     * GET /api/medicines/expired?userId=123
     * @param userId the user ID from query parameter
     * @return ResponseEntity with list of expired medicines
     */
    @GetMapping("/expired")
    public ResponseEntity<?> getExpiredMedicines(@RequestParam Long userId) {
        try {
            System.out.println("Received request to get expired medicines for user: " + userId);
            
            List<Medicine> expiredMedicines = medicineService.getExpiredMedicines(userId);
            
            return ResponseEntity.ok(expiredMedicines);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving expired medicines: " + e.getMessage());
        }
    }
    
    /**
     * Get medicines expiring soon for a user
     * GET /api/medicines/expiring-soon?userId=123
     * @param userId the user ID from query parameter
     * @return ResponseEntity with list of medicines expiring soon
     */
    @GetMapping("/expiring-soon")
    public ResponseEntity<?> getMedicinesExpiringSoon(@RequestParam Long userId) {
        try {
            System.out.println("Received request to get medicines expiring soon for user: " + userId);
            
            List<Medicine> expiringSoon = medicineService.getMedicinesExpiringSoon(userId);
            
            return ResponseEntity.ok(expiringSoon);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving medicines expiring soon: " + e.getMessage());
        }
    }
    
    /**
     * Get low stock medicines for a user
     * GET /api/medicines/low-stock?userId=123&threshold=5
     * @param userId the user ID from query parameter
     * @param threshold the minimum quantity threshold (optional, default: 5)
     * @return ResponseEntity with list of low stock medicines
     */
    @GetMapping("/low-stock")
    public ResponseEntity<?> getLowStockMedicines(@RequestParam Long userId, 
                                                 @RequestParam(required = false) Integer threshold) {
        try {
            System.out.println("Received request to get low stock medicines for user: " + userId + 
                             ", threshold: " + threshold);
            
            List<Medicine> lowStockMedicines = medicineService.getLowStockMedicines(userId, threshold);
            
            return ResponseEntity.ok(lowStockMedicines);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving low stock medicines: " + e.getMessage());
        }
    }
    
    /**
     * Search medicines by name for a user
     * GET /api/medicines/search?userId=123&name=aspirin
     * @param userId the user ID from query parameter
     * @param name the medicine name to search for
     * @return ResponseEntity with list of matching medicines
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchMedicinesByName(@RequestParam Long userId, 
                                                  @RequestParam String name) {
        try {
            System.out.println("Received request to search medicines by name for user: " + userId + 
                             ", name: " + name);
            
            List<Medicine> medicines = medicineService.searchMedicinesByName(userId, name);
            
            return ResponseEntity.ok(medicines);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error searching medicines: " + e.getMessage());
        }
    }
}