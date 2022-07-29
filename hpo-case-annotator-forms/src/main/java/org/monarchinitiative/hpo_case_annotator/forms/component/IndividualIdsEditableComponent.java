package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.DataEditController;
import org.monarchinitiative.hpo_case_annotator.model.v2.Sex;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;

import java.io.IOException;

public class IndividualIdsEditableComponent extends VBox implements DataEditController<ObservablePedigreeMember> {

    // TODO - setup icon based on sex and proband status.
    private static final String DEFAULT_STYLECLASS = "individual-ids-component";

    private final ObjectProperty<ObservablePedigreeMember> item = new SimpleObjectProperty<>();

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
    private AgeEditableComponent ageComponent;

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

        item.addListener((obs, o, newPedigreeMember) -> {
            if (newPedigreeMember == null)
                clearForm();
            else {
                setPedigreeMemberData(newPedigreeMember);
            }
        });
    }

    private void setPedigreeMemberData(ObservablePedigreeMember member) {
        individualId.setText(member.getId());
        paternalId.setText(member.getPaternalId().orElse(null));
        maternalId.setText(member.getMaternalId().orElse(null));
        sex.setValue(member.getSex());
        proband.setSelected(member.isProband());
        ageComponent.setInitialData(member.getObservableAge());
    }


    private void clearForm() {
        individualId.setText(null);
        paternalId.setText(null);
        maternalId.setText(null);

        sex.setValue(null);
        proband.setSelected(false);
        ageComponent.setInitialData(null);
    }

    @Override
    public void setInitialData(ObservablePedigreeMember item) {
        this.item.set(item);
    }

    @Override
    public ObservablePedigreeMember getEditedData() {
        ObservablePedigreeMember item = this.item.get();
        if (item != null) {
            item.setId(individualId.getText());
            item.setPaternalId(paternalId.getText());
            item.setMaternalId(maternalId.getText());
            item.setSex(sex.getValue());
            item.setProband(proband.isSelected());
            item.setObservableAge(ageComponent.getEditedData());
        }

        return item;
    }

}
