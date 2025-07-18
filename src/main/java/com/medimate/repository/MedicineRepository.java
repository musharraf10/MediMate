package com.medimate.repository;

import com.medimate.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Medicine Repository Interface
 * This interface extends JpaRepository to provide CRUD operations for Medicine entity
 * 
 * @Repository: Marks this interface as a Spring repository component
 * JpaRepository<Medicine, Long>: Provides CRUD operations for Medicine entity with Long as ID type
 */
@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    
    /**
     * Find all medicines for a specific user
     * Spring Data JPA automatically implements this method based on the method name
     * @param userId the user ID to search for
     * @return list of medicines belonging to the user
     */
    List<Medicine> findByUserId(Long userId);
    
    /**
     * Find medicines by user ID and expiry date before a specific date (expired medicines)
     * @param userId the user ID
     * @param date the date to compare against (typically current date)
     * @return list of expired medicines for the user
     */
    List<Medicine> findByUserIdAndExpiryDateBefore(Long userId, LocalDate date);
    
    /**
     * Find medicines expiring within a date range (expiring soon)
     * @param userId the user ID
     * @param startDate start date of the range (typically current date)
     * @param endDate end date of the range (typically current date + 30 days)
     * @return list of medicines expiring soon for the user
     */
    List<Medicine> findByUserIdAndExpiryDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find medicines with low stock (quantity below threshold)
     * @param userId the user ID
     * @param threshold the minimum quantity threshold
     * @return list of medicines with low stock for the user
     */
    List<Medicine> findByUserIdAndQuantityLessThan(Long userId, Integer threshold);
    
    /**
     * Custom query to find medicines by name (case-insensitive search)
     * @Query: Custom JPQL query annotation
     * @param userId the user ID
     * @param name the medicine name to search for
     * @return list of medicines matching the name for the user
     */
    @Query("SELECT m FROM Medicine m WHERE m.userId = :userId AND LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Medicine> findByUserIdAndNameContainingIgnoreCase(@Param("userId") Long userId, @Param("name") String name);
    
    /**
     * Custom query to find all expired medicines across all users
     * This will be used by the scheduled task
     * @param date the date to compare against (typically current date)
     * @return list of all expired medicines
     */
    @Query("SELECT m FROM Medicine m WHERE m.expiryDate < :date")
    List<Medicine> findAllExpiredMedicines(@Param("date") LocalDate date);
    
    /**
     * Custom query to count expired medicines for a user
     * @param userId the user ID
     * @param date the date to compare against
     * @return count of expired medicines
     */
    @Query("SELECT COUNT(m) FROM Medicine m WHERE m.userId = :userId AND m.expiryDate < :date")
    Long countExpiredMedicinesByUserId(@Param("userId") Long userId, @Param("date") LocalDate date);
    
    /**
     * Custom query to count medicines expiring soon for a user
     * @param userId the user ID
     * @param startDate start date of the range
     * @param endDate end date of the range
     * @return count of medicines expiring soon
     */
    @Query("SELECT COUNT(m) FROM Medicine m WHERE m.userId = :userId AND m.expiryDate BETWEEN :startDate AND :endDate")
    Long countExpiringSoonByUserId(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}