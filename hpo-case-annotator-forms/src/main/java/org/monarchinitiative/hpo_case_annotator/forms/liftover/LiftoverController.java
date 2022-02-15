package org.monarchinitiative.hpo_case_annotator.forms.liftover;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import org.monarchinitiative.hpo_case_annotator.core.liftover.LiftOverService;
import org.monarchinitiative.hpo_case_annotator.forms.util.ContigNameStringConverter;
import org.monarchinitiative.hpo_case_annotator.forms.util.Formats;
import org.monarchinitiative.svart.Contig;


public class LiftoverController {

    private final ObjectProperty<LiftOverService> liftoverService = new SimpleObjectProperty<>(this, "liftoverService");
    private final ObjectProperty<LiftOverService.ContigPosition> lifted = new SimpleObjectProperty<>(this, "lifted");
    private final TextFormatter<Number> sourceTextFormatter = Formats.numberFormatter();

    @FXML
    private ComboBox<String> sourceAssembly;
    @FXML
    private ComboBox<Contig> sourceContig;
    @FXML
    private TextField sourcePosition;
    @FXML
    private ComboBox<String> targetAssembly;
    @FXML
    private Label resultContigLabel;
    @FXML
    private TextField resultPosition;
    @FXML
    private Label messageLabel;


    @FXML
    private void initialize() {
        liftoverService.addListener(liftOverServiceListener());
        sourceAssembly.valueProperty().addListener(sourceGenomicAssemblyListener());
        sourcePosition.setTextFormatter(sourceTextFormatter);
        sourceContig.setConverter(ContigNameStringConverter.getInstance());

        lifted.bind(createLiftoverCalculation());
        lifted.addListener(updateResultFields());
    }

    private InvalidationListener liftOverServiceListener() {
        return obs -> {
            LiftOverService service = liftoverService.get();
            if (service != null) {
                if (service.sourceGenomicAssemblies().isEmpty()) {
                    messageLabel.setText("Liftover is not set up");
                } else {
                    sourceAssembly.getItems().setAll(service.sourceGenomicAssemblyNames());
                    messageLabel.setText(null);
                }
            } else {
                messageLabel.setText("Liftover is not set up");
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
                        messageLabel.setText("Liftover is not set up");
                        return null;
                    }
                    String target = targetAssembly.getValue();
                    if (target == null) {
                        messageLabel.setText("Set target assembly");
                        return null;
                    }

                    Contig source = sourceContig.getValue();
                    if (source == null) {
                        messageLabel.setText("Set contig");
                        return null;
                    }
                    Number pos = sourceTextFormatter.getValue();
                    if (pos == null) {
                        messageLabel.setText("Set position");
                        return null;
                    }

                    LiftOverService.ContigPosition position = new LiftOverService.ContigPosition(source.ucscName(), pos.intValue());
                    return service.liftOver(position, sourceAssembly.getValue(), target)
                            .orElse(null);
                },
                sourceAssembly.valueProperty(), targetAssembly.valueProperty(), sourceContig.valueProperty(), sourceTextFormatter.valueProperty());
    }

    private ChangeListener<LiftOverService.ContigPosition> updateResultFields() {
        return (obs, old, novel) -> {
            if (novel == null) {
                resultContigLabel.setText(null);
                resultPosition.clear();
                messageLabel.setText(String.format("Unable to convert %s:%d", sourceContig.getValue().ucscName(), sourceTextFormatter.getValue()));
            } else {
                resultContigLabel.setText(novel.contig());
                resultPosition.setText(String.valueOf(novel.position()));
                messageLabel.setText("Success!");
            }
        };
    }

    public ObjectProperty<LiftOverService> liftoverServiceProperty() {
        return liftoverService;
    }
}
