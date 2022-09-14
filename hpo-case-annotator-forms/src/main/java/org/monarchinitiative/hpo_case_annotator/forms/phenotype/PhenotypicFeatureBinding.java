package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.forms.component.age.TimeElementBindingComponent;
import org.monarchinitiative.hpo_case_annotator.model.v2.TimeElement;
import org.monarchinitiative.hpo_case_annotator.observable.v2.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

public class PhenotypicFeatureBinding extends PhenotypicFeatureBase implements Observable {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhenotypicFeatureBinding.class);

    static final Callback<PhenotypicFeatureBinding, Stream<Observable>> EXTRACTOR = pfb -> Stream.of(
            pfb.onsetIsUnknown.selectedProperty(),
            pfb.onsetComponent,
            pfb.resolutionIsUnknown.selectedProperty(),
            pfb.resolutionComponent
    );

    @FXML
    private CheckBox onsetIsUnknown;
    @FXML
    private TimeElementBindingComponent onsetComponent;
    @FXML
    private CheckBox resolutionIsUnknown;
    @FXML
    private TimeElementBindingComponent resolutionComponent;

    private boolean valueIsNotBeingSetByUserInteraction;

    public PhenotypicFeatureBinding() {
        super(PhenotypicFeatureBinding.class.getResource("PhenotypicFeatureBinding.fxml"));
    }

    @FXML
    protected void initialize() {
        super.initialize();

        onsetComponent.visibleProperty().bind(onsetIsUnknown.selectedProperty().not());
        resolutionComponent.visibleProperty().bind(resolutionIsUnknown.selectedProperty().not());

        addListener(obs -> {
            if (valueIsNotBeingSetByUserInteraction)
                return;

            ObservablePhenotypicFeature data = this.data.get();
            if (data == null)
                return;

            // We handle change of onset/resolution known status separately.
            if (obs.equals(onsetIsUnknown.selectedProperty())) {
                if (onsetIsUnknown.isSelected()) {
                    data.setOnset(null);
                    onsetComponent.setData(null);
                } else {
                    ObservableTimeElement onset = new ObservableTimeElement();
                    data.setOnset(onset);
                    onsetComponent.setData(onset);
                }
            } else if (obs.equals(resolutionIsUnknown.selectedProperty())) {
                if (resolutionIsUnknown.isSelected()) {
                    data.setResolution(null);
                    resolutionComponent.setData(null);
                } else {
                    ObservableTimeElement resolution = new ObservableTimeElement();
                    data.setResolution(resolution);
                    resolutionComponent.setData(resolution);
                }
            } else {
                // Otherwise re-compute the state of the onset and resolution.
                if (!onsetIsUnknown.isSelected())
                    copyTimeElement(onsetComponent.getData(), data.getOnset());
                if (!resolutionIsUnknown.isSelected())
                    copyTimeElement(resolutionComponent.getData(), data.getResolution());
            }
        });
    }

    @Override
    protected void bind(ObservablePhenotypicFeature data) {
        super.bind(data);
        try {
            valueIsNotBeingSetByUserInteraction = true;
            if (data == null) {
                clear();
            } else {
                onsetIsUnknown.setSelected(data.getOnset() == null);
                onsetComponent.setData(data.getOnset());
                resolutionIsUnknown.setSelected(data.getResolution() == null);
                resolutionComponent.setData(data.getResolution());
            }
        } finally {
            valueIsNotBeingSetByUserInteraction = false;
        }
    }

    @Override
    protected void unbind(ObservablePhenotypicFeature data) {
        super.unbind(data);
        try {
            valueIsNotBeingSetByUserInteraction = true;
            clear();
        } finally {
            valueIsNotBeingSetByUserInteraction = false;
        }
    }

    private void clear() {
        onsetIsUnknown.setSelected(true);
        onsetComponent.setData(null);
        resolutionIsUnknown.setSelected(true);
        resolutionComponent.setData(null);
    }

    @Override
    public void addListener(InvalidationListener listener) {
        EXTRACTOR.call(this).forEach(obs -> obs.addListener(listener));
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        EXTRACTOR.call(this).forEach(obs -> obs.removeListener(listener));
    }

    private static void copyTimeElement(ObservableTimeElement source, ObservableTimeElement target) {
        TimeElement.TimeElementCase tec = source.getTimeElementCase();
        if (tec == null) {
            // Should not happen but let's be careful
            LOGGER.warn("Time element case should have been set but it is not!");
            return;
        }

        target.setTimeElementCase(tec);
        switch (tec) {
            case GESTATIONAL_AGE -> {
                ObservableGestationalAge sourceGestationalAge = source.getGestationalAge();
                ObservableGestationalAge targetGestationalAge = target.getGestationalAge();
                if (sourceGestationalAge == targetGestationalAge)
                    return;

                targetGestationalAge.setWeeks(sourceGestationalAge.getWeeks());
                targetGestationalAge.setDays(sourceGestationalAge.getDays());
            }
            case AGE -> copyAge(source.getAge(), target.getAge());
            case AGE_RANGE -> {
                ObservableAgeRange sourceAgeRange = source.getAgeRange();
                ObservableAgeRange targetAgeRange = target.getAgeRange();
                if (sourceAgeRange == targetAgeRange)
                    return;
                if (sourceAgeRange.getStart() != null && targetAgeRange.getStart() == null)
                    targetAgeRange.setStart(new ObservableAge());
                copyAge(sourceAgeRange.getStart(), targetAgeRange.getStart());

                if (sourceAgeRange.getEnd() != null && targetAgeRange.getEnd() == null)
                    targetAgeRange.setEnd(new ObservableAge());
                copyAge(sourceAgeRange.getEnd(), targetAgeRange.getEnd());
            }
            case ONTOLOGY_CLASS -> target.setOntologyClass(source.getOntologyClass());
        }
    }

    private static void copyAge(ObservableAge sourceAge, ObservableAge targetAge) {
        if (sourceAge == targetAge)
            return;
        targetAge.setYears(sourceAge.getYears());
        targetAge.setMonths(sourceAge.getMonths());
        targetAge.setDays(sourceAge.getDays());
    }
}
