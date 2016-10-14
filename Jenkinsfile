properties properties: [
        [$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '30', numToKeepStr: '10']],
        [$class: 'GithubProjectProperty', displayName: '', projectUrlStr: 'https://github.com/hypery2k/grunt-galenframework'],
]

node {
    // Jenkins makes these variables available for each job it runs
    def mvnHome = tool 'Maven'
    def jdkHome = tool 'JDK8'
    env.JAVA_HOME = "${jdkHome}"
    def buildNumber = env.BUILD_NUMBER
    def workspace = env.WORKSPACE
    def buildUrl = env.BUILD_URL

    // PRINT ENVIRONMENT TO JOB
    echo "jdk installation path is: ${jdkHome}"
    echo "workspace directory is $workspace"
    echo "build URL is $buildUrl"
    echo "build Number is $buildNumber"

    try {
        stage('Checkout') {
            checkout scm
        }

        stage('Build') {
            sh "${mvnHome}/bin/mvn clean package"
        }

        stage('Unit-Test') {
            wrap([$class: "Xvfb"]) {
                sh "${mvnHome}/bin/mvn test"
            }
        }

    } catch (e) {
        step([$class: 'Mailer', notifyEveryUnstableBuild: true, recipients: 'galen@martinreinhardt-online.de', sendToIndividuals: true])
        throw e
    } finally {
        junit '*/target/surefire-reports/TEST-*.xml'
    }
}