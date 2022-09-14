package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

public class PedigreeMemberIdsBindingComponent extends BaseIndividualIdsBindingComponent<ObservablePedigreeMember> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PedigreeMemberIdsBindingComponent.class);

    static final Callback<PedigreeMemberIdsBindingComponent, Stream<Observable>> EXTRACTOR = item -> Stream.of(
            item.paternalId.textProperty(),
            item.maternalId.textProperty(),
            item.proband.selectedProperty()
    );

    @FXML
    private TitledTextField paternalId;
    @FXML
    private TitledTextField maternalId;
    @FXML
    private TitledCheckBox proband;

    public PedigreeMemberIdsBindingComponent() {
        super(PedigreeMemberIdsBindingComponent.class.getResource("PedigreeMemberIdsBindingComponent.fxml"));
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void bind(ObservablePedigreeMember data) {
        try {
            valueIsNotBeingSetByUserInteraction = true;

            super.bind(data);

            if (data == null) { // clear
                paternalId.setText(null);
                maternalId.setText(null);
                proband.setSelected(false);
            } else {
                paternalId.setText(data.getPaternalId().orElse(null));
                maternalId.setText(data.getMaternalId().orElse(null));
                proband.setSelected(data.isProband());
            }
        } finally {
            valueIsNotBeingSetByUserInteraction = false;
        }
    }

    @Override
    protected void unbind(ObservablePedigreeMember data) {
        super.unbind(data);
    }

    @Override
    public void invalidated(Observable obs) {
        super.invalidated(obs);

        if (valueIsNotBeingSetByUserInteraction)
            return;
        ObservablePedigreeMember opm = data.get();
        if (opm == null)
            return;

        if (obs.equals(paternalId.textProperty())) {
            opm.setPaternalId(paternalId.getText());
        } else if (obs.equals(maternalId.textProperty())) {
            opm.setMaternalId(maternalId.getText());
        } else if (obs.equals(proband.selectedProperty())) {
            opm.setProband(proband.isSelected());
        } // fall through

    }

    @Override
    protected Stream<Observable> dependencies() {
        return Stream.concat(
                BaseIndividualIdsBindingComponent.EXTRACTOR.call(this),
                EXTRACTOR.call(this)
        );
    }
}
