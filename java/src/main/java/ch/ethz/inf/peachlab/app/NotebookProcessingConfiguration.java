package ch.ethz.inf.peachlab.app;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "notebook-processing")
public class NotebookProcessingConfiguration {

    private String baseUrl;
    private String notebookPath;
    private String competitionPath;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getNotebookPath() {
        return notebookPath;
    }

    public void setNotebookPath(String notebookPath) {
        this.notebookPath = notebookPath;
    }

    public String getCompetitionPath() {
        return competitionPath;
    }

    public void setCompetitionPath(String competitionPath) {
        this.competitionPath = competitionPath;
    }
}
