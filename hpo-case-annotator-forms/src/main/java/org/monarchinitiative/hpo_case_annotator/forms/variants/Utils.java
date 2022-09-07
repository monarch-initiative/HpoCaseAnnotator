package org.monarchinitiative.hpo_case_annotator.forms.variants;

import org.monarchinitiative.svart.Contig;

class Utils {

    static String getContigNameOrEmptyString(Contig contig) {
        return contig == null ? "" : contig.name();
    }

}
