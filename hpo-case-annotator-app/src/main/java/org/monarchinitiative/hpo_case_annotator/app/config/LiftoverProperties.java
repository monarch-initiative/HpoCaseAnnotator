package org.monarchinitiative.hpo_case_annotator.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("hca.liftover")
public class LiftoverProperties {

    private String hg18ToHg38Url;
    private String hg19ToHg38Url;

    public String hg18ToHg38Url() {
        return hg18ToHg38Url;
    }

    public void setHg18ToHg38Url(String hg18ToHg38Url) {
        this.hg18ToHg38Url = hg18ToHg38Url;
    }

    public String hg19ToHg38Url() {
        return hg19ToHg38Url;
    }

    public void setHg19ToHg38Url(String hg19ToHg38Url) {
        this.hg19ToHg38Url = hg19ToHg38Url;
    }
}
