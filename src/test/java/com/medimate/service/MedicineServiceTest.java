package com.medimate.service;

import com.medimate.entity.Medicine;
import com.medimate.repository.MedicineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MedicineService
 * These tests verify the business logic in the service layer
 */
class MedicineServiceTest {
    
    /**
     * Mock the repository dependency
     * @Mock creates a mock object that we can control in our tests
     */
    @Mock
    private MedicineRepository medicineRepository;
    
    /**
     * Inject mocks into the service
     * @InjectMocks creates an instance of the service and injects the mocked dependencies
     */
    @InjectMocks
    private MedicineService medicineService;
    
    /**
     * Sample medicine for testing
     */
    private Medicine testMedicine;
    
    /**
     * Set up test data before each test
     * @BeforeEach runs before each test method
     */
    @BeforeEach
    void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);
        
        // Create test medicine
        testMedicine = new Medicine();
        testMedicine.setId(1L);
        testMedicine.setName("Test Medicine");
        testMedicine.setQuantity(10);
        testMedicine.setExpiryDate(LocalDate.now().plusDays(30));
        testMedicine.setUserId(1L);
    }
    
    /**
     * Test adding a medicine successfully
     */
    @Test
    void testAddMedicine_Success() {
        // Arrange: Set up the mock behavior
        when(medicineRepository.save(any(Medicine.class))).thenReturn(testMedicine);
        
        // Act: Call the method under test
        Medicine result = medicineService.addMedicine(testMedicine);
        
        // Assert: Verify the results
        assertNotNull(result);
        assertEquals(testMedicine.getName(), result.getName());
        assertEquals(testMedicine.getQuantity(), result.getQuantity());
        
        // Verify that the repository save method was called
        verify(medicineRepository, times(1)).save(testMedicine);
    }
    
    /**
     * Test adding a null medicine (should throw exception)
     */
    @Test
    void testAddMedicine_NullMedicine() {
        // Act & Assert: Verify that exception is thrown
        assertThrows(IllegalArgumentException.class, () -> {
            medicineService.addMedicine(null);
        });
        
        // Verify that repository save was never called
        verify(medicineRepository, never()).save(any(Medicine.class));
    }
    
    /**
     * Test adding a medicine with expiry date in the past
     */
    @Test
    void testAddMedicine_ExpiredDate() {
        // Arrange: Set expiry date to past
        testMedicine.setExpiryDate(LocalDate.now().minusDays(1));
        
        // Act & Assert: Verify that exception is thrown
        assertThrows(IllegalArgumentException.class, () -> {
            medicineService.addMedicine(testMedicine);
        });
        
        // Verify that repository save was never called
        verify(medicineRepository, never()).save(any(Medicine.class));
    }
    
    /**
     * Test getting medicines by user ID
     */
    @Test
    void testGetAllMedicinesByUserId_Success() {
        // Arrange: Set up mock data
        List<Medicine> medicines = Arrays.asList(testMedicine);
        when(medicineRepository.findByUserId(1L)).thenReturn(medicines);
        
        // Act: Call the method under test
        List<Medicine> result = medicineService.getAllMedicinesByUserId(1L);
        
        // Assert: Verify the results
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testMedicine.getName(), result.get(0).getName());
        
        // Verify that the repository method was called
        verify(medicineRepository, times(1)).findByUserId(1L);
    }
    
    /**
     * Test getting medicine by ID
     */
    @Test
    void testGetMedicineById_Success() {
        // Arrange: Set up mock behavior
        when(medicineRepository.findById(1L)).thenReturn(Optional.of(testMedicine));
        
        // Act: Call the method under test
        Optional<Medicine> result = medicineService.getMedicineById(1L);
        
        // Assert: Verify the results
        assertTrue(result.isPresent());
        assertEquals(testMedicine.getName(), result.get().getName());
        
        // Verify that the repository method was called
        verify(medicineRepository, times(1)).findById(1L);
    }
    
    /**
     * Test getting medicine by ID when medicine doesn't exist
     */
    @Test
    void testGetMedicineById_NotFound() {
        // Arrange: Set up mock behavior to return empty
        when(medicineRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act: Call the method under test
        Optional<Medicine> result = medicineService.getMedicineById(1L);
        
        // Assert: Verify the results
        assertFalse(result.isPresent());
        
        // Verify that the repository method was called
        verify(medicineRepository, times(1)).findById(1L);
    }
    
    /**
     * Test deleting medicine successfully
     */
    @Test
    void testDeleteMedicine_Success() {
        // Arrange: Set up mock behavior
        when(medicineRepository.existsById(1L)).thenReturn(true);
        doNothing().when(medicineRepository).deleteById(1L);
        
        // Act: Call the method under test
        assertDoesNotThrow(() -> {
            medicineService.deleteMedicine(1L);
        });
        
        // Verify that the repository methods were called
        verify(medicineRepository, times(1)).existsById(1L);
        verify(medicineRepository, times(1)).deleteById(1L);
    }
    
    /**
     * Test deleting medicine that doesn't exist
     */
    @Test
    void testDeleteMedicine_NotFound() {
        // Arrange: Set up mock behavior
        when(medicineRepository.existsById(1L)).thenReturn(false);
        
        // Act & Assert: Verify that exception is thrown
        assertThrows(RuntimeException.class, () -> {
            medicineService.deleteMedicine(1L);
        });
        
        // Verify that existsById was called but deleteById was not
        verify(medicineRepository, times(1)).existsById(1L);
        verify(medicineRepository, never()).deleteById(1L);
    }
    
    /**
     * Test getting expired medicines
     */
    @Test
    void testGetExpiredMedicines() {
        // Arrange: Create expired medicine
        Medicine expiredMedicine = new Medicine();
        expiredMedicine.setId(2L);
        expiredMedicine.setName("Expired Medicine");
        expiredMedicine.setExpiryDate(LocalDate.now().minusDays(5));
        expiredMedicine.setUserId(1L);
        
        List<Medicine> expiredMedicines = Arrays.asList(expiredMedicine);
        when(medicineRepository.findByUserIdAndExpiryDateBefore(eq(1L), any(LocalDate.class)))
                .thenReturn(expiredMedicines);
        
        // Act: Call the method under test
        List<Medicine> result = medicineService.getExpiredMedicines(1L);
        
        // Assert: Verify the results
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Expired Medicine", result.get(0).getName());
        
        // Verify that the repository method was called
        verify(medicineRepository, times(1)).findByUserIdAndExpiryDateBefore(eq(1L), any(LocalDate.class));
    }
}