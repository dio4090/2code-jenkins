package br.com.sceneboard

def exec(ProjectMetadata projectMetadata) {

    def status = false

    timeout(60) {

        node ('master') {

            try {

                if(projectMetadata.projectName == "example") {
                    def example = new Example()
                    example.exec(projectMetadata)
                }

                //status = true
                //sendSlackMessage(status, projectMetadata.projectName)

            } catch (Exception e) {

                //sendSlackMessage(status, projectMetadata.projectName)
                throw e

            } finally {

                deleteDir()
            
            }

        }

    }

}

def sendSlackMessage(boolean status, String projectName) {

    def color = ""
    def title_text = ""
    def attachment_text = "Veja mais detalhes abaixo"
    def author_name = "Jenkins Webhook App"
    //def webhook_url = "${SLACK_HOOK_URL}"
    def content_type = "Content-type: application/json"
    def message = ""
    def messageRequest = ""
    
    if (status){
        
        color = "good"
        title_text = "*Build Success! ${BUILD_TAG}*"
        message = "{ \"text\": \"${title_text}\", \"attachments\": [ { \"text\": \"${attachment_text}\", \"author_name\":\"${author_name}\", \"color\": \"${color}\", \"actions\": [ { \"name\": \"Jenkins\", \"text\": \"Ver no Jenkins\", \"type\": \"button\", \"style\": \"primary\", \"url\": \"${BUILD_URL}console\" } ] } ] }"
        //messageRequest = "curl -X POST -H '${content_type}' --data '${message}' ${webhook_url}"
        sh "${messageRequest}"
        
    
    } else {

        color = "danger"
        title_text = "*Build Failed! ${BUILD_TAG}*"
        message = "{ \"text\": \"${title_text}\", \"attachments\": [ { \"text\": \"${attachment_text}\", \"author_name\":\"${author_name}\", \"color\": \"${color}\", \"actions\": [ { \"name\": \"Jenkins\", \"text\": \"Ver no Jenkins\", \"type\": \"button\", \"style\": \"danger\", \"url\": \"${BUILD_URL}console\" } ] } ] }"
        //messageRequest = "curl -X POST -H '${content_type}' --data '${message}' ${webhook_url}"
        sh "${messageRequest}"
        
    } 
    
} 