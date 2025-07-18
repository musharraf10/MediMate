# MediMate - Medicine Expiry & Stock Reminder Application

## Overview
MediMate is a Spring Boot application that helps users track their medicine inventory, monitor expiry dates, and receive reminders for expired or low-stock medicines.

## Features
- **Medicine Management**: Add, update, delete, and view medicines
- **Expiry Tracking**: Track medicine expiry dates and get notifications
- **Stock Monitoring**: Monitor medicine quantities and low-stock alerts
- **Scheduled Tasks**: Automatic daily checks for expired medicines
- **REST API**: Complete REST API with proper error handling
- **Database Integration**: MySQL database with JPA/Hibernate

## Technology Stack
- **Framework**: Spring Boot 3.2.0
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA with Hibernate
- **Build Tool**: Maven
- **Java Version**: 17
- **Testing**: JUnit 5, Mockito

## Project Structure
```
src/
├── main/
│   ├── java/com/medimate/
│   │   ├── MediMateApplication.java          # Main application class
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
│       ├── application.properties            # Configuration
│       └── database-schema.sql              # Database schema
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

## Development Notes
- The application uses Spring Boot's auto-configuration
- Database schema is automatically created/updated using Hibernate
- All endpoints return JSON responses
- Input validation is implemented using Bean Validation
- Scheduled tasks run automatically when the application starts

## Future Enhancements
- User authentication and authorization
- Email/SMS notifications for expired medicines
- Mobile app integration
- Dashboard with charts and statistics
- Barcode scanning for medicine entry
- Integration with pharmacy systems

## License
This project is for educational purposes and demonstrates Spring Boot best practices.