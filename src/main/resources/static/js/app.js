/**
 * MediMate Frontend Application
 * This file contains all the JavaScript functionality for the MediMate web application
 */

// API Configuration
const API_BASE_URL = 'http://localhost:8080/api';

// Global Variables
let currentUser = 1;
let currentSection = 'dashboard';
let medicines = [];
let expiredMedicines = [];
let expiringSoonMedicines = [];
let lowStockMedicines = [];

// DOM Elements
const userSelect = document.getElementById('userSelect');
const navButtons = document.querySelectorAll('.nav-btn');
const sections = document.querySelectorAll('.section');
const addMedicineBtn = document.getElementById('addMedicineBtn');
const addMedicineModal = document.getElementById('addMedicineModal');
const editMedicineModal = document.getElementById('editMedicineModal');
const addMedicineForm = document.getElementById('addMedicineForm');
const editMedicineForm = document.getElementById('editMedicineForm');
const searchInput = document.getElementById('searchInput');
const loadingSpinner = document.getElementById('loadingSpinner');

// Initialize Application
document.addEventListener('DOMContentLoaded', function() {
    console.log('MediMate Application Starting...');
    initializeEventListeners();
    loadDashboard();
    setMinDate();
});

/**
 * Initialize all event listeners
 */
function initializeEventListeners() {
    // User selection
    userSelect.addEventListener('change', function() {
        currentUser = parseInt(this.value);
        console.log('User changed to:', currentUser);
        refreshCurrentSection();
    });

    // Navigation
    navButtons.forEach(btn => {
        btn.addEventListener('click', function() {
            const section = this.dataset.section;
            switchSection(section);
        });
    });

    // Add Medicine Modal
    addMedicineBtn.addEventListener('click', () => openAddMedicineModal());
    document.getElementById('closeModal').addEventListener('click', () => closeAddMedicineModal());
    document.getElementById('cancelBtn').addEventListener('click', () => closeAddMedicineModal());
    
    // Edit Medicine Modal
    document.getElementById('closeEditModal').addEventListener('click', () => closeEditMedicineModal());
    document.getElementById('cancelEditBtn').addEventListener('click', () => closeEditMedicineModal());

    // Forms
    addMedicineForm.addEventListener('submit', handleAddMedicine);
    editMedicineForm.addEventListener('submit', handleEditMedicine);

    // Search
    searchInput.addEventListener('input', handleSearch);

    // Alert tabs
    document.querySelectorAll('.alert-tab').forEach(tab => {
        tab.addEventListener('click', function() {
            switchAlertTab(this.dataset.alert);
        });
    });

    // Close modals when clicking outside
    window.addEventListener('click', function(event) {
        if (event.target === addMedicineModal) {
            closeAddMedicineModal();
        }
        if (event.target === editMedicineModal) {
            closeEditMedicineModal();
        }
    });
}

/**
 * Switch between different sections of the application
 */
function switchSection(sectionName) {
    // Update navigation
    navButtons.forEach(btn => btn.classList.remove('active'));
    document.querySelector(`[data-section="${sectionName}"]`).classList.add('active');

    // Update sections
    sections.forEach(section => section.classList.remove('active'));
    document.getElementById(sectionName).classList.add('active');

    currentSection = sectionName;

    // Load section data
    switch(sectionName) {
        case 'dashboard':
            loadDashboard();
            break;
        case 'medicines':
            loadMedicines();
            break;
        case 'alerts':
            loadAlerts();
            break;
    }
}

/**
 * Refresh the current section data
 */
function refreshCurrentSection() {
    switchSection(currentSection);
}

/**
 * Load dashboard data and statistics
 */
async function loadDashboard() {
    console.log('Loading dashboard for user:', currentUser);
    showLoading();

    try {
        // Load all medicines for statistics
        await loadAllMedicines();
        await loadExpiredMedicines();
        await loadExpiringSoonMedicines();
        await loadLowStockMedicines();

        // Update statistics
        updateDashboardStats();
        
        // Show recent medicines
        displayRecentMedicines();

        showToast('Dashboard loaded successfully', 'success');
    } catch (error) {
        console.error('Error loading dashboard:', error);
        showToast('Error loading dashboard', 'error');
    } finally {
        hideLoading();
    }
}

/**
 * Update dashboard statistics
 */
function updateDashboardStats() {
    document.getElementById('totalMedicines').textContent = medicines.length;
    document.getElementById('expiredCount').textContent = expiredMedicines.length;
    document.getElementById('expiringSoonCount').textContent = expiringSoonMedicines.length;
    document.getElementById('lowStockCount').textContent = lowStockMedicines.length;
}

/**
 * Display recent medicines on dashboard
 */
function displayRecentMedicines() {
    const recentMedicinesList = document.getElementById('recentMedicinesList');
    
    if (medicines.length === 0) {
        recentMedicinesList.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-pills"></i>
                <h3>No medicines found</h3>
                <p>Add your first medicine to get started</p>
            </div>
        `;
        return;
    }

    // Sort by added date and take first 5
    const recentMedicines = medicines
        .sort((a, b) => new Date(b.addedDate) - new Date(a.addedDate))
        .slice(0, 5);

    recentMedicinesList.innerHTML = recentMedicines.map(medicine => `
        <div class="medicine-list-item">
            <div class="medicine-list-info">
                <h4>${medicine.name}</h4>
                <p>Quantity: ${medicine.quantity} | Expires: ${formatDate(medicine.expiryDate)}</p>
            </div>
            <div class="medicine-list-status">
                ${getMedicineStatusBadge(medicine)}
            </div>
        </div>
    `).join('');
}

/**
 * Load all medicines for the current user
 */
async function loadAllMedicines() {
    try {
        const response = await fetch(`${API_BASE_URL}/medicines?userId=${currentUser}`);
        if (!response.ok) throw new Error('Failed to fetch medicines');
        medicines = await response.json();
        console.log('Loaded medicines:', medicines.length);
    } catch (error) {
        console.error('Error loading medicines:', error);
        medicines = [];
        throw error;
    }
}

/**
 * Load medicines and display them
 */
async function loadMedicines() {
    console.log('Loading medicines for user:', currentUser);
    showLoading();

    try {
        await loadAllMedicines();
        displayMedicines(medicines);
        showToast('Medicines loaded successfully', 'success');
    } catch (error) {
        console.error('Error loading medicines:', error);
        showToast('Error loading medicines', 'error');
        displayMedicines([]);
    } finally {
        hideLoading();
    }
}

/**
 * Display medicines in the grid
 */
function displayMedicines(medicinesToDisplay) {
    const medicinesGrid = document.getElementById('medicinesGrid');
    
    if (medicinesToDisplay.length === 0) {
        medicinesGrid.innerHTML = `
            <div class="empty-state" style="grid-column: 1 / -1;">
                <i class="fas fa-pills"></i>
                <h3>No medicines found</h3>
                <p>Add your first medicine or try a different search</p>
            </div>
        `;
        return;
    }

    medicinesGrid.innerHTML = medicinesToDisplay.map(medicine => `
        <div class="medicine-card ${getMedicineCardClass(medicine)}">
            <div class="medicine-header">
                <h3 class="medicine-name">${medicine.name}</h3>
                <div class="medicine-actions">
                    <button class="action-btn edit-btn" onclick="openEditMedicineModal(${medicine.id})">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="action-btn delete-btn" onclick="deleteMedicine(${medicine.id})">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </div>
            <div class="medicine-info">
                <div class="info-row">
                    <span class="info-label">Quantity:</span>
                    <span class="quantity-badge ${medicine.quantity < 10 ? 'low' : ''}">${medicine.quantity}</span>
                </div>
                <div class="info-row">
                    <span class="info-label">Expiry Date:</span>
                    <span class="expiry-badge ${getExpiryBadgeClass(medicine.expiryDate)}">${formatDate(medicine.expiryDate)}</span>
                </div>
                <div class="info-row">
                    <span class="info-label">Added:</span>
                    <span class="info-value">${formatDate(medicine.addedDate)}</span>
                </div>
            </div>
        </div>
    `).join('');
}

/**
 * Get CSS class for medicine card based on status
 */
function getMedicineCardClass(medicine) {
    const today = new Date();
    const expiryDate = new Date(medicine.expiryDate);
    const daysUntilExpiry = Math.ceil((expiryDate - today) / (1000 * 60 * 60 * 24));

    if (daysUntilExpiry < 0) return 'expired';
    if (daysUntilExpiry <= 30) return 'expiring-soon';
    if (medicine.quantity < 10) return 'low-stock';
    return '';
}

/**
 * Get CSS class for expiry badge
 */
function getExpiryBadgeClass(expiryDate) {
    const today = new Date();
    const expiry = new Date(expiryDate);
    const daysUntilExpiry = Math.ceil((expiry - today) / (1000 * 60 * 60 * 24));

    if (daysUntilExpiry < 0) return 'expired';
    if (daysUntilExpiry <= 30) return 'expiring-soon';
    return 'good';
}

/**
 * Get status badge for medicine
 */
function getMedicineStatusBadge(medicine) {
    const today = new Date();
    const expiryDate = new Date(medicine.expiryDate);
    const daysUntilExpiry = Math.ceil((expiryDate - today) / (1000 * 60 * 60 * 24));

    if (daysUntilExpiry < 0) {
        return '<span class="alert-status expired">Expired</span>';
    }
    if (daysUntilExpiry <= 30) {
        return '<span class="alert-status expiring">Expiring Soon</span>';
    }
    if (medicine.quantity < 10) {
        return '<span class="alert-status low-stock">Low Stock</span>';
    }
    return '<span class="alert-status good">Good</span>';
}

/**
 * Handle search functionality
 */
function handleSearch() {
    const searchTerm = searchInput.value.toLowerCase().trim();
    
    if (searchTerm === '') {
        displayMedicines(medicines);
        return;
    }

    const filteredMedicines = medicines.filter(medicine =>
        medicine.name.toLowerCase().includes(searchTerm)
    );

    displayMedicines(filteredMedicines);
}

/**
 * Load alerts data
 */
async function loadAlerts() {
    console.log('Loading alerts for user:', currentUser);
    showLoading();

    try {
        await loadExpiredMedicines();
        await loadExpiringSoonMedicines();
        await loadLowStockMedicines();

        displayAlerts();
        showToast('Alerts loaded successfully', 'success');
    } catch (error) {
        console.error('Error loading alerts:', error);
        showToast('Error loading alerts', 'error');
    } finally {
        hideLoading();
    }
}

/**
 * Load expired medicines
 */
async function loadExpiredMedicines() {
    try {
        const response = await fetch(`${API_BASE_URL}/medicines/expired?userId=${currentUser}`);
        if (!response.ok) throw new Error('Failed to fetch expired medicines');
        expiredMedicines = await response.json();
        console.log('Loaded expired medicines:', expiredMedicines.length);
    } catch (error) {
        console.error('Error loading expired medicines:', error);
        expiredMedicines = [];
        throw error;
    }
}

/**
 * Load medicines expiring soon
 */
async function loadExpiringSoonMedicines() {
    try {
        const response = await fetch(`${API_BASE_URL}/medicines/expiring-soon?userId=${currentUser}`);
        if (!response.ok) throw new Error('Failed to fetch expiring soon medicines');
        expiringSoonMedicines = await response.json();
        console.log('Loaded expiring soon medicines:', expiringSoonMedicines.length);
    } catch (error) {
        console.error('Error loading expiring soon medicines:', error);
        expiringSoonMedicines = [];
        throw error;
    }
}

/**
 * Load low stock medicines
 */
async function loadLowStockMedicines() {
    try {
        const response = await fetch(`${API_BASE_URL}/medicines/low-stock?userId=${currentUser}&threshold=10`);
        if (!response.ok) throw new Error('Failed to fetch low stock medicines');
        lowStockMedicines = await response.json();
        console.log('Loaded low stock medicines:', lowStockMedicines.length);
    } catch (error) {
        console.error('Error loading low stock medicines:', error);
        lowStockMedicines = [];
        throw error;
    }
}

/**
 * Display alerts in their respective tabs
 */
function displayAlerts() {
    displayExpiredAlerts();
    displayExpiringSoonAlerts();
    displayLowStockAlerts();
}

/**
 * Display expired medicines alerts
 */
function displayExpiredAlerts() {
    const expiredAlerts = document.getElementById('expiredAlerts');
    
    if (expiredMedicines.length === 0) {
        expiredAlerts.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-check-circle"></i>
                <h3>No expired medicines</h3>
                <p>All your medicines are still valid!</p>
            </div>
        `;
        return;
    }

    expiredAlerts.innerHTML = expiredMedicines.map(medicine => {
        const daysExpired = Math.abs(Math.ceil((new Date() - new Date(medicine.expiryDate)) / (1000 * 60 * 60 * 24)));
        return `
            <div class="alert-item">
                <div class="alert-info">
                    <h4>${medicine.name}</h4>
                    <p>Expired ${daysExpired} days ago</p>
                    <p>Quantity: ${medicine.quantity}</p>
                    <p>Expiry Date: ${formatDate(medicine.expiryDate)}</p>
                </div>
                <div class="alert-status expired">Expired</div>
            </div>
        `;
    }).join('');
}

/**
 * Display expiring soon medicines alerts
 */
function displayExpiringSoonAlerts() {
    const expiringAlerts = document.getElementById('expiringAlerts');
    
    if (expiringSoonMedicines.length === 0) {
        expiringAlerts.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-check-circle"></i>
                <h3>No medicines expiring soon</h3>
                <p>Your medicines have good expiry dates!</p>
            </div>
        `;
        return;
    }

    expiringAlerts.innerHTML = expiringSoonMedicines.map(medicine => {
        const daysUntilExpiry = Math.ceil((new Date(medicine.expiryDate) - new Date()) / (1000 * 60 * 60 * 24));
        return `
            <div class="alert-item">
                <div class="alert-info">
                    <h4>${medicine.name}</h4>
                    <p>Expires in ${daysUntilExpiry} days</p>
                    <p>Quantity: ${medicine.quantity}</p>
                    <p>Expiry Date: ${formatDate(medicine.expiryDate)}</p>
                </div>
                <div class="alert-status expiring">Expiring Soon</div>
            </div>
        `;
    }).join('');
}

/**
 * Display low stock medicines alerts
 */
function displayLowStockAlerts() {
    const lowstockAlerts = document.getElementById('lowstockAlerts');
    
    if (lowStockMedicines.length === 0) {
        lowstockAlerts.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-check-circle"></i>
                <h3>No low stock medicines</h3>
                <p>All your medicines have sufficient stock!</p>
            </div>
        `;
        return;
    }

    lowstockAlerts.innerHTML = lowStockMedicines.map(medicine => `
        <div class="alert-item">
            <div class="alert-info">
                <h4>${medicine.name}</h4>
                <p>Only ${medicine.quantity} left in stock</p>
                <p>Expiry Date: ${formatDate(medicine.expiryDate)}</p>
            </div>
            <div class="alert-status low-stock">Low Stock</div>
        </div>
    `).join('');
}

/**
 * Switch between alert tabs
 */
function switchAlertTab(alertType) {
    // Update tab buttons
    document.querySelectorAll('.alert-tab').forEach(tab => tab.classList.remove('active'));
    document.querySelector(`[data-alert="${alertType}"]`).classList.add('active');

    // Update alert lists
    document.querySelectorAll('.alert-list').forEach(list => list.classList.remove('active'));
    
    switch(alertType) {
        case 'expired':
            document.getElementById('expiredAlerts').classList.add('active');
            break;
        case 'expiring':
            document.getElementById('expiringAlerts').classList.add('active');
            break;
        case 'lowstock':
            document.getElementById('lowstockAlerts').classList.add('active');
            break;
    }
}

/**
 * Open add medicine modal
 */
function openAddMedicineModal() {
    addMedicineModal.classList.add('active');
    document.getElementById('medicineName').focus();
}

/**
 * Close add medicine modal
 */
function closeAddMedicineModal() {
    addMedicineModal.classList.remove('active');
    addMedicineForm.reset();
}

/**
 * Open edit medicine modal
 */
async function openEditMedicineModal(medicineId) {
    try {
        showLoading();
        const response = await fetch(`${API_BASE_URL}/medicines/${medicineId}`);
        if (!response.ok) throw new Error('Failed to fetch medicine details');
        
        const medicine = await response.json();
        
        // Populate form
        document.getElementById('editMedicineId').value = medicine.id;
        document.getElementById('editMedicineName').value = medicine.name;
        document.getElementById('editMedicineQuantity').value = medicine.quantity;
        document.getElementById('editMedicineExpiry').value = medicine.expiryDate;
        
        editMedicineModal.classList.add('active');
        document.getElementById('editMedicineName').focus();
        
    } catch (error) {
        console.error('Error loading medicine for edit:', error);
        showToast('Error loading medicine details', 'error');
    } finally {
        hideLoading();
    }
}

/**
 * Close edit medicine modal
 */
function closeEditMedicineModal() {
    editMedicineModal.classList.remove('active');
    editMedicineForm.reset();
}

/**
 * Handle add medicine form submission
 */
async function handleAddMedicine(event) {
    event.preventDefault();
    
    const formData = {
        name: document.getElementById('medicineName').value.trim(),
        quantity: parseInt(document.getElementById('medicineQuantity').value),
        expiryDate: document.getElementById('medicineExpiry').value,
        userId: currentUser
    };

    // Validation
    if (!formData.name || formData.quantity < 0 || !formData.expiryDate) {
        showToast('Please fill all required fields correctly', 'error');
        return;
    }

    // Check if expiry date is in the future
    const today = new Date();
    const expiryDate = new Date(formData.expiryDate);
    if (expiryDate <= today) {
        showToast('Expiry date must be in the future', 'error');
        return;
    }

    try {
        showLoading();
        
        const response = await fetch(`${API_BASE_URL}/medicines`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData)
        });

        if (!response.ok) {
            const errorData = await response.text();
            throw new Error(errorData || 'Failed to add medicine');
        }

        const newMedicine = await response.json();
        console.log('Medicine added successfully:', newMedicine);
        
        closeAddMedicineModal();
        showToast('Medicine added successfully!', 'success');
        
        // Refresh current section
        refreshCurrentSection();
        
    } catch (error) {
        console.error('Error adding medicine:', error);
        showToast('Error adding medicine: ' + error.message, 'error');
    } finally {
        hideLoading();
    }
}

/**
 * Handle edit medicine form submission
 */
async function handleEditMedicine(event) {
    event.preventDefault();
    
    const medicineId = document.getElementById('editMedicineId').value;
    const formData = {
        name: document.getElementById('editMedicineName').value.trim(),
        quantity: parseInt(document.getElementById('editMedicineQuantity').value),
        expiryDate: document.getElementById('editMedicineExpiry').value,
        userId: currentUser
    };

    // Validation
    if (!formData.name || formData.quantity < 0 || !formData.expiryDate) {
        showToast('Please fill all required fields correctly', 'error');
        return;
    }

    try {
        showLoading();
        
        const response = await fetch(`${API_BASE_URL}/medicines/${medicineId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData)
        });

        if (!response.ok) {
            const errorData = await response.text();
            throw new Error(errorData || 'Failed to update medicine');
        }

        const updatedMedicine = await response.json();
        console.log('Medicine updated successfully:', updatedMedicine);
        
        closeEditMedicineModal();
        showToast('Medicine updated successfully!', 'success');
        
        // Refresh current section
        refreshCurrentSection();
        
    } catch (error) {
        console.error('Error updating medicine:', error);
        showToast('Error updating medicine: ' + error.message, 'error');
    } finally {
        hideLoading();
    }
}

/**
 * Delete a medicine
 */
async function deleteMedicine(medicineId) {
    if (!confirm('Are you sure you want to delete this medicine? This action cannot be undone.')) {
        return;
    }

    try {
        showLoading();
        
        const response = await fetch(`${API_BASE_URL}/medicines/${medicineId}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            const errorData = await response.text();
            throw new Error(errorData || 'Failed to delete medicine');
        }

        console.log('Medicine deleted successfully');
        showToast('Medicine deleted successfully!', 'success');
        
        // Refresh current section
        refreshCurrentSection();
        
    } catch (error) {
        console.error('Error deleting medicine:', error);
        showToast('Error deleting medicine: ' + error.message, 'error');
    } finally {
        hideLoading();
    }
}

/**
 * Set minimum date for date inputs (today)
 */
function setMinDate() {
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('medicineExpiry').setAttribute('min', today);
    document.getElementById('editMedicineExpiry').setAttribute('min', today);
}

/**
 * Format date for display
 */
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

/**
 * Show loading spinner
 */
function showLoading() {
    loadingSpinner.classList.add('active');
}

/**
 * Hide loading spinner
 */
function hideLoading() {
    loadingSpinner.classList.remove('active');
}

/**
 * Show toast notification
 */
function showToast(message, type = 'info') {
    const toastContainer = document.getElementById('toastContainer');
    
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    
    const icon = getToastIcon(type);
    toast.innerHTML = `
        <i class="${icon}"></i>
        <span>${message}</span>
    `;
    
    toastContainer.appendChild(toast);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        toast.style.animation = 'toastSlideOut 0.3s ease forwards';
        setTimeout(() => {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
            }
        }, 300);
    }, 5000);
}

/**
 * Get icon for toast notification
 */
function getToastIcon(type) {
    switch(type) {
        case 'success': return 'fas fa-check-circle';
        case 'error': return 'fas fa-exclamation-circle';
        case 'warning': return 'fas fa-exclamation-triangle';
        default: return 'fas fa-info-circle';
    }
}

// Add CSS for toast slide out animation
const style = document.createElement('style');
style.textContent = `
    @keyframes toastSlideOut {
        from { transform: translateX(0); opacity: 1; }
        to { transform: translateX(100%); opacity: 0; }
    }
`;
document.head.appendChild(style);

// Global error handler for unhandled promise rejections
window.addEventListener('unhandledrejection', function(event) {
    console.error('Unhandled promise rejection:', event.reason);
    showToast('An unexpected error occurred', 'error');
});

// Global error handler
window.addEventListener('error', function(event) {
    console.error('Global error:', event.error);
    showToast('An unexpected error occurred', 'error');
});

console.log('MediMate Application Initialized Successfully!');