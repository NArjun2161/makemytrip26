pipeline {
    agent any

    environment {
        M2_HOME = '/usr/share/maven'
        PATH = "${env.PATH}:${M2_HOME}/bin:/opt/sonar-scanner/bin"
        SONARQUBE_ENV = 'MySonarQubeServer'
    }

    stages {
        stage('Tool Versions') {
            steps {
                sh '''
                    echo "🔧 Checking tool versions..."
                    git --version
                    mvn -v
                    java --version
                '''
            }
        }

        stage('Print Environment') {
            steps {
                sh 'printenv'
            }
        }

        stage('Checkout Code') {
            steps {
                git url: 'https://github.com/NArjun2161/makemytrip26.git'
            }
        }

        stage('Build and Unit Test') {
            steps {
                sh 'mvn clean verify'
            }
        }

        stage('Generate JaCoCo Report') {
            steps {
                sh 'mvn jacoco:report'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv("${SONARQUBE_ENV}") {
                    withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                        sh '''
                            mvn dependency:copy-dependencies

                            sonar-scanner \
                              -Dsonar.projectKey=newProject \
                              -Dsonar.sources=src \
                              -Dsonar.java.binaries=target/classes \
                              -Dsonar.java.libraries=target/dependency \
                              -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                              -Dsonar.host.url=http://192.168.217.155:9000 \
                              -Dsonar.login=$SONAR_TOKEN
                        '''
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 2, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Package Application') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Publish Test Results') {
            steps {
                junit '**/target/surefire-reports/*.xml'
            }
        }

        stage('Archive JAR Artifacts') {
            steps {
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }

        stage('Docker Build & Deploy') {
            steps {
                script {
                    echo "🐳 Building Docker image..."

                    sh '''
                        docker rm -f makemytrip-app || true
                        docker rmi -f makemytrip-image || true

                        docker build -t makemytrip-image .

                        docker run -d -p 8080:8080 --name makemytrip-app makemytrip-image
                    '''

                    sleep(time: 10, unit: 'SECONDS')

                    def curlStatus = sh(script: 'curl -f http://localhost:8080', returnStatus: true)
                    if (curlStatus != 0) {
                        error("❌ Dockerized app did not start properly.")
                    } else {
                        echo "✅ Dockerized app is running at http://localhost:8080"
                    }
                }
            }
        }
    }
}
