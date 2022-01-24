package org.monarchinitiative.hpo_case_annotator.app;

import javafx.application.HostServices;
import org.springframework.stereotype.Component;

import java.net.URL;

@Component
public class HostServicesUrlBrowser implements UrlBrowser {

    private HostServices hostServices;

    public HostServices hostServices() {
        return hostServices;
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    @Override
    public void showDocument(URL url) {
        if (hostServices != null) {
            hostServices.showDocument(url.toExternalForm());
        }
    }
}
