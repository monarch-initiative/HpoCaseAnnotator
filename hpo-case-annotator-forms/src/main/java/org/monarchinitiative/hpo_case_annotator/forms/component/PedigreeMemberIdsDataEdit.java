package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.fxml.FXML;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;

public class PedigreeMemberIdsDataEdit extends BaseIndividualIdsDataEdit<ObservablePedigreeMember> {

    @FXML
    private TitledTextField paternalId;
    @FXML
    private TitledTextField maternalId;
    @FXML
    private TitledCheckBox proband;

    public PedigreeMemberIdsDataEdit() {
        super(PedigreeMemberIdsDataEdit.class.getResource("PedigreeMemberIdsDataEdit.fxml"));
    }


    @Override
    public void setInitialData(ObservablePedigreeMember data) {
        super.setInitialData(data);

        paternalId.setText(data.getPaternalId().orElse(null));
        maternalId.setText(data.getMaternalId().orElse(null));
        proband.setSelected(data.isProband());
    }

    @Override
    public void commit() {
        super.commit();

        item.setPaternalId(paternalId.getText());
        item.setMaternalId(maternalId.getText());
        item.setProband(proband.isSelected());
    }

}
