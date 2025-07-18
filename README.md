# MediMate - Medicine Expiry & Stock Reminder Application

## Overview
MediMate is a full-stack Spring Boot application with a modern web frontend that helps users track their medicine inventory, monitor expiry dates, and receive reminders for expired or low-stock medicines.

## Features
### Backend Features
- **Medicine Management**: Add, update, delete, and view medicines
- **Expiry Tracking**: Track medicine expiry dates and get notifications
- **Stock Monitoring**: Monitor medicine quantities and low-stock alerts
- **Scheduled Tasks**: Automatic daily checks for expired medicines
- **REST API**: Complete REST API with proper error handling
- **Database Integration**: MySQL database with JPA/Hibernate
- **CORS Support**: Configured for frontend-backend communication

### Frontend Features
- **Modern Web Interface**: Responsive design with intuitive navigation
- **Dashboard**: Overview with statistics and recent medicines
- **Medicine Management**: Add, edit, delete medicines with modal forms
- **Search Functionality**: Real-time search through medicines
- **Alert System**: Visual alerts for expired, expiring soon, and low-stock medicines
- **Toast Notifications**: User feedback for all operations
- **Multi-user Support**: Switch between different users
- **Mobile Responsive**: Works on all device sizes

## Technology Stack
### Backend
- **Framework**: Spring Boot 3.2.0
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA with Hibernate
- **Build Tool**: Maven
- **Java Version**: 17
- **Testing**: JUnit 5, Mockito

### Frontend
- **HTML5**: Semantic markup
- **CSS3**: Modern styling with animations and responsive design
- **JavaScript**: Vanilla ES6+ for dynamic functionality
- **Font Awesome**: Icons and visual elements
- **Fetch API**: HTTP requests to backend
## Project Structure
```
src/
├── main/
│   ├── java/com/medimate/
│   │   ├── MediMateApplication.java          # Main application class
│   │   ├── config/
│   │   │   └── WebConfig.java                # CORS configuration
│   │   ├── controller/
│   │   │   └── MedicineController.java       # REST API endpoints
│   │   ├── service/
│   │   │   └── MedicineService.java          # Business logic
│   │   ├── repository/
│   │   │   └── MedicineRepository.java       # Data access layer
│   │   ├── entity/
│   │   │   └── Medicine.java                 # JPA entity
│   │   ├── dto/
│   │   │   └── ApiResponse.java              # Response wrapper
│   │   ├── exception/
│   │   │   └── GlobalExceptionHandler.java   # Global error handling
│   │   └── scheduler/
│   │       └── MedicineScheduler.java        # Scheduled tasks
│   └── resources/
│       ├── static/                           # Frontend files
│       │   ├── index.html                    # Main HTML page
│       │   ├── css/
│       │   │   └── styles.css                # Application styles
│       │   └── js/
│       │       └── app.js                    # Frontend JavaScript
│       ├── application.properties            # Configuration
│       └── database-schema.sql               # Database schema
└── test/
    └── java/com/medimate/
        ├── MediMateApplicationTests.java     # Integration tests
        └── service/
            └── MedicineServiceTest.java      # Unit tests
```

## Database Schema
The application uses a single `medicines` table with the following structure:
- `id`: Primary key (auto-increment)
- `name`: Medicine name (required)
- `quantity`: Stock quantity (required)
- `expiry_date`: Medicine expiry date (required)
- `added_date`: When the medicine was added (auto-generated)
- `user_id`: User who owns the medicine (required)

## API Endpoints

### Medicine Management
- `POST /api/medicines` - Add new medicine
- `GET /api/medicines?userId={userId}` - Get all medicines for a user
- `GET /api/medicines/{id}` - Get medicine by ID
- `PUT /api/medicines/{id}` - Update medicine
- `DELETE /api/medicines/{id}` - Delete medicine

### Special Queries
- `GET /api/medicines/expired?userId={userId}` - Get expired medicines
- `GET /api/medicines/expiring-soon?userId={userId}` - Get medicines expiring in 30 days
- `GET /api/medicines/low-stock?userId={userId}&threshold={threshold}` - Get low stock medicines
- `GET /api/medicines/search?userId={userId}&name={name}` - Search medicines by name

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher

### Database Setup
1. Install MySQL and create a database:
   ```sql
   CREATE DATABASE medimate_db;
   ```

2. Run the SQL script from `src/main/resources/database-schema.sql`

### Application Configuration
1. Update `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/medimate_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

### Running the Application
1. Clone the repository
2. Navigate to project directory
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```
4. The application will start on `http://localhost:8080`

### Accessing the Application
1. **Web Interface**: Open `http://localhost:8080` in your browser
2. **API Documentation**: API endpoints are available at `http://localhost:8080/api/medicines`

## Using the Web Interface

### Dashboard
- View statistics: total medicines, expired, expiring soon, low stock
- See recent medicines added to your inventory
- Quick overview of your medicine status

### Medicine Management
- **Add Medicine**: Click "Add Medicine" button, fill the form
- **Edit Medicine**: Click edit icon on any medicine card
- **Delete Medicine**: Click delete icon (with confirmation)
- **Search**: Use the search bar to find specific medicines

### Alerts
- **Expired**: View all expired medicines
- **Expiring Soon**: Medicines expiring within 30 days
- **Low Stock**: Medicines with quantity below 10

### User Management
- Switch between users using the dropdown in the header
- Each user has their own medicine inventory

## Testing the API

### Add a Medicine
```bash
curl -X POST http://localhost:8080/api/medicines \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Aspirin",
    "quantity": 50,
    "expiryDate": "2025-12-31",
    "userId": 1
  }'
```

### Get All Medicines for a User
```bash
curl -X GET "http://localhost:8080/api/medicines?userId=1"
```

### Get Expired Medicines
```bash
curl -X GET "http://localhost:8080/api/medicines/expired?userId=1"
```

### Get Low Stock Medicines
```bash
curl -X GET "http://localhost:8080/api/medicines/low-stock?userId=1&threshold=10"
```

## Frontend Features in Detail

### Responsive Design
- Mobile-first approach with breakpoints at 768px and 480px
- Flexible grid layouts that adapt to screen size
- Touch-friendly buttons and interactions

### User Experience
- Smooth animations and transitions
- Loading states for all operations
- Toast notifications for user feedback
- Modal dialogs for forms
- Intuitive navigation with active states

### Visual Design
- Modern gradient backgrounds
- Card-based layout for medicines
- Color-coded status indicators
- Professional typography and spacing
- Consistent iconography using Font Awesome

### Performance
- Efficient DOM manipulation
- Debounced search functionality
- Optimized API calls
- Minimal external dependencies

## Scheduled Tasks
The application includes automated scheduled tasks:
- **Daily at 9:00 AM**: Check and log expired medicines
- **Daily at 9:30 AM**: Check medicines expiring soon
- **Every hour**: System health check
- **Every 10 minutes**: Test task (for development)

## Error Handling
The application includes comprehensive error handling:
- Input validation with detailed error messages
- Global exception handling
- Standardized API response format
- Logging for debugging and monitoring

## Browser Support
- Chrome 60+
- Firefox 55+
- Safari 12+
- Edge 79+

## Development Notes
- The application uses Spring Boot's auto-configuration
- Database schema is automatically created/updated using Hibernate
- All endpoints return JSON responses
- Input validation is implemented using Bean Validation
- Scheduled tasks run automatically when the application starts
- Frontend uses modern JavaScript features (ES6+)
- CORS is configured to allow frontend-backend communication
- Static files are served from `src/main/resources/static`

## Future Enhancements
- User authentication and authorization
- Email/SMS notifications for expired medicines
- Mobile app integration
- Dashboard with charts and statistics
- Barcode scanning for medicine entry
- Integration with pharmacy systems
- Progressive Web App (PWA) features
- Dark mode theme
- Export/import functionality
- Medicine interaction warnings
- Prescription management

## License
This project is for educational purposes and demonstrates Spring Boot best practices.