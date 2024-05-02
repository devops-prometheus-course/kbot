pipeline {
agent any
    parameters {

        choice(name: 'OS', choices: ['linux', 'darwin', 'windows', 'all'], description: 'Pick OS')
        choice(name: 'ARCH', choices: ['arm64', 'amd64'], description: 'Pick ARCH')
    }
    environment {
        REPO = 'https://github.com/devops-prometheus-course/kbot'
        BRANCH = 'main'
        TARGETOS = "${params.OS}"
        TARGETARCH = "${params.ARCH}"
    }
    stages {

        stage("clone") {
            steps {
                echo 'CLONE REPOSITORY'
                  git branch: "${BRANCH}", url: "${REPO}"
            }
        }

        stage("test") {
            steps {
                echo 'TEST EXECUTION STARTED'
                sh 'make test'
            }
        }

        stage("build") {
            steps {
                echo "Build for platform ${params.OS}"
                echo "Build for arch: ${params.ARCH}"
                sh 'make build'
            }
        }

        stage("image") {
            steps {
                script {
                    echo 'BUILD EXECUTION STARTED'
                    sh 'make image'
                }
            }
        }

        stage("push") {
            steps {
                script {
                    docker.withRegistry('', 'dockerhub') {
                    sh 'make push'
                    }             
                }
            }
        }
    }
}