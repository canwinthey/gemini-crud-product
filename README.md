# Gemini CRUD Product Application

This document provides a high-level overview and onboarding guide for new development team members joining the `gemini-crud-product` project.

## 1. Project Overview

This project is a simple Java Spring Boot application that demonstrates basic Create, Read, Update, and Delete (CRUD) operations for a `Product` entity. It provides a RESTful API for managing product information, uses an in-memory H2 database for data persistence, and includes server-side validation, global exception handling, testing, Dockerization, and a Jenkins CI/CD pipeline.

**Core Functionality:**
*   Manage product details (ID, Name, Description, Price).
*   Expose REST endpoints for CRUD operations.
*   Validate incoming product data.
*   Handle exceptions gracefully.

## 2. Technology Stack

*   **Language:** Java 17
*   **Framework:** Spring Boot 3.4.x
*   **Build Tool:** Apache Maven
*   **Database:** H2 Database (in-memory for development/testing)
*   **ORM:** Spring Data JPA / Hibernate
*   **API:** Spring Web (RESTful API)
*   **Validation:** Jakarta Bean Validation
*   **Utility:** Lombok (for boilerplate code reduction)
*   **Testing:** JUnit 5, Mockito, Spring Boot Test
*   **Code Coverage:** JaCoCo Maven Plugin
*   **Code Quality:** SonarQube Maven Plugin (configured but skipped by default)
*   **Containerization:** Docker
*   **CI/CD:** Jenkins Pipeline

## 3. Project Structure

The project follows a standard Maven and Spring Boot project structure:

```
.
├── .git/
├── .mvn/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── gemini/
│   │   │           └── product/
│   │   │               ├── controller/     # REST API controllers
│   │   │               ├── domain/         # Data Transfer Objects (DTOs)
│   │   │               ├── exception/      # Custom exceptions and global handler
│   │   │               ├── mapper/         # DTO to Entity mappers
│   │   │               ├── model/          # JPA Entities
│   │   │               ├── repository/     # Spring Data JPA repositories
│   │   │               ├── service/        # Business logic services
│   │   │               └── GeminiCrudProductApplication.java # Main Spring Boot application
│   │   └── resources/
│   │       ├── application.properties      # Spring Boot configuration
│   │       ├── data.sql                    # Initial data script for H2
│   │       └── schema.sql                  # Database schema creation script for H2
│   └── test/
│       └── java/
│           └── com/
│               └── gemini/
│                   └── product/            # Unit and integration tests
├── Dockerfile                              # Docker image definition
├── Jenkinsfile                             # Jenkins CI/CD pipeline definition
├── mvnw                                    # Maven Wrapper (Linux/macOS)
├── mvnw.cmd                                # Maven Wrapper (Windows)
├── pom.xml                                 # Project Object Model (Maven configuration)
└── README.md                               # This document
```

## 4. How to Get Started (Local Development)

### Prerequisites

*   **Java Development Kit (JDK):** Version 17 or higher.
*   **Apache Maven:** Version 3.x.x.
*   **Docker Desktop:** For running the application in a container.
*   **Git:** For version control.

### Building the Project

Navigate to the project root directory in your terminal and run:

```bash
./mvnw clean install
```
(Use `mvnw.cmd clean install` on Windows PowerShell/CMD)

This command compiles the code, runs tests, and packages the application into a JAR file in the `target/` directory.

### Running the Application Locally

#### Option 1: Using Maven (Spring Boot)

```bash
./mvnw spring-boot:run
```
(Use `mvnw.cmd spring-boot:run` on Windows PowerShell/CMD)

The application will start on `http://localhost:9090`.

#### Option 2: Using Docker

1.  **Build the Docker Image:**
    ```bash
    docker build -t gemini-crud-product .
    ```
2.  **Run the Docker Container:**
    ```bash
    docker run -d --name gemini-crud-product-app -p 9090:9090 gemini-crud-product
    ```
    The application will be accessible at `http://localhost:9090`.

### Accessing H2 Console

The H2 database console is enabled for local development. You can access it at:
`http://localhost:9090/h2-console`

*   **JDBC URL:** `jdbc:h2:mem:testdb`
*   **Username:** `sa`
*   **Password:** `password`

### Testing CRUD Operations (cURL Examples)

Assuming the application is running on `http://localhost:9090`:

**1. Get all Products:**
```bash
curl -X GET http://localhost:9090/api/products
```

**2. Get Product by ID (e.g., ID 1):**
```bash
curl -X GET http://localhost:9090/api/products/1
```

**3. Create a new Product:**
```bash
curl -X POST -H "Content-Type: application/json" -d "{ \"name\": \"New Product\", \"description\": \"Description of new product\", \"price\": 99.99 }" http://localhost:9090/api/products
```

**4. Update an existing Product (e.g., ID 1):**
```bash
curl -X PUT -H "Content-Type: application/json" -d "{ \"name\": \"Updated Shirt\", \"description\": \"A very comfortable updated cotton shirt\", \"price\": 29.99 }" http://localhost:9090/api/products/1
```

**5. Delete a Product (e.g., ID 1):**
```bash
curl -X DELETE http://localhost:9090/api/products/1
```

## 5. Testing

*   **Unit Tests:** Written using JUnit 5 and Mockito. They cover controllers, services, mappers, and models.
*   **Running Tests:** Tests are executed automatically during `mvn clean install` or can be run specifically with `mvn test`.
*   **Code Coverage:** JaCoCo plugin generates code coverage reports in `target/site/jacoco/index.html`.

## 6. CI/CD (Jenkins)

A `Jenkinsfile` is provided at the project root for a multi-stage CI/CD pipeline.

**Pipeline Stages:**
*   **Checkout:** Clones the Git repository.
*   **Build:** Builds the application using Maven.
*   **Test:** Runs unit tests and publishes JUnit results and JaCoCo coverage reports.
*   **SonarQube Analysis:** (Commented out by default) Runs static code analysis. Requires a SonarQube server and Jenkins configuration.
*   **Build Docker Image:** Builds a Docker image of the application locally.
*   **Deploy:** Stops, removes, and runs the Docker image locally.

**To run the Jenkins pipeline:**
1.  Ensure Jenkins is running and configured with Maven, Git, and Docker access.
2.  Create a Pipeline job in Jenkins, pointing to this GitHub repository and the `Jenkinsfile`.
3.  Click "Build Now".

## 7. Key Components

*   **`Product` (model):** JPA Entity representing the product in the database.
*   **`ProductDto` (domain):** Data Transfer Object for product data, used for API requests/responses, includes server-side validation annotations.
*   **`ProductRepository` (repository):** Spring Data JPA interface for database operations on `Product` entities.
*   **`ProductService` (service):** Contains business logic for CRUD operations, interacts with `ProductRepository`.
*   **`ProductController` (controller):** RESTful API endpoints for `Product` management, uses `ProductDto` and `ProductMapper`.
*   **`ProductMapper` (mapper):** Utility class for converting between `Product` entities and `ProductDto` objects.
*   **`GlobalExceptionHandler` (exception):** Centralized exception handling for the application, providing consistent error responses (e.g., 404 for `ProductNotFoundException`, 400 for validation errors).
*   **`ProductNotFoundException` (exception):** Custom exception for when a product is not found.

## 8. Vulnerabilities/Security

*   **Server-Side Validation:** `ProductDto` uses Jakarta Bean Validation (`@NotBlank`, `@Positive`) to ensure data integrity and prevent common input-related vulnerabilities.
*   **Global Exception Handling:** `GlobalExceptionHandler` provides a structured way to handle exceptions, preventing sensitive information leakage and improving API robustness.
*   **Hardcoded Credentials:** (Future Consideration) Currently, H2 database credentials are hardcoded in `application.properties`. For production, these should be managed via environment variables or a secrets management solution.

## 9. Future Enhancements/Considerations

*   **External Database:** Migrate from H2 in-memory to a persistent database (e.g., PostgreSQL, MySQL).
*   **Authentication & Authorization:** Implement Spring Security for securing API endpoints.
*   **Logging:** Implement a more robust logging strategy (e.g., ELK stack).
*   **Monitoring:** Integrate with a monitoring solution (e.g., Prometheus, Grafana).
*   **API Documentation:** Add OpenAPI/Swagger for API documentation.
*   **Error Handling:** More granular error codes and messages.
*   **Performance Testing:** Implement performance tests.
*   **SonarQube Integration:** Enable and configure SonarQube analysis in the Jenkins pipeline for continuous code quality checks.
*   **Deployment Strategy:** Enhance the Jenkins `Deploy` stage for production-grade deployments (e.g., Kubernetes manifests, blue/green deployments).
