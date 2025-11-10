# CI/CD Setup and Troubleshooting Guide for gemini-crud-product

This document provides a comprehensive guide for setting up and troubleshooting the Continuous Integration/Continuous Deployment (CI/CD) pipeline for the `gemini-crud-product` application. It covers local development, Dockerization, and Jenkins pipeline configuration.

## 1. Project Overview

The `gemini-crud-product` is a Java Spring Boot application demonstrating CRUD operations for a `Product` entity. It features a RESTful API, H2 in-memory database, server-side validation, global exception handling, comprehensive testing, Dockerization, and a Jenkins CI/CD pipeline.

## 2. Prerequisites

Before you begin, ensure you have the following installed on your development machine:

*   **Java Development Kit (JDK):** Version 17 or higher.
*   **Apache Maven:** Version 3.x.x.
*   **Git:** For version control.
*   **Docker Desktop:** For running Docker containers and building images.

## 3. Local Development Setup

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

## 4. Dockerizing the Application

The application is designed to be built outside the Docker container and then packaged into a Docker image.

### `Dockerfile` Content

```dockerfile
FROM eclipse-temurin:17-jdk-jammy

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} app.jar

EXPOSE 9090

ENTRYPOINT ["java","-jar","/app.jar"]
```

### Building the Docker Image

After building the JAR file using `mvn clean install`, build the Docker image:

```bash
docker build -t gemini-crud-product .
```

### Running the Docker Container Locally

```bash
docker run -d --name gemini-crud-product-app -p 9090:9090 gemini-crud-product
```

## 5. Setting up Jenkins Locally with Docker

This section guides you through setting up Jenkins in a Docker container and configuring it to build and deploy your application.

### 5.1. Running Jenkins in a Docker Container

1.  **Create a Docker volume for Jenkins data (recommended for persistence):**
    ```bash
    docker volume create jenkins_home
    ```

2.  **Run the Jenkins Docker container with Docker socket mounted:**
    This allows the Jenkins container to interact with the host's Docker daemon to build and run other containers. We'll run it as `root` for simplicity in a local testing environment to avoid permission issues with the Docker socket.

    ```powershell
    docker run -d -p 8080:8080 -p 50000:50000 `
      --name Jenkins `
      -v jenkins_home:/var/jenkins_home `
      -v //var/run/docker.sock:/var/run/docker.sock `
      --user root `
      jenkins/jenkins:lts
    ```
    *   `-d`: Run in detached mode (background).
    *   `-p 8080:8080`: Map container port 8080 (Jenkins web UI) to host port 8080.
    *   `-p 50000:50000`: Map container port 50000 (for Jenkins agents) to host port 50000.
    *   `--name Jenkins`: Name the container.
    *   `-v jenkins_home:/var/jenkins_home`: Mount the `jenkins_home` volume for data persistence.
    *   `-v //var/run/docker.sock:/var/run/docker.sock`: Mount the Docker socket from the host. Note `//var/run/docker.sock` is used for Windows paths.
    *   `--user root`: Run the container process as `root` to bypass Docker socket permission issues in a local setup. **(Not recommended for production environments due to security implications).**
    *   `jenkins/jenkins:lts`: Use the long-term support Jenkins image.

3.  **Retrieve the initial admin password:**
    After a few minutes, Jenkins will be running. Get the initial admin password from the container logs:
    ```bash
    docker logs Jenkins
    ```
    Look for a line similar to: `Jenkins initial setup is required. An admin user has been created and a password generated.` followed by a long alphanumeric string.

4.  **Access Jenkins UI:**
    Open your web browser and go to `http://localhost:8080`. Enter the initial admin password and follow the setup instructions (install suggested plugins, create admin user).

### 5.2. Installing Docker CLI inside Jenkins Container

For the Jenkins pipeline to execute Docker commands (like `docker build`, `docker stop`, `docker rm`, `docker run`), the Docker CLI needs to be available inside the Jenkins container.

1.  **Access the Jenkins container's bash shell:**
    ```bash
    docker exec -u root -it Jenkins bash
    ```

2.  **Install Docker CLI:**
    ```bash
    apt-get update
    apt-get install -y docker.io
    ```

3.  **Exit the container's shell:**
    ```bash
    exit
    ```

### 5.3. Configuring Jenkins Global Tools and Plugins

1.  **Install Required Jenkins Plugins:**
    *   Go to `Manage Jenkins` -> `Manage Plugins`.
    *   Go to the "Available" tab and install the following:
        *   **Git Plugin**
        *   **Pipeline Plugin**
        *   **SonarQube Scanner for Jenkins** (if you plan to enable SonarQube analysis)
        *   **Docker Pipeline Plugin**
        *   **Pipeline Utility Steps Plugin** (provides `cleanWs()` if you decide to use it)

2.  **Configure Global Tool Configuration:**
    *   Go to `Manage Jenkins` -> `Global Tool Configuration`.
    *   **Git:**
        *   Scroll to the "Git" section.
        *   Click "Add Git".
        *   Name it `DefaultGit`.
        *   For "Path to Git executable", leave it empty or set it to `git` (as it should be in the container's PATH after installing `docker.io`).
        *   Click "Save".
    *   **Maven:**
        *   Scroll to the "Maven" section.
        *   Click "Add Maven".
        *   Name it `Maven3`.
        *   Select "Install automatically" and choose the latest Maven 3.x version (e.g., `3.9.6`).
        *   Click "Save".
    *   **SonarQube Scanner (if applicable):**
        *   Scroll to the "SonarQube Scanner" section.
        *   Click "Add SonarQube Scanner".
        *   Name it `SonarScanner`.
        *   Select "Install automatically" and choose a version.
        *   Click "Save".

3.  **Configure System Credentials (if applicable):**
    *   Go to `Manage Jenkins` -> `Manage Credentials`.
    *   **SonarQube Token:** If you enable SonarQube analysis, add a "Secret text" credential with ID `sonarqube-token` and your SonarQube authentication token.
    *   **Docker Hub Credentials:** If you decide to push to Docker Hub, add a "Username with password" credential with ID `docker-hub-credentials` and your Docker Hub username and password.

### 5.4. Creating the Jenkins Pipeline Job

1.  **Create a New Pipeline Job:**
    *   Click "New Item" from the Jenkins dashboard.
    *   Enter a name for your job (e.g., `gemini-crud-product-pipeline`).
    *   Select "Pipeline" as the item type.
    *   Click "OK".

2.  **Configure the Pipeline Job:**
    *   In the job configuration page, scroll down to the "Pipeline" section.
    *   For "Definition", select "Pipeline script from SCM".
    *   For "SCM", select "Git".
    *   **Repository URL:** `https://github.com/canwinthey/gemini-crud-product.git`
    *   **Credentials:** Since your repository is public, you might not need credentials, or you can add a "None" credential.
    *   **Branches to build:** `master`
    *   **Script Path:** `Jenkinsfile`
    *   Click "Save".

### 5.5. Running the Pipeline

*   On the job's dashboard, click "Build Now".
*   Monitor the build progress in the "Build History" section. Click on a build number and then "Console Output" to see the logs.

## 6. Jenkinsfile Breakdown

The `Jenkinsfile` defines a multi-stage CI/CD pipeline:

```groovy
pipeline {
    agent any
    tools {
        git 'DefaultGit' // Matches Git installation name in Global Tool Configuration
    }
    environment {
        // SONAR_SCANNER_HOME = tool 'SonarScanner' // Uncomment and configure if SonarQube is enabled
        // SONAR_HOST_URL = 'http://your-sonarqube-server' // Uncomment and configure if SonarQube is enabled
        // SONAR_LOGIN = credentials('sonarqube-token') // Uncomment and configure if SonarQube is enabled

        // DOCKER_REGISTRY_CREDENTIALS = credentials('docker-hub-credentials') // Uncomment and configure if pushing to Docker Hub
        DOCKER_IMAGE_NAME = 'canwinthey/gemini-crud-product'
    }

    stages {
        stage('Checkout') {
            steps {
                //deleteDir() // Ensure a completely clean workspace - currently commented out
                checkout scm // Uses the SCM configured in the Jenkins job
            }
        }

        stage('Build') {
            steps {
                withMaven(maven: 'Maven3') { // Matches Maven installation name in Global Tool Configuration
                    sh 'mvn clean install -DskipTests'
                }
            }
        }

        stage('Test') {
            steps {
                withMaven(maven: 'Maven3') { // Matches Maven installation name in Global Tool Configuration
                    sh 'mvn test'
                }
            }
            post {
                always {
                    junit 'target/surefire-reports/**/*.xml' // Publish JUnit test results
                    jacoco execPattern: 'target/jacoco.exec', classPattern: 'target/classes', exclusionPattern: '' // Publish JaCoCo coverage report
                }
            }
        }

        // stage('SonarQube Analysis') { // Uncomment to enable SonarQube analysis
        //     steps {
        //         withSonarQubeEnv(credentialsId: 'sonarqube-token', installationName: 'SonarScanner') {
        //             sh "mvn sonar:sonar -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.login=${SONAR_LOGIN}"
        //         }
        //     }
        // }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKER_IMAGE_NAME}:${env.BUILD_NUMBER}") // Builds Docker image locally
                }
            }
        }

        stage('Deploy') {
            steps {
                echo "Deploying application: Running docker image: ${DOCKER_IMAGE_NAME}"
                sh "docker stop ${DOCKER_IMAGE_NAME} || true" // Stop existing container
                sh "docker rm ${DOCKER_IMAGE_NAME} || true"   // Remove existing container
                sh "docker run -d --name gemini-crud-product -p 9090:9090 ${DOCKER_IMAGE_NAME}:${env.BUILD_NUMBER}" // Run new container
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished.'
        }
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
```

## 7. Troubleshooting Common Jenkins Pipeline Errors

### 7.1. `mvn: not found` Error

**Problem:** The Jenkins agent cannot find the `mvn` command during the "Build" or "Test" stages.
**Solution:**
1.  **Configure Maven in Jenkins Global Tool Configuration:**
    *   Go to `Manage Jenkins` -> `Global Tool Configuration`.
    *   Scroll to the "Maven" section.
    *   Click "Add Maven".
    *   Name it `Maven3`.
    *   Select "Install automatically" and choose a Maven 3.x version.
    *   Click "Save".
2.  **Ensure `Jenkinsfile` uses `withMaven`:** The `Jenkinsfile` already includes `withMaven(maven: 'Maven3')` in the "Build" and "Test" stages. Ensure the name `Maven3` matches your configuration.

### 7.2. `permission denied while trying to connect to the Docker daemon socket` Error

**Problem:** The Jenkins container does not have sufficient permissions to access the Docker daemon socket on the host.
**Solution:**
1.  **Ensure Docker socket is mounted:** Verify the Jenkins container is run with `-v //var/run/docker.sock:/var/run/docker.sock` (for Windows) or `-v /var/run/docker.sock:/var/run/docker.sock` (for Linux/macOS).
2.  **Run Jenkins container as `root` (for local testing):** The provided `docker run` command for Jenkins already includes `--user root` to mitigate this in a local testing environment. **Remember this is not recommended for production.**

### 7.3. `fatal: not in a git directory` Error

**Problem:** Git commands fail because the workspace is not recognized as a Git repository. This can happen due to workspace corruption or incorrect Git setup.
**Solution:**
1.  **Ensure Git is configured in Jenkins Global Tool Configuration:**
    *   Go to `Manage Jenkins` -> `Global Tool Configuration`.
    *   Scroll to the "Git" section.
    *   Ensure you have a Git installation named `DefaultGit`.
    *   For "Path to Git executable", leave it empty or set it to `git`.
    *   Click "Save".
2.  **`Jenkinsfile` Checkout Strategy:** The `Jenkinsfile` uses `checkout scm` which relies on the SCM configuration of the Jenkins job. It also has `deleteDir()` commented out. If you face persistent issues, you might temporarily uncomment `deleteDir()` to force a completely clean workspace before checkout, but ensure the "Pipeline Utility Steps Plugin" is installed for `deleteDir()` to work.

### 7.4. `Selected Git installation does not exist. Using Default` Warning

**Problem:** Jenkins issues a warning that a specific Git installation isn't found, falling back to a default. This can sometimes precede a `fatal: not in a git directory` error if the default isn't functional.
**Solution:** Explicitly configure Git in `Manage Jenkins` -> `Global Tool Configuration` as described in section 5.3.

## 8. Accessing Reports

After a successful Jenkins pipeline run:

*   **JUnit Test Results:** In your Jenkins job, navigate to the "Test Result Trend" or "Latest Test Result" link. The URL will typically be `http://localhost:8080/job/your-job-name/lastSuccessfulBuild/testReport/`.
*   **JaCoCo Coverage Report:** In your Jenkins job, look for a "Coverage Report" link or similar, usually generated by the JaCoCo plugin. The URL will typically be `http://localhost:8080/job/your-job-name/lastSuccessfulBuild/jacoco/`.

Remember to replace `your-job-name` with the actual name of your Jenkins pipeline job.
