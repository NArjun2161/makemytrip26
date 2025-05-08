pipeline {
    agent any

    environment {
        M2_HOME = '/usr/share/maven' // Optional
        SONARQUBE_ENV = 'MySonarQubeServer' 
    }

    stages {
        stage('Checking Versions of Tools') {
            steps {
                sh ''' 
                    git --version
                    mvn -v
                    java --version
                '''
            }
        }

        stage('Print All Env Vars') {
            steps {
                sh 'printenv'
            }
        }

        stage('Checkout') {
            steps {
                git url: 'https://github.com/NArjun2161/makemytrip26.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn compile'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv("${SONARQUBE_ENV}") {
