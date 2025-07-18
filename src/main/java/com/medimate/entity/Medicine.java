package com.medimate.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Medicine Entity Class
 * This class represents a medicine record in our database
 * 
 * @Entity: Marks this class as a JPA entity (database table)
 * @Table: Specifies the table name in the database
 */
@Entity
@Table(name = "medicines")
public class Medicine {
    
    /**
     * Primary key for the medicine record
     * @Id: Marks this field as the primary key
     * @GeneratedValue: Automatically generates values for this field
     * @Column: Specifies column properties in the database
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    /**
     * Name of the medicine
     * @NotBlank: Validation annotation - field cannot be null or empty
     * @Size: Validation annotation - specifies minimum and maximum length
     */
    @NotBlank(message = "Medicine name is required")
    @Size(min = 2, max = 100, message = "Medicine name must be between 2 and 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    /**
     * Quantity of the medicine in stock
     * @NotNull: Field cannot be null
     * @Min: Minimum value validation
     */
    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    /**
     * Expiry date of the medicine
     * @NotNull: Field cannot be null
     * @Future: Date must be in the future (for new medicines)
     */
    @NotNull(message = "Expiry date is required")
    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;
    
    /**
     * Date when the medicine was added to the system
     * This field is automatically set when the record is created
     */
    @Column(name = "added_date", nullable = false)
    private LocalDateTime addedDate;
    
    /**
     * ID of the user who added this medicine
     * This allows multiple users to manage their own medicines
     */
    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /**
     * Default constructor
     * Required by JPA
     */
    public Medicine() {
        // Set the added date to current date/time when creating a new medicine
        this.addedDate = LocalDateTime.now();
    }
    
    /**
     * Constructor with all required fields
     * @param name medicine name
     * @param quantity quantity in stock
     * @param expiryDate expiry date
     * @param userId user ID who owns this medicine
     */
    public Medicine(String name, Integer quantity, LocalDate expiryDate, Long userId) {
        this.name = name;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.userId = userId;
        this.addedDate = LocalDateTime.now();
    }
    
    // Getter and Setter methods
    // These methods allow access to private fields from other classes
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public LocalDate getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public LocalDateTime getAddedDate() {
        return addedDate;
    }
    
    public void setAddedDate(LocalDateTime addedDate) {
        this.addedDate = addedDate;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    /**
     * Override toString method for better debugging
     * @return string representation of the medicine object
     */
    @Override
    public String toString() {
        return "Medicine{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", expiryDate=" + expiryDate +
                ", addedDate=" + addedDate +
                ", userId=" + userId +
                '}';
    }
}