package org.monarchinitiative.hpo_case_annotator.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hca.hpo")
public class HpoProperties {

    private String ontologyUrl;

    public String ontologyUrl() {
        return ontologyUrl;
    }

    public void setOntologyUrl(String ontologyUrl) {
        this.ontologyUrl = ontologyUrl;
    }
}
