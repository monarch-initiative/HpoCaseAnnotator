package org.monarchinitiative.hpo_case_annotator.forms.liftover;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.core.liftover.LiftOverService;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledComboBox;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledLabel;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledTextField;
import org.monarchinitiative.hpo_case_annotator.forms.status.StatusBar;
import org.monarchinitiative.hpo_case_annotator.forms.util.ContigNameStringConverter;
import org.monarchinitiative.svart.Contig;

import java.io.IOException;

public class Liftover extends VBox {

    private final ObjectProperty<LiftOverService> liftoverService = new SimpleObjectProperty<>(this, "liftoverService");
    private final ObjectProperty<LiftOverService.ContigPosition> lifted = new SimpleObjectProperty<>(this, "lifted");

    @FXML
    private TitledComboBox<String> sourceAssembly;
    @FXML
    private TitledComboBox<Contig> sourceContig;
    @FXML
    private TitledTextField sourcePosition;
    @FXML
    private TitledComboBox<String> targetAssembly;
    @FXML
    private TitledLabel resultContigLabel;
    @FXML
    private TitledTextField resultPosition;
    @FXML
    private StatusBar statusBar;

    public Liftover() {
        FXMLLoader loader = new FXMLLoader(Liftover.class.getResource("Liftover.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ObjectProperty<LiftOverService> liftoverServiceProperty() {
        return liftoverService;
    }

    @FXML
    private void initialize() {
        disableProperty().bind(liftoverService.isNull());

        liftoverService.addListener(liftOverServiceListener());
        sourceAssembly.valueProperty().addListener(sourceGenomicAssemblyListener());
        sourceContig.getTitledItem().setConverter(ContigNameStringConverter.getInstance());

        lifted.bind(createLiftoverCalculation());
        lifted.addListener(updateResultFields());
    }

    private InvalidationListener liftOverServiceListener() {
        return obs -> {
            LiftOverService service = liftoverService.get();
            if (service != null) {
                if (service.sourceGenomicAssemblies().isEmpty()) {
                    statusBar.setMessage("Liftover is not set up");
                } else {
                    sourceAssembly.getItems().setAll(service.sourceGenomicAssemblyNames());
                    statusBar.setMessage("Set source genomic assembly");
                }
            } else {
                statusBar.setMessage("Liftover is not set up");
            }
        };
    }

    private InvalidationListener sourceGenomicAssemblyListener() {
        return obs -> {
            LiftOverService service = liftoverService.get();
            if (service == null) {
                targetAssembly.getItems().clear();
                sourceContig.getItems().clear();
            } else {
                String novel = sourceAssembly.getValue();
                targetAssembly.getItems().setAll(service.supportedConversions(novel));
                service.sourceGenomicAssemblies().stream()
                        .filter(ga -> ga.name().equals(novel))
                        .findFirst()
                        .ifPresent(ga -> sourceContig.getItems().setAll(ga.contigs()));
            }
        };
    }

    private ObjectBinding<LiftOverService.ContigPosition> createLiftoverCalculation() {
        return Bindings.createObjectBinding(() -> {
                    LiftOverService service = liftoverService.get();
                    if (service == null) {
                        statusBar.setMessage("Liftover is not set up");
                        return null;
                    }
                    String target = targetAssembly.getValue();
                    if (target == null) {
                        statusBar.setMessage("Set target genomic assembly");
                        return null;
                    }

                    Contig source = sourceContig.getValue();
                    if (source == null) {
                        statusBar.setMessage("Set source contig");
                        return null;
                    }
                    int pos;
                    try {
                        pos = Integer.parseInt(sourcePosition.getText());
                    } catch (NumberFormatException e) {
                        statusBar.setMessage("Set source position");
                        return null;
                    }

                    LiftOverService.ContigPosition position = new LiftOverService.ContigPosition(source.ucscName(), pos);
                    return service.liftOver(position, sourceAssembly.getValue(), target)
                            .orElse(null);
                },
                sourceAssembly.valueProperty(), targetAssembly.valueProperty(), sourceContig.valueProperty(), sourcePosition.textProperty());
    }

    private ChangeListener<LiftOverService.ContigPosition> updateResultFields() {
        return (obs, old, novel) -> {
            if (novel == null) {
                resultContigLabel.setText(null);
                resultPosition.getTitledItem().clear();
                statusBar.setMessage(String.format("Unable to convert %s:%s", sourceContig.getValue().ucscName(), sourcePosition.getText()));
            } else {
                resultContigLabel.setText(novel.contig());
                resultPosition.setText(String.valueOf(novel.position()));
                statusBar.setMessage("Success!");
            }
        };
    }

}
