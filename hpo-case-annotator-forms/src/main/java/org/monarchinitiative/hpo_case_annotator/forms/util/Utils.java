package org.monarchinitiative.hpo_case_annotator.forms.util;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.monarchinitiative.hpo_case_annotator.model.v2.OntologyClass;
import org.monarchinitiative.phenol.annotations.constants.hpo.HpoOnsetTermIds;
import org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Comparator;
import java.util.List;

import static javafx.beans.binding.Bindings.*;

public class Utils {

    private Utils() {}

    public static void closeTheStage(ActionEvent e) {
        Button okButton = (Button) e.getSource();
        Stage stage = (Stage) okButton.getParent().getScene().getWindow();
        stage.close();
    }

    public static StringBinding nullableStringProperty(ObjectProperty<?> property, String propertyId) {
        // 2 select statements but can't be done better.
        return when(select(property, propertyId).isNotNull())
                .then(selectString(property, propertyId))
                .otherwise("N/A");
    }

    public static List<OntologyClass> prepareOnsetOntologyClasses(Ontology hpo) {
        if (hpo == null) {
            return List.of();
        }

        TermId primaryTerm = hpo.getPrimaryTermId(HpoOnsetTermIds.ONSET);
        return OntologyAlgorithm.getDescendents(hpo, primaryTerm).stream()
                .map(tid -> OntologyClass.of(tid, hpo.getTermMap().get(tid).getName()))
                .sorted(Comparator.comparing(OntologyClass::getLabel))
                .toList();
    }
}
