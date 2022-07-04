package br.com.sceneboard

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import groovy.json.JsonSlurper

def exec(ProjectMetadata projectMetadata) {

    projectMetadata.imageFullName = "${ECR_DEFAULT_REGISTRY}/${projectMetadata.projectName}:${BRANCH_NAME}-${BUILD_NUMBER}"
    projectMetadata.imageName = "${ECR_DEFAULT_REGISTRY}/${projectMetadata.projectName}:latest"
    
    try {

        node ('docker_node') {
            stage ('Git Checkout') {
                    
                gitPhase()

            } 
            stage ('Image Build Phase') {
                
                imgBuildPhase(projectMetadata)

            }
            stage ('Image Push Phase') {

                imgPushPhase(projectMetadata)

            }
        }

        node ('sceneboard_back') {
            stage ('Image Run Phase') {

                imgRunPhase(projectMetadata)

            }
        }

    } finally {

        deleteDir()
    
    }

}

def gitPhase() {

    checkout scm

}

def imgBuildPhase(ProjectMetadata projectMetadata) {

    sh "docker build . -f ${projectMetadata.dockerfile} -t ${projectMetadata.imageFullName} --no-cache"

}

def imgPushPhase(ProjectMetadata projectMetadata) {

    sh "docker push ${projectMetadata.imageFullName}"

}

def imgRunPhase(ProjectMetadata projectMetadata) {
    
    gitPhase()

    //alimenta .env para o docker compose
    sh "echo DOCKER_IMAGE=${projectMetadata.imageFullName} >> .env"
    sh "echo CONTAINER_NAME=${projectMetadata.projectName}-${BRANCH_NAME} >> .env" 
    sh "docker-compose -f docker-compose-ci.yaml up -d"

}

def getSecretMetadata(ProjectMetadata projectMetadata) {
    // argumentos do script python
    String args = " --access-key-id ${ACCESS_KEY_ID} "+
                  " --secret-access-key-id ${SECRET_ACCESS_KEY_ID} "+
                  " --region us-east-1 "+
                  " --secret-id ${projectMetadata.secretsManagerId} "+
                  " --env-project ${projectMetadata.envProject} "+
                  " --env-type ${projectMetadata.envType} "+
                  " --docker-image ${projectMetadata.imageFullName} "+
                  " --container-name ${projectMetadata.projectName}-${BRANCH_NAME}"

    // carregar e sceneboardar script python responsável por criar o .env
    configFileProvider(
        [configFile(fileId: '1b1a554b-34e5-4e2a-8116-02a861c482a1', variable: 'SECRETSCRIPT')]) {
        sh "python3 $SECRETSCRIPT ${args}"
    }

}
