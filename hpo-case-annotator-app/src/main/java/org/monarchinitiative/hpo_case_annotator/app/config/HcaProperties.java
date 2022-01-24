package org.monarchinitiative.hpo_case_annotator.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "hca")
public class HcaProperties {

    private String version;
    private String name;
    private String entrezGeneUrl;

    @NestedConfigurationProperty // hca.reference
    private GenomicRemoteResourcesProperties reference;
    @NestedConfigurationProperty // hca.hpo
    private HpoProperties hpo;
    @NestedConfigurationProperty // hca.text-mining
    private TextMiningProperties textMining;
    @NestedConfigurationProperty // hca.liftover
    private LiftoverProperties liftover;

    public String version() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String entrezGeneUrl() {
        return entrezGeneUrl;
    }

    public void setEntrezGeneUrl(String entrezGeneUrl) {
        this.entrezGeneUrl = entrezGeneUrl;
    }

    public GenomicRemoteResourcesProperties reference() {
        return reference;
    }

    public void setReference(GenomicRemoteResourcesProperties reference) {
        this.reference = reference;
    }

    public HpoProperties hpo() {
        return hpo;
    }

    public void setHpo(HpoProperties hpo) {
        this.hpo = hpo;
    }

    public TextMiningProperties textMining() {
        return textMining;
    }

    public void setTextMining(TextMiningProperties textMining) {
        this.textMining = textMining;
    }

    public LiftoverProperties liftover() {
        return liftover;
    }

    public void setLiftover(LiftoverProperties liftover) {
        this.liftover = liftover;
    }
}
