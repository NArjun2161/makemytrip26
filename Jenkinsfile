pipeline {
    agent any

    environment {
        M2_HOME = '/usr/share/maven'
        PATH = "${env.PATH}:${M2_HOME}/bin:/opt/sonar-scanner/bin"
        SONARQUBE_ENV = 'MySonarQubeServer'
        IMAGE_NAME = 'arjun1421/makemytrip-image:latest'
        CONTAINER_NAME = 'makemytrip-app'
        PORT = '9090'
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
                        docker rm -f $CONTAINER_NAME || true
                        docker rmi -f $IMAGE_NAME || true

                        docker build -t $IMAGE_NAME .

                        docker run -d -p $PORT:$PORT --name $CONTAINER_NAME $IMAGE_NAME
                    '''

                    sleep(time: 10, unit: 'SECONDS')

                    def curlStatus = sh(script: "curl -f http://localhost:$PORT", returnStatus: true)
                    if (curlStatus != 0) {
                        error("❌ Dockerized app did not start properly.")
                    } else {
                        echo "✅ Dockerized app is running at http://localhost:$PORT"
                    }
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker push $IMAGE_NAME
                    '''
                }
            }
        }
    }
}
