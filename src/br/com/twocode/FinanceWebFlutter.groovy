package br.com.twocode

def exec(ProjectMetadata projectMetadata) {
    //TODO: Quando tiver AWS trocar para a primeira linha
    //projectMetadata.imageFullName = "${ECR_DEFAULT_REGISTRY}/${projectMetadata.projectName}:${BRANCH_NAME}-${BUILD_NUMBER}"
    projectMetadata.imageFullName = "${projectMetadata.projectName}:${main}-${BUILD_NUMBER}"

    try {
        node ('docker_node') {
            stage ('Git Checkout') {
                gitPhase()
            }

            stage ('Image Build Phase') {
                node('docker_node') {
                    imgBuildPhase(projectMetadata)
                }
            }
            stage ('Image Run Phase') {
                    imgRunPhase(projectMetadata)
            }
            deleteDir()
        }
    } finally {
        deleteDir()
    }
}

def gitPhase() {
    checkout scm
}

def imgBuildPhase(ProjectMetadata projectMetadata) {
    gitPhase()

    sh "docker build -t ${projectMetadata.imageFullName} \
    --no-cache -f Dockerfile ."
}

def imgRunPhase(ProjectMetadata projectMetadata) {
    gitPhase()

    sh "echo DOCKER_IMAGE=${projectMetadata.imageFullName} >> .env"
    sh "echo CONTAINER_NAME=${projectMetadata.projectName}-${BRANCH_NAME} >> .env"

    sh "docker image pull ${projectMetadata.imageFullName}"
    sh "docker-compose -f docker-compose-ci.yaml -p ${projectMetadata.projectName}-${BRANCH_NAME} up -d"
}
