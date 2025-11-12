package ch.ethz.inf.peachlab.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfiguration {

    @Value("${application.title}")
    private String applicationTitle;

    @Value("${backend.csvLocation}")
    private String csvLocation;

    public String getApplicationTitle() {
        return applicationTitle;
    }

    public String getCsvLocation() {
        return csvLocation;
    }
}
