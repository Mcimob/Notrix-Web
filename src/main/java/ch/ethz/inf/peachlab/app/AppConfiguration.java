package ch.ethz.inf.peachlab.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class AppConfiguration {

    @Value("${application.title}")
    private String applicationTitle;

    @Value("classpath:README.md")
    private Resource readme;

    public String getApplicationTitle() {
        return applicationTitle;
    }

    public Resource getReadme() {
        return readme;
    }
}
