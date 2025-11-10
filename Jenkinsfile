pipeline {
    agent any

    environment {
        // Replace with your SonarQube server URL and credentials ID
        SONAR_SCANNER_HOME = tool 'SonarScanner' // Assumes SonarScanner is configured in Jenkins Global Tool Configuration
        SONAR_HOST_URL = 'http://your-sonarqube-server'
        SONAR_LOGIN = credentials('sonarqube-token') // Jenkins credential ID for SonarQube token

        // Replace with your Docker registry credentials ID
        DOCKER_REGISTRY_CREDENTIALS = credentials('docker-hub-credentials') // Jenkins credential ID for Docker Hub
        DOCKER_IMAGE_NAME = 'canwinthey/gemini-crud-product' // Replace with your Docker Hub username/repo
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/canwinthey/gemini-crud-product.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/**/*.xml'
                    jacoco exclusions: [], pattern: 'target/jacoco.exec'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv(credentialsId: 'sonarqube-token', installationName: 'SonarScanner') {
                    sh "mvn sonar:sonar -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.login=${SONAR_LOGIN}"
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKER_IMAGE_NAME}:${env.BUILD_NUMBER}")
                }
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying application...'
                sh "docker stop ${DOCKER_IMAGE_NAME} || true"
                sh "docker rm ${DOCKER_IMAGE_NAME} || true"
                sh "docker run -d --name ${DOCKER_IMAGE_NAME} -p 9090:9090 ${DOCKER_IMAGE_NAME}:${env.BUILD_NUMBER}"
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
