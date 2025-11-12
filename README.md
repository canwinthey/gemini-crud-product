# Gemini CRUD Product Application

This document provides a comprehensive overview and setup guide for the `gemini-crud-product` project.

## 1. Project Overview

This project is a simple Java Spring Boot application that demonstrates basic Create, Read, Update, and Delete (CRUD) operations for a `Product` entity. It provides a RESTful API, uses an in-memory H2 database, and is fully integrated with Docker, Jenkins, and SonarQube for a complete CI/CD and code quality workflow.

**Core Functionality:**
*   Manage product details (ID, Name, Description, Price).
*   Expose REST endpoints for CRUD operations with interactive API documentation.
*   Validate incoming product data.
*   Handle exceptions gracefully.
*   Automated build, test, and code analysis via a Jenkins pipeline.

## 2. Technology Stack

*   **Language:** Java 17
*   **Framework:** Spring Boot 3.2.4
*   **Build Tool:** Apache Maven
*   **Database:** H2 Database (in-memory for development/testing)
*   **ORM:** Spring Data JPA / Hibernate
*   **API Documentation:** SpringDoc (Swagger UI)
*   **Containerization:** Docker, Docker Compose
*   **CI/CD:** Jenkins Pipeline
*   **Code Quality:** SonarQube
*   **Testing:** JUnit 5, Mockito, Spring Boot Test
*   **Code Coverage:** JaCoCo
*   **CORS Configuration:** Configured to allow requests from `http://localhost:3000` to support local ReactJS development.

## 3. Project Structure

The project follows a standard Maven structure and includes configuration for the full development and CI/CD lifecycle.

```
.
├── ...
├── src/
│   ├── main/
│   └── test/
├── Dockerfile
├── docker-compose.yml  # Docker Compose for local SonarQube environment
├── Jenkinsfile         # Jenkins CI/CD pipeline definition
├── mvnw & mvnw.cmd     # Maven Wrapper
├── pom.xml             # Maven Project Object Model
└── README.md           # This document
```

## 4. Local Development Setup

### Prerequisites

*   **Java Development Kit (JDK):** Version 17 or higher.
*   **Docker Desktop:** Required for running the application and the SonarQube environment.
*   **Git:** For version control.

### Step 1: Set Up SonarQube Environment

This project uses SonarQube for static code analysis. A `docker-compose.yml` file is provided to easily run SonarQube and a PostgreSQL database locally.

1.  **Start the containers:**
    From the project root directory, run the following command. This will download the images and start the services in the background.
    ```bash
    docker-compose up -d
    ```

2.  **Access SonarQube:**
    *   Wait a few minutes for the server to start.
    *   Navigate to **[http://localhost:9000](http://localhost:9000)** in your browser.
    *   Log in with default credentials:
        *   **Username:** `admin`
        *   **Password:** `admin`
    *   You will be prompted to change the password.

This local SonarQube instance is what the Jenkins pipeline will use for code analysis.

### Step 2: Run the Spring Boot Application

You can run the application using either Maven or Docker.

#### Option A: Using Maven

```bash
# On Windows
.\mvnw.cmd spring-boot:run

# On Linux/macOS
./mvnw spring-boot:run
```

#### Option B: Using Docker

1.  **Build the JAR file:**
    ```bash
    .\mvnw.cmd clean install
    ```
2.  **Build the Docker Image:**
    ```bash
    docker build -t gemini-crud-product .
    ```
3.  **Run the Docker Container:**
    ```bash
    docker run -d --name gemini-crud-product-app -p 9090:9090 gemini-crud-product
    ```

The application will be available at `http://localhost:9090`.

## 5. API and Code Quality Documentation

### Swagger UI (API Documentation)

Interactive API documentation is available via Swagger UI. Once the application is running, access it here:

*   **URL:** **[http://localhost:9090/swagger-ui/index.html](http://localhost:9090/swagger-ui/index.html)**

From the Swagger UI, you can view all API endpoints, see request/response models, and execute API calls directly from your browser.

### SonarQube (Code Quality)

Code analysis reports are published to the local SonarQube instance. You can view the project dashboard here:

*   **URL:** **[http://localhost:9000](http://localhost:9000)**

## 6. CI/CD Pipeline (Jenkins)

A `Jenkinsfile` is included to automate the build, test, analysis, and deployment process.

### Jenkins Prerequisites

Before running the pipeline, you must configure your Jenkins instance:

1.  **Install Plugin:** Install the **"SonarQube Scanner for Jenkins"** plugin (`Manage Jenkins` > `Plugins`).
2.  **Configure SonarQube Server:**
    *   Go to `Manage Jenkins` > `Configure System`.
    *   In the "SonarQube servers" section, add a server.
    *   **Name:** `sonarqube` (This must match the name in the `Jenkinsfile`).
    *   **Server URL:** `http://localhost:9000` (or your SonarQube server's address).
    *   **Server authentication token:** Add a "Secret text" credential containing a token generated from the SonarQube UI (`My Account` > `Security`).
3.  **Configure Maven:**
    *   Go to `Manage Jenkins` > `Global Tool Configuration`.
    *   Add a Maven installation named `Maven3`.

### Pipeline Stages

The pipeline includes the following stages:

*   **Prepare:** Stops and removes old Docker containers to ensure a clean environment.
*   **Checkout:** Clones the project from source control.
*   **Build:** Compiles the application and skips tests for this stage.
*   **Test:** Runs all unit and integration tests and publishes the results (JUnit and JaCoCo).
*   **SonarQube Analysis:**
    *   Performs static code analysis using SonarQube.
    *   Waits for the analysis to complete and checks the project's **Quality Gate**.
    *   The pipeline will **fail** if the Quality Gate conditions are not met.
*   **Build Docker Image:** Builds a new Docker image tagged with the build number.
*   **Deploy:** Deploys the application by running the newly built Docker image.

### Running the Pipeline

1.  Create a "Pipeline" job in Jenkins.
2.  Configure the job to use "Pipeline script from SCM".
3.  Point it to your Git repository. The script path should be `Jenkinsfile`.
4.  Run the pipeline.