package org.monarchinitiative.hpo_case_annotator.forms.pedigree;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import org.monarchinitiative.hpo_case_annotator.core.data.DiseaseIdentifierService;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableCuratedVariant;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;
import org.monarchinitiative.phenol.ontology.data.Ontology;

class ObservablePedigreeMemberListCell extends ListCell<ObservablePedigreeMember> {

    private final PedigreeMemberPane pedigreeMemberPane;

    ObservablePedigreeMemberListCell(ListProperty<ObservableCuratedVariant> variants,
                                     ObjectProperty<Ontology> hpo,
                                     ObjectProperty<DiseaseIdentifierService> diseaseIdentifierService) {
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        pedigreeMemberPane = new PedigreeMemberPane();

        pedigreeMemberPane.prefWidthProperty().bind(maxWidthProperty());
        pedigreeMemberPane.dataProperty().bind(itemProperty()); // TODO - evaluate if this is good/necessary
        pedigreeMemberPane.variantsProperty().bind(variants);
        pedigreeMemberPane.hpoProperty().bind(hpo);
        pedigreeMemberPane.diseaseIdentifierServiceProperty().bind(diseaseIdentifierService);
    }

    @Override
    protected void updateItem(ObservablePedigreeMember item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            setGraphic(pedigreeMemberPane);
        }
    }
}
