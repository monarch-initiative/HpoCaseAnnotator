package org.monarchinitiative.hpo_case_annotator.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hca.text-mining")
public class TextMiningProperties {

    private String biolarkUrl;
    private String scigraphUrl;

    public String biolarkUrl() {
        return biolarkUrl;
    }

    public void setBiolarkUrl(String biolarkUrl) {
        this.biolarkUrl = biolarkUrl;
    }

    public String scigraphUrl() {
        return scigraphUrl;
    }

    public void setScigraphUrl(String scigraphUrl) {
        this.scigraphUrl = scigraphUrl;
    }
}
