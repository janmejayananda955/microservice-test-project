# Authentication Microservice

A production-grade, enterprise-level authentication microservice built with Spring Boot 3.5.14 and Java 21, implementing JWT-based authentication with refresh token rotation, following SOLID principles and industry best practices.

## 🚀 Project Overview

This authentication service provides a robust, scalable, and secure authentication solution for microservices architecture. It implements stateless authentication using JWT tokens with a sophisticated refresh token rotation mechanism to enhance security while maintaining excellent user experience.

## 🛠️ Technology Stack

### Core Technologies
- **Java 21** - Latest LTS version with modern language features
- **Spring Boot 3.5.14** - Enterprise-grade application framework
- **Spring Security 6** - Comprehensive security framework
- **Spring Data JPA** - Database abstraction layer
- **PostgreSQL** - Relational database for persistent storage

### Security & Authentication
- **JWT (JJWT 0.12.6)** - JSON Web Token implementation for stateless authentication
- **BCrypt** - Password hashing algorithm
- **SHA-256** - Token hashing for database storage
- **Apache Commons Codec** - Cryptographic utilities

### Development Tools
- **Lombok** - Reduce boilerplate code
- **Maven** - Dependency management and build tool
- **Jakarta Validation** - Request validation

## 🏗️ Architecture & SOLID Principles

### SOLID Principles Implementation

#### **Single Responsibility Principle (SRP)**
Each class has a single, well-defined responsibility:
- `AuthController` - Handles HTTP request/response
- `RegisterService`/`RegisterServiceImpl` - User registration logic
- `AuthenticationService`/`AuthenticationServiceImpl` - Login orchestration
- `RefreshTokenService`/`RefreshTokenServiceImpl` - Token refresh logic
- `LogoutService`/`LogoutServiceImpl` - Session management
- `LoginProvider` implementations - Specific authentication strategies
- `UserRepository`/`RefreshSessionRepository` - Data access layer
- `JwtFilterChain` - JWT token validation
- `AuthUtil` - JWT token generation and cookie management

#### **Open/Closed Principle (OCP)**
- `LoginProvider` interface allows extension for new authentication strategies without modifying existing code
- Strategy pattern for supporting multiple login types (email/password, OAuth, etc.)
- New login providers can be added by implementing `LoginProvider` interface - no changes needed to existing code

#### **Liskov Substitution Principle (LSP)**
- `CustomUserDetails` implements Spring Security's `UserDetails` interface seamlessly
- Repository interfaces extend Spring Data JPA interfaces

#### **Interface Segregation Principle (ISP)**
- `RegisterService` - Only registration methods
- `AuthenticationService` - Only login orchestration
- `RefreshTokenService` - Only token refresh methods
- `LogoutService` - Only logout methods
- `LoginProvider` - Only authentication strategy methods
- Repository interfaces are focused on specific entity operations

#### **Dependency Inversion Principle (DIP)**
- Service layer depends on abstractions (interfaces) rather than concrete implementations
- `AuthenticationServiceImpl` depends on `LoginProvider` interface, not specific implementations
- Constructor injection for all dependencies
- Spring automatically injects all `LoginProvider` implementations as a Map

## 🔐 Security Features

### JWT Token Implementation
- **Access Token**: Short-lived (15 minutes) token for API authentication
- **Refresh Token**: Long-lived (7 days) token for token rotation
- **Token Rotation**: Refresh tokens are rotated on each use to prevent token replay attacks
- **Dual Secret Keys**: Separate secrets for access and refresh tokens

### Refresh Token Rotation Strategy
```
Login → Access Token (15min) + Refresh Token (7 days)
         ↓
Refresh Request → Validate old refresh token
                  ↓
                  Generate new access token + new refresh token
                  ↓
                  Invalidate old refresh token in database
                  ↓
                  Return new tokens
```

### Security Enhancements
- **SHA-256 Hashing**: Refresh tokens are hashed before database storage
- **HttpOnly Cookies**: Refresh tokens stored in HttpOnly, Secure cookies to prevent XSS
- **SameSite Protection**: Lax policy to prevent CSRF attacks
- **Device Limit**: Maximum 3 active sessions per user
- **Session Tracking**: IP address and User-Agent logging for each session
- **Attack Detection**: Automatic detection and logging of revoked token reuse attempts
- **Automatic Revocation**: All sessions revoked when attack detected

### Database Security
- **Indexed Columns**: Email and refresh token columns indexed for performance
- **UUID Primary Keys**: Non-guessable identifiers
- **Audit Timestamps**: Created and updated timestamps for all entities
- **Soft Delete**: Revoked flag instead of physical deletion for security audit

## 🎯 Strategy Pattern Implementation

The authentication service uses the **Strategy Pattern** to support multiple login types while maintaining clean architecture and following SOLID principles.

### Architecture Overview

```
AuthController
    ↓
AuthenticationService (Orchestrator)
    ↓
Map<LoginType, LoginProvider> (Strategy Selection)
    ↓
LoginProvider Implementation (Strategy)
    ↓
EmailPasswordLoginProvider (Concrete Strategy)
```

### Components

#### **LoginProvider Interface**
```java
@Component
public interface LoginProvider {
    LoginType supportType();
    ApiResponse<?> login(LoginRequestDto loginRequest);
}
```
- Defines the contract for all authentication strategies
- Annotated with `@Component` for Spring auto-detection

#### **LoginType Enum**
```java
public enum LoginType {
    EMAIL_PASSWORD,
    // Future: OAUTH2_GOOGLE, OAUTH2_GITHUB, etc.
}
```
- Defines available authentication types
- Used as key in provider map

#### **EmailPasswordLoginProvider**
- Implements `LoginProvider` for email/password authentication
- Handles Spring Security authentication
- Manages JWT token generation
- Enforces device limits

#### **AuthenticationServiceImpl**
- Injects all `LoginProvider` implementations via constructor
- Builds `Map<LoginType, LoginProvider>` for strategy lookup
- Selects appropriate provider based on `LoginType` from request
- Delegates authentication to selected strategy

### Benefits

1. **Extensibility**: Add new login types (OAuth2, SAML, etc.) by creating new providers
2. **Maintainability**: Each authentication method isolated in its own class
3. **Testability**: Each strategy can be tested independently
4. **Clean Code**: No conditional logic for different login types
5. **Open/Closed**: Open for extension, closed for modification

### Adding a New Login Provider

To add a new authentication method (e.g., OAuth2 Google):

1. Add new enum value:
```java
public enum LoginType {
    EMAIL_PASSWORD,
    OAUTH2_GOOGLE,
}
```

2. Create new provider:
```java
@Component
public class OAuth2GoogleLoginProvider implements LoginProvider {
    @Override
    public LoginType supportType() {
        return LoginType.OAUTH2_GOOGLE;
    }

    @Override
    public ApiResponse<?> login(LoginRequestDto loginRequest) {
        // OAuth2 implementation
    }
}
```

3. Spring automatically registers it - no other changes needed!

## 📁 Production-Grade File Structure

```
authService/
├── src/
│   ├── main/
│   │   ├── java/com/authService/
│   │   │   ├── AuthServiceApplication.java          # Application entry point
│   │   │   ├── config/                              # Configuration classes
│   │   │   │   ├── ApplicationConfig.java           # Bean configurations
│   │   │   │   └── WebSecurityConfig.java          # Security filter chain
│   │   │   ├── controller/                          # REST API controllers
│   │   │   │   └── AuthController.java              # Authentication endpoints
│   │   │   ├── dto/                                 # Data Transfer Objects
│   │   │   │   ├── LoginRequestDto.java            # Login request payload
│   │   │   │   └── RegisterRequestDto.java         # Registration request payload
│   │   │   ├── entity/                              # JPA Entities
│   │   │   │   ├── User.java                       # User entity
│   │   │   │   └── RefreshSession.java             # Refresh session entity
│   │   │   ├── exception/                           # Exception handling
│   │   │   │   ├── ApiResponse.java                # Standardized API response
│   │   │   │   ├── GlobalException.java            # Global exception handler
│   │   │   │   ├── InvalidCredentialsException.java # Custom exception
│   │   │   │   ├── ResourceAlreadyExistsException.java
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── repository/                          # Data access layer
│   │   │   │   ├── UserRepository.java             # User data operations
│   │   │   │   └── RefreshSessionRepository.java   # Session data operations
│   │   │   ├── security/                            # Security components
│   │   │   │   ├── AuthUtil.java                   # JWT utilities
│   │   │   │   ├── CustomUserDetails.java         # User details implementation
│   │   │   │   ├── CustomUserDetailsService.java   # User details service
│   │   │   │   ├── JwtAuthenticationEntryPoint.java # Authentication entry point
│   │   │   │   └── JwtFilterChain.java             # JWT filter
│   │   │   ├── service/                             # Business logic layer
│   │   │   │   ├── RegisterService.java            # Registration interface
│   │   │   │   ├── AuthenticationService.java      # Authentication interface
│   │   │   │   ├── RefreshTokenService.java        # Token refresh interface
│   │   │   │   ├── LogoutService.java              # Logout interface
│   │   │   │   ├── impl/
│   │   │   │   │   ├── RegisterServiceImpl.java    # Registration implementation
│   │   │   │   │   ├── AuthenticationServiceImpl.java # Authentication implementation
│   │   │   │   │   ├── RefreshTokenServiceImpl.java # Token refresh implementation
│   │   │   │   │   └── LogoutServiceImpl.java      # Logout implementation
│   │   │   │   └── strategy/                       # Strategy pattern
│   │   │   │       ├── LoginProvider.java          # Login provider interface
│   │   │   │       ├── EmailPasswordLoginProvider.java # Email/password strategy
│   │   │   │       └── type/
│   │   │   │           └── LoginType.java          # Login type enum
│   │   └── resources/
│   │       ├── application.yml                     # Application configuration
│   │       ├── static/                             # Static resources
│   │       └── templates/                          # Template files
│   └── test/                                        # Test directory
├── .env                                             # Environment variables
├── .gitignore                                      # Git ignore rules
├── pom.xml                                         # Maven configuration
└── HELP.md                                         # Project help documentation
```

## ✨ Features Implemented

### Authentication Flow
1. **User Registration**
   - Email uniqueness validation
   - Password encryption using BCrypt
   - User entity creation with audit timestamps

2. **User Login (Strategy Pattern)**
   - Client specifies login type in request (`EMAIL_PASSWORD`)
   - `AuthenticationService` selects appropriate `LoginProvider` strategy
   - `EmailPasswordLoginProvider` handles email/password authentication:
     - Spring Security authentication
     - JWT access token generation (15 min expiry)
     - JWT refresh token generation (7 days expiry)
     - Device/session limit enforcement (max 3 devices)
     - Refresh token storage in database (SHA-256 hashed)
     - IP address and User-Agent tracking
     - HttpOnly cookie setting for refresh token
   - Future login types (OAuth2, etc.) can be added without modifying existing code

3. **Token Refresh**
   - Refresh token validation
   - Token rotation mechanism
   - New access and refresh token generation
   - Old refresh token invalidation
   - Attack detection for revoked token reuse
   - Automatic session revocation on attack detection

4. **Logout (Single Device)**
   - Refresh token validation
   - Session revocation
   - Cookie clearing

5. **Logout All Devices**
   - User authentication verification
   - All sessions revocation for user
   - Cookie clearing

## 🔌 API Endpoints

### Base URL
```
http://localhost:8081/api
```

### Authentication Endpoints

#### Register User
```http
POST /v1/auth/register
Content-Type: application/json

{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "securePassword123"
}
```

#### Login
```http
POST /v1/auth/login
Content-Type: application/json

{
  "type": "EMAIL_PASSWORD",
  "email": "john@example.com",
  "password": "securePassword123"
}
```

**Response:**
- Access Token in response body
- Refresh Token in HttpOnly cookie

#### Refresh Token
```http
GET /v1/auth/refresh-token
Cookie: refreshToken=<refresh_token_value>
```

**Response:**
- New Access Token in response body
- New Refresh Token in HttpOnly cookie

#### Logout
```http
GET /v1/auth/logout
Cookie: refreshToken=<refresh_token_value>
```

#### Logout All Devices
```http
GET /v1/auth/logout-all
Cookie: refreshToken=<refresh_token_value>
```

## 🔧 Configuration

### Environment Variables
Create a `.env` file in the project root:
```env
JWT_ACCESS_SECRET=your-super-secret-access-key-min-256-bits
JWT_REFRESH_SECRET=your-super-secret-refresh-key-min-256-bits
```

### Database Configuration
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/auth_db
    username: postgres
    password: admin
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

### Server Configuration
```yaml
server:
  port: 8081
  servlet:
    context-path: /api
```

## 🏃 Running the Application

### Prerequisites
- Java 21 or higher
- Maven 3.6+
- PostgreSQL 12+

### Steps
1. Clone the repository
2. Configure database in `application.yml`
3. Set environment variables in `.env` file
4. Run the application:
```bash
./mvnw spring-boot:run
```

Or build and run:
```bash
./mvnw clean package
java -jar target/authService-0.0.1-SNAPSHOT.jar
```

## 📊 Database Schema

### Users Table
```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    full_name VARCHAR NOT NULL,
    email VARCHAR UNIQUE NOT NULL,
    password VARCHAR NOT NULL,
    enabled BOOLEAN DEFAULT true,
    is_verified BOOLEAN DEFAULT false,
    is_account_non_locked BOOLEAN DEFAULT true,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
```

### Refresh Sessions Table
```sql
CREATE TABLE refresh_sessions (
    id UUID PRIMARY KEY,
    refresh_token VARCHAR UNIQUE NOT NULL,
    expires_at TIMESTAMP,
    revoked BOOLEAN DEFAULT false,
    ip_address VARCHAR NOT NULL,
    user_agent VARCHAR NOT NULL,
    user_id UUID,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_refresh_token ON refresh_sessions(refresh_token);
```

## 🔒 Security Best Practices Implemented

1. **Stateless Authentication**: No server-side session storage
2. **Token Rotation**: Prevents token replay attacks
3. **Short-lived Access Tokens**: 15-minute expiry limits exposure
4. **Secure Cookie Storage**: HttpOnly, Secure, SameSite attributes
5. **Token Hashing**: SHA-256 hashing before database storage
6. **Device Limit**: Prevents unlimited concurrent sessions
7. **Attack Detection**: Logs and responds to suspicious activity
8. **Input Validation**: Jakarta Validation on all DTOs
9. **Exception Handling**: Centralized exception handling with proper HTTP status codes
10. **CORS Configuration**: Configurable cross-origin resource sharing

## 🎯 Future Enhancements

- OAuth2/OIDC integration (Google, GitHub, etc.) - Strategy pattern ready
- Two-Factor Authentication (2FA)
- Email verification
- Password reset functionality
- Role-based access control (RBAC)
- Rate limiting
- Audit logging
- Redis integration for distributed caching
- Docker containerization
- Kubernetes deployment manifests

## 📝 License

This project is proprietary software. All rights reserved.

## 👤 Author

Built with enterprise-grade standards and best practices for production deployment.
