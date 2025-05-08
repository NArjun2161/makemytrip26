pipeline {
    agent any

    environment {
        M2_HOME = '/usr/share/maven' // Optional
        SONARQUBE_ENV = 'MySonarQubeServer' // Name you gave in Jenkins → Configure System
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

        // ✅ ADD THIS STAGE FOR SONARQUBE
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv("${env.SONARQUBE_ENV}") {
                    sh '''
                        sonar-scanner \
                          -Dsonar.projectKey=makemytrip \
                          -Dsonar.sources=src \
                          -Dsonar.java.binaries=target/classes \
                          -Dsonar.host.url=http://192.168.217.155:9000/
                    '''
                }
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Clean and Package') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Publish Test Results') {
            steps {
                junit '**/target/surefire-reports/*.xml'
            }
        }

        stage('Archive Build Artifacts') {
            steps {
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }

        stage('Deploy (Local Run)') {
            steps {
                sh '''
                    pkill -f "makemytrip.*.jar" || true
                    nohup java -jar target/makemytrip-0.0.1-SNAPSHOT.jar --server.port=9090 > app.log 2>&1 &
                    echo "App started on port 9090"
                '''
            }
        }

        stage('Complete Pipeline') {
            steps {
                echo '✅ Arjun Pipeline executed successfully!'
            }
        }
    }

    post {
        success {
            echo '🎉 Pipeline completed successfully..'
        }
        failure {
            echo '❌ Pipeline failed. Please check the logs.'
        }
    }
}
