-- MediMate Database Schema
-- This file contains the SQL commands to create the database and tables

-- Create database (run this command in MySQL command line or MySQL Workbench)
CREATE DATABASE IF NOT EXISTS medimate_db;

-- Use the database
USE medimate_db;

-- Create medicines table
CREATE TABLE IF NOT EXISTS medicines (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    expiry_date DATE NOT NULL,
    added_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,
    
    -- Add constraints
    CONSTRAINT chk_quantity CHECK (quantity >= 0),
    CONSTRAINT chk_expiry_date CHECK (expiry_date >= CURDATE())
);

-- Create indexes for better performance
CREATE INDEX idx_medicines_user_id ON medicines(user_id);
CREATE INDEX idx_medicines_expiry_date ON medicines(expiry_date);
CREATE INDEX idx_medicines_name ON medicines(name);
CREATE INDEX idx_medicines_quantity ON medicines(quantity);

-- Insert sample data for testing
INSERT INTO medicines (name, quantity, expiry_date, user_id) VALUES
('Aspirin', 50, '2025-12-31', 1),
('Paracetamol', 30, '2025-06-15', 1),
('Vitamin C', 100, '2024-03-20', 1),
('Cough Syrup', 2, '2024-02-10', 1),
('Antibiotics', 15, '2025-09-30', 2),
('Blood Pressure Medicine', 25, '2025-08-20', 2),
('Diabetes Medicine', 5, '2024-04-15', 2),
('Pain Relief Gel', 8, '2025-11-10', 3);

-- Create a view for expired medicines
CREATE VIEW expired_medicines AS
SELECT 
    id,
    name,
    quantity,
    expiry_date,
    added_date,
    user_id,
    DATEDIFF(CURDATE(), expiry_date) as days_expired
FROM medicines
WHERE expiry_date < CURDATE();

-- Create a view for medicines expiring soon (within 30 days)
CREATE VIEW medicines_expiring_soon AS
SELECT 
    id,
    name,
    quantity,
    expiry_date,
    added_date,
    user_id,
    DATEDIFF(expiry_date, CURDATE()) as days_until_expiry
FROM medicines
WHERE expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY);

-- Create a view for low stock medicines (quantity < 10)
CREATE VIEW low_stock_medicines AS
SELECT 
    id,
    name,
    quantity,
    expiry_date,
    added_date,
    user_id
FROM medicines
WHERE quantity < 10;

-- Show table structure
DESCRIBE medicines;

-- Show sample data
SELECT * FROM medicines;

-- Show expired medicines
SELECT * FROM expired_medicines;

-- Show medicines expiring soon
SELECT * FROM medicines_expiring_soon;

-- Show low stock medicines
SELECT * FROM low_stock_medicines;