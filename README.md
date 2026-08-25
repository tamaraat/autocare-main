# AutoCare

AutoCare is a Spring Boot web application for managing cars, service appointments, service offers, users, roles, and spare parts.

The project consists of two independent Spring Boot applications:

- AutoCare Main Application
- Parts Service REST Microservice

The applications run independently on separate ports and use separate MySQL databases.

## Technology Stack

- Java 17
- Spring Boot
- Spring MVC
- Thymeleaf
- Spring Security
- Spring Data JPA
- Spring Validation
- Spring Cloud OpenFeign
- Spring Cache
- MySQL
- Maven
- JUnit 5
- Mockito
- MockMvc

## Application Architecture

## GitHub Repositories

Main application:

https://github.com/tamaraat/autocare-main

REST microservice:

https://github.com/tamaraat/parts-service

### Main Application

The main application runs on:

`http://localhost:8080`

It contains the core AutoCare functionality and the web interface.

### Parts Service

The REST microservice runs on:

`http://localhost:8081`

It manages spare parts and inventory.

The main application communicates with the Parts Service using Spring Cloud OpenFeign.

## Databases

The applications use separate MySQL databases.

Main application:

`autocare_main_db`

Parts microservice:

`autocare_parts_db`

Spring Data JPA is used for database access.

Domain entities use UUID identifiers.

## Main Domain Entities

### Car

Represents a vehicle owned by a registered customer.

### Appointment

Represents a service appointment for a customer's vehicle.

### ServiceOffer

Represents a service offered by AutoCare.

### User

Represents an application user with authentication and authorization information.

## Main Functionalities

### Cars

Authenticated users can:

- add a car
- edit their own car
- delete their own car
- view their cars

### Appointments

Authenticated users can:

- create service appointments
- view their appointments
- cancel appointments

Administrators can:

- view all appointments
- confirm appointments

### Service Offers

Visitors and authenticated users can view available services.

Administrators can:

- create service offers
- edit service offers
- enable or disable service offers

### User Profiles

Authenticated users can:

- view their profile
- edit their first and last name

### Role Management

Administrators can:

- view registered users
- promote customers to administrators
- change administrators back to customers

Administrators cannot change their own role through the role management page.

### Spare Parts

Administrators can manage spare parts through the main application.

Supported operations include:

- view spare parts
- create spare parts
- edit spare parts
- update stock
- delete spare parts

These operations are executed through the Parts Service using a Feign Client.

## Security

Spring Security provides authentication and authorization.

The application contains two roles:

- CUSTOMER
- ADMIN

The application contains:

- public endpoints
- authenticated endpoints
- administrator-only endpoints

Passwords are stored using BCrypt hashing.

CSRF protection remains enabled.

## Validation

Form input is validated using Jakarta Bean Validation.

Additional business validation is implemented in the service layer.

Examples include:

- duplicate registration number prevention
- valid vehicle production year
- required profile information
- valid service data
- valid appointment data

## Error Handling

The application provides centralized exception handling for invalid operations and missing resources.

Custom exceptions include errors for:

- missing cars
- missing appointments
- missing service offers

The application displays controlled error responses instead of relying on the default white-label error page.

## REST Microservice Integration

The main application uses Spring Cloud OpenFeign to communicate with the Parts Service.

The integration includes:

- GET spare parts
- POST spare part
- PUT spare part
- PUT stock quantity
- DELETE spare part

The Parts Service contains its own domain entity, repository, service layer, REST controller, validation, exception handling, caching, scheduling, logging, and tests.

## Caching and Scheduling

Caching and scheduled jobs are implemented in the Parts Service.

The Parts Service caches spare part retrieval and invalidates the cache when inventory data changes.

Scheduled jobs include:

- cron-based automatic low-stock restocking
- fixed-delay cache refresh

## Logging

Important domain operations contain log statements.

Examples include:

- creating cars
- editing cars
- deleting cars
- creating appointments
- changing appointment state
- managing services
- managing spare parts
- managing user profiles and roles

## Testing

The project includes:

- Unit tests
- Integration tests
- API/controller tests

Tests can be run with:

```bash
./mvnw test