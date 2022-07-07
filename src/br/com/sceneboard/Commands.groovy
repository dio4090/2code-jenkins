package br.com.sceneboard

def exec(ProjectMetadata projectMetadata) {
    timeout(60) {
        node ('mestre') {
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
