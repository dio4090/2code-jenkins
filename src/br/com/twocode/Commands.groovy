package br.com.twocode

def exec(ProjectMetadata projectMetadata) {
    timeout(60) {
        node ('docker_node') {
            try {
                if (projectMetadata.projectName == 'finance-flutter-web') {
                    def webFlutter = new FinanceWebFlutter()
                    webFlutter.exec(projectMetadata)
                }
            } catch (Exception e) {
                throw e
            } finally {
                deleteDir()
            }
        }
    }
}
