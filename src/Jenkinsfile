pipeline {
    agent any
    
    stages {
        stage('checkout') {
            steps {
                git 'https://github.com/NArjun2161/makemytrip26.git'
            }
        }
        stage('build') {
            steps {
                sh 'mvn compile'
            }
        }
        stage('test') {
            steps {
                sh 'mvn test'
            }
        }
    }
}
