package com.medimate.service;

import com.medimate.entity.Medicine;
import com.medimate.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Medicine Service Class
 * This class contains the business logic for medicine operations
 * 
 * @Service: Marks this class as a Spring service component
 * Service layer sits between Controller and Repository layers
 */
@Service
public class MedicineService {
    
    /**
     * Dependency injection of MedicineRepository
     * @Autowired: Spring automatically injects the repository instance
     */
    @Autowired
    private MedicineRepository medicineRepository;
    
    /**
     * Add a new medicine to the database
     * @param medicine the medicine object to be saved
     * @return the saved medicine object with generated ID
     */
    public Medicine addMedicine(Medicine medicine) {
        try {
            // Validate that the medicine is not null
            if (medicine == null) {
                throw new IllegalArgumentException("Medicine cannot be null");
            }
            
            // Validate that expiry date is not in the past
            if (medicine.getExpiryDate() != null && medicine.getExpiryDate().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Expiry date cannot be in the past");
            }
            
            // Save the medicine to database
            Medicine savedMedicine = medicineRepository.save(medicine);
            System.out.println("Medicine added successfully: " + savedMedicine);
            return savedMedicine;
            
        } catch (Exception e) {
            System.err.println("Error adding medicine: " + e.getMessage());
            throw new RuntimeException("Failed to add medicine: " + e.getMessage());
        }
    }
    
    /**
     * Get all medicines for a specific user
     * @param userId the user ID
     * @return list of all medicines belonging to the user
     */
    public List<Medicine> getAllMedicinesByUserId(Long userId) {
        try {
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }
            
            List<Medicine> medicines = medicineRepository.findByUserId(userId);
            System.out.println("Retrieved " + medicines.size() + " medicines for user " + userId);
            return medicines;
            
        } catch (Exception e) {
            System.err.println("Error retrieving medicines for user " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve medicines: " + e.getMessage());
        }
    }
    
    /**
     * Get a specific medicine by ID
     * @param id the medicine ID
     * @return Optional containing the medicine if found, empty otherwise
     */
    public Optional<Medicine> getMedicineById(Long id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("Medicine ID cannot be null");
            }
            
            return medicineRepository.findById(id);
            
        } catch (Exception e) {
            System.err.println("Error retrieving medicine with ID " + id + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve medicine: " + e.getMessage());
        }
    }
    
    /**
     * Update an existing medicine
     * @param id the medicine ID to update
     * @param updatedMedicine the updated medicine data
     * @return the updated medicine object
     */
    public Medicine updateMedicine(Long id, Medicine updatedMedicine) {
        try {
            if (id == null || updatedMedicine == null) {
                throw new IllegalArgumentException("Medicine ID and updated medicine cannot be null");
            }
            
            // Check if medicine exists
            Optional<Medicine> existingMedicine = medicineRepository.findById(id);
            if (existingMedicine.isEmpty()) {
                throw new RuntimeException("Medicine with ID " + id + " not found");
            }
            
            // Update the medicine
            Medicine medicine = existingMedicine.get();
            medicine.setName(updatedMedicine.getName());
            medicine.setQuantity(updatedMedicine.getQuantity());
            medicine.setExpiryDate(updatedMedicine.getExpiryDate());
            
            Medicine savedMedicine = medicineRepository.save(medicine);
            System.out.println("Medicine updated successfully: " + savedMedicine);
            return savedMedicine;
            
        } catch (Exception e) {
            System.err.println("Error updating medicine with ID " + id + ": " + e.getMessage());
            throw new RuntimeException("Failed to update medicine: " + e.getMessage());
        }
    }
    
    /**
     * Delete a medicine by ID
     * @param id the medicine ID to delete
     */
    public void deleteMedicine(Long id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("Medicine ID cannot be null");
            }
            
            // Check if medicine exists
            if (!medicineRepository.existsById(id)) {
                throw new RuntimeException("Medicine with ID " + id + " not found");
            }
            
            medicineRepository.deleteById(id);
            System.out.println("Medicine deleted successfully with ID: " + id);
            
        } catch (Exception e) {
            System.err.println("Error deleting medicine with ID " + id + ": " + e.getMessage());
            throw new RuntimeException("Failed to delete medicine: " + e.getMessage());
        }
    }
    
    /**
     * Get all expired medicines for a user
     * @param userId the user ID
     * @return list of expired medicines
     */
    public List<Medicine> getExpiredMedicines(Long userId) {
        try {
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }
            
            LocalDate today = LocalDate.now();
            List<Medicine> expiredMedicines = medicineRepository.findByUserIdAndExpiryDateBefore(userId, today);
            System.out.println("Found " + expiredMedicines.size() + " expired medicines for user " + userId);
            return expiredMedicines;
            
        } catch (Exception e) {
            System.err.println("Error retrieving expired medicines for user " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve expired medicines: " + e.getMessage());
        }
    }
    
    /**
     * Get medicines expiring soon (within next 30 days) for a user
     * @param userId the user ID
     * @return list of medicines expiring soon
     */
    public List<Medicine> getMedicinesExpiringSoon(Long userId) {
        try {
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }
            
            LocalDate today = LocalDate.now();
            LocalDate thirtyDaysFromNow = today.plusDays(30);
            
            List<Medicine> expiringSoon = medicineRepository.findByUserIdAndExpiryDateBetween(userId, today, thirtyDaysFromNow);
            System.out.println("Found " + expiringSoon.size() + " medicines expiring soon for user " + userId);
            return expiringSoon;
            
        } catch (Exception e) {
            System.err.println("Error retrieving medicines expiring soon for user " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve medicines expiring soon: " + e.getMessage());
        }
    }
    
    /**
     * Get medicines with low stock (quantity less than threshold) for a user
     * @param userId the user ID
     * @param threshold the minimum quantity threshold (default: 5)
     * @return list of medicines with low stock
     */
    public List<Medicine> getLowStockMedicines(Long userId, Integer threshold) {
        try {
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }
            
            // Set default threshold if not provided
            if (threshold == null) {
                threshold = 5;
            }
            
            List<Medicine> lowStockMedicines = medicineRepository.findByUserIdAndQuantityLessThan(userId, threshold);
            System.out.println("Found " + lowStockMedicines.size() + " low stock medicines for user " + userId + " (threshold: " + threshold + ")");
            return lowStockMedicines;
            
        } catch (Exception e) {
            System.err.println("Error retrieving low stock medicines for user " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve low stock medicines: " + e.getMessage());
        }
    }
    
    /**
     * Search medicines by name for a user
     * @param userId the user ID
     * @param name the medicine name to search for
     * @return list of medicines matching the name
     */
    public List<Medicine> searchMedicinesByName(Long userId, String name) {
        try {
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }
            
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Medicine name cannot be null or empty");
            }
            
            List<Medicine> medicines = medicineRepository.findByUserIdAndNameContainingIgnoreCase(userId, name.trim());
            System.out.println("Found " + medicines.size() + " medicines matching name '" + name + "' for user " + userId);
            return medicines;
            
        } catch (Exception e) {
            System.err.println("Error searching medicines by name for user " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to search medicines: " + e.getMessage());
        }
    }
    
    /**
     * Get all expired medicines across all users (used by scheduled task)
     * @return list of all expired medicines
     */
    public List<Medicine> getAllExpiredMedicines() {
        try {
            LocalDate today = LocalDate.now();
            List<Medicine> expiredMedicines = medicineRepository.findAllExpiredMedicines(today);
            System.out.println("Found " + expiredMedicines.size() + " expired medicines across all users");
            return expiredMedicines;
            
        } catch (Exception e) {
            System.err.println("Error retrieving all expired medicines: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve expired medicines: " + e.getMessage());
        }
    }
}