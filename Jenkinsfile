Jenkinsfile (Declarative Pipeline)
pipeline {
    agent { docker { image 'gradle:6.5' } }
    stages {
        stage('build') {
            steps {
                sh 'echo hello'
            }
        }
    }
}