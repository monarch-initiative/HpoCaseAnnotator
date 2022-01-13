package org.monarchinitiative.hpo_case_annotator.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hca.reference")
public class GenomicRemoteResourcesProperties {


    private GenomicRemoteResource hg19;
    private GenomicRemoteResource hg38;

    public GenomicRemoteResource hg19() {
        return hg19;
    }

    public void setHg19(GenomicRemoteResource hg19) {
        this.hg19 = hg19;
    }

    public GenomicRemoteResource hg38() {
        return hg38;
    }

    public void setHg38(GenomicRemoteResource hg38) {
        this.hg38 = hg38;
    }

    public static class GenomicRemoteResource {
        private String genomeFastaUrl;
        private String genomeAssemblyReportUrl;

        public String genomeFastaUrl() {
            return genomeFastaUrl;
        }

        public void setGenomeFastaUrl(String genomeFastaUrl) {
            this.genomeFastaUrl = genomeFastaUrl;
        }

        public String genomeAssemblyReportUrl() {
            return genomeAssemblyReportUrl;
        }

        public void setGenomeAssemblyReportUrl(String genomeAssemblyReportUrl) {
            this.genomeAssemblyReportUrl = genomeAssemblyReportUrl;
        }
    }
}
