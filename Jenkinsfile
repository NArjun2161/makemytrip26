pipeline {
    agent any

    stages {
        stage('checking versions of tools') {
            steps {
                sh ''' 
                    git --version
                    mvn -v
                    java --version
                '''
            }
        }

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

        stage('clean Package') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Complete Pipeline') {
            steps {
                echo "Arjun Pipeline executed"
            }
        }
    }
}
