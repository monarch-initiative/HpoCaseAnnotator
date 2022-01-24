package org.monarchinitiative.hpo_case_annotator.app.model.genome;

import java.net.URL;

public record GenomicRemoteResource(URL genomeUrl,
                                    URL assemblyReportUrl) {
}
