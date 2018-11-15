package org.monarchinitiative.hpo_case_annotator.model.xml_model;

import java.util.ArrayList;
import java.util.List;

public enum VariantMode {
    MENDELIAN,
    SOMATIC,
    SPLICING;


    /**
     * Return array of String representations of enum.
     *
     * @return array of String representations of enum.
     */
    public static String[] getNames() {
        List<String> names = new ArrayList<>();
        for (VariantMode vm : VariantMode.values()) {
            names.add(vm.name());
        }
        return names.toArray(new String[names.size()]);
    }
}
