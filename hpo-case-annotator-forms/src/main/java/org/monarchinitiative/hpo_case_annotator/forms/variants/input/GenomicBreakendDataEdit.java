package org.monarchinitiative.hpo_case_annotator.forms.variants.input;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.core.reference.genome.GenomicAssemblyService;
import org.monarchinitiative.hpo_case_annotator.forms.VBoxDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.util.Formats;
import org.monarchinitiative.hpo_case_annotator.forms.util.FormUtils;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableGenomicBreakend;
import org.monarchinitiative.svart.Contig;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * <h2>Properties</h2>
 * {@link GenomicBreakendDataEdit} needs the following properties to be set in order to work.
 * <ul>
 *   <li>{@link #genomicAssemblyServiceProperty()}</li>
 * </ul>
 */
public class GenomicBreakendDataEdit extends VBoxDataEdit<ObservableGenomicBreakend> implements Observable {

    private static final Callback<GenomicBreakendDataEdit, Stream<Observable>> EXTRACTOR = bc -> Stream.of(
            bc.contigComboBox.valueProperty(),
            bc.positionTextField.textProperty(),
            bc.idTextField.textProperty(),
            bc.isPositiveStrandCheckBox.selectedProperty());

    private final ObjectProperty<GenomicAssemblyService> genomicAssemblyService = new SimpleObjectProperty<>(null, "assemblyService");

    private ObservableGenomicBreakend item;

    @FXML
    private ComboBox<Contig> contigComboBox;
    @FXML
    private TextField positionTextField;
    @FXML
    private TextField idTextField;
    @FXML
    private CheckBox isPositiveStrandCheckBox;

    public GenomicBreakendDataEdit() {
        FXMLLoader loader = new FXMLLoader(GenomicBreakendDataEdit.class.getResource("GenomicBreakendDataEdit.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ObjectProperty<GenomicAssemblyService> genomicAssemblyServiceProperty() {
        return genomicAssemblyService;
    }

    @FXML
    protected void initialize() {
        FormUtils.initializeContigComboBox(contigComboBox, genomicAssemblyService);
        positionTextField.setTextFormatter(Formats.numberFormatter());
    }

    @Override
    public void setInitialData(ObservableGenomicBreakend data) {
        item = Objects.requireNonNull(data);

        contigComboBox.setValue(data.getContig());
        positionTextField.setText(String.valueOf(data.getPos()));
        idTextField.setText(data.getId());
        isPositiveStrandCheckBox.setSelected(data.getStrand().isPositive());
    }

    @Override
    public void commit() {
        item.setContig(contigComboBox.getValue());
        // TODO - finish
    }

    @Override
    public void addListener(InvalidationListener listener) {
        EXTRACTOR.call(this).forEach(obs -> obs.addListener(listener));
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        EXTRACTOR.call(this).forEach(obs -> obs.removeListener(listener));
    }
}
