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

        stage('Deploy (Local Run)') {
            steps {
                script {
                    echo "🚀 Starting Spring Boot application..."

                    sh '''
                        PID=$(lsof -ti:9090 || true)
                        if [ -n "$PID" ]; then
                            echo "🛑 Killing process on port 9090 (PID=$PID)"
                            kill -9 $PID
                        else
                            echo "✅ No existing process on port 9090"
                        fi
                    '''

                    // Start the app in background
                    sh 'nohup java -jar target/makemytrip-0.0.1-SNAPSHOT.jar --server.port=9090 > app.log 2>&1 &'

                    sleep(time: 10, unit: "SECONDS")

                    // Verify it's running
                    def curlStatus = sh(script: 'curl -f http://localhost:9090', returnStatus: true)
                    if (curlStatus != 0) {
                        error("❌ Spring Boot app did not start properly.")
                    } else {
                        echo "✅ App started successfully at http://localhost:9090"
                    }
                }
            }
        }
    }
