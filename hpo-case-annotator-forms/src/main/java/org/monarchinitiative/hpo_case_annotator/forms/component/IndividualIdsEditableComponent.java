package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.DataEditController;
import org.monarchinitiative.hpo_case_annotator.forms.component.age.TimeElementEditableComponent;
import org.monarchinitiative.hpo_case_annotator.model.v2.Sex;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;

import java.io.IOException;

public class IndividualIdsEditableComponent extends VBox implements DataEditController<ObservablePedigreeMember> {
    private static final String DEFAULT_STYLECLASS = "individual-ids-component";

    private ObservablePedigreeMember item;

    @FXML
    private Label idLabel;
    @FXML
    private TitledTextField individualId;
    @FXML
    private TitledTextField paternalId;
    @FXML
    private TitledTextField maternalId;
    @FXML
    private TitledComboBox<Sex> sex;
    @FXML
    private TitledCheckBox proband;
    @FXML
    private TimeElementEditableComponent ageComponent;

    public IndividualIdsEditableComponent() {
        getStyleClass().add(DEFAULT_STYLECLASS);
        FXMLLoader loader = new FXMLLoader(IndividualIdsEditableComponent.class.getResource("IndividualIdsEditableComponent.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void initialize() {
        sex.getItems().addAll(Sex.values());
        idLabel.textProperty().bind(individualId.textProperty());
    }

    @Override
    public void setInitialData(ObservablePedigreeMember data) {
        if (data == null) {
            individualId.setText(null);
            paternalId.setText(null);
            maternalId.setText(null);

            sex.setValue(null);
            proband.setSelected(false);
            ageComponent.setInitialData(null);
            item = new ObservablePedigreeMember();
        } else {
            individualId.setText(data.getId());
            paternalId.setText(data.getPaternalId().orElse(null));
            maternalId.setText(data.getMaternalId().orElse(null));
            sex.setValue(data.getSex());
            proband.setSelected(data.isProband());
            ageComponent.setInitialData(data.getAge());
            item = data;
        }
    }

    @Override
    public ObservablePedigreeMember getEditedData() {
        item.setId(individualId.getText());
        item.setPaternalId(paternalId.getText());
        item.setMaternalId(maternalId.getText());
        item.setSex(sex.getValue());
        item.setProband(proband.isSelected());
        item.setAge(ageComponent.getEditedData());

        return item;
    }

}
