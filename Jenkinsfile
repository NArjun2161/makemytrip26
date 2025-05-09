pipeline {
    agent any

    environment {
        M2_HOME = '/usr/share/maven'
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
                            export PATH=$PATH:/opt/sonar-scanner/bin
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

        stage('Deploy with Ngrok') {
            steps {
                sh '''#!/bin/bash
                    echo "🚦 Stopping previous Spring Boot app and Ngrok if any..."
                    pkill -f "makemytrip-0.0.1-SNAPSHOT.jar" || true
                    pkill -f "ngrok" || true
                    sleep 2

                    echo "🚀 Starting Spring Boot app on port 9090..."
                    nohup /usr/lib/jvm/java-21-amazon-corretto/bin/java -jar target/makemytrip-0.0.1-SNAPSHOT.jar --server.port=9090 > "$WORKSPACE/app.log" 2>&1 &

                    echo "🌐 Starting Ngrok tunnel on port 9090..."
                    nohup ngrok http 9090 > "$WORKSPACE/ngrok.log" 2>&1 &

                    echo "⏳ Waiting for Ngrok to initialize..."
                    sleep 10

                    echo "🌍 Fetching Ngrok public URL..."
                    ngrok_url=$(curl --silent http://localhost:4040/api/tunnels | jq -r '.tunnels[0].public_url')
                    
                    if [ -z "$ngrok_url" ] || [ "$ngrok_url" = "null" ]; then
                        echo "❌ Failed to fetch Ngrok public URL"
                        exit 1
                    else
                        echo "✅ Ngrok public URL: $ngrok_url"
                    fi

                    echo "📜 app.log:"
                    tail -n 20 "$WORKSPACE/app.log"

                    echo "📜 ngrok.log:"
                    tail -n 20 "$WORKSPACE/ngrok.log"
                '''
            }
        }
    }

    post {
        success {
            echo '🎉 Pipeline completed successfully.'
        }
        failure {
            echo '❌ Pipeline failed. Please check the logs.'
        }
    }
}
