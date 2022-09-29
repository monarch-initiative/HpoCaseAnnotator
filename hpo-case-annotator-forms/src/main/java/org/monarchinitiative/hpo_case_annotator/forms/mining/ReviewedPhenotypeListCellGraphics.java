package org.monarchinitiative.hpo_case_annotator.forms.mining;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.monarchinitiative.hpo_case_annotator.forms.base.FlowPaneBindingObservableDataComponent;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledLabel;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledTimeElementSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static javafx.beans.binding.Bindings.when;

class ReviewedPhenotypeListCellGraphics extends FlowPaneBindingObservableDataComponent<ObservableReviewedPhenotypicFeature> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewedPhenotypeListCellGraphics.class);

    private static final Map<ReviewStatus, Image> IMAGE_MAP = readImages();
    private static final String PRESENT = "Present";
    private static final String EXCLUDED = "Excluded";

    @FXML
    private ImageView reviewStatus;
    @FXML
    private TitledLabel term;
    @FXML
    private TitledLabel status;
    @FXML
    private TitledTimeElementSummary onset;
    @FXML
    private TitledTimeElementSummary resolution;

    private ChangeListener<? super ReviewStatus> reviewStatusListener;

    ReviewedPhenotypeListCellGraphics() {
        FXMLLoader loader = new FXMLLoader(ReviewedPhenotypeListCellGraphics.class.getResource("ReviewedPhenotypeListCellGraphics.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<ReviewStatus, Image> readImages() {
        Map<ReviewStatus, Image> map = new HashMap<>();
        try {
            map.put(ReviewStatus.APPROVED, loadImage("approved.png"));
            map.put(ReviewStatus.REJECTED, loadImage("rejected.png"));
            map.put(ReviewStatus.UNREVIEWED, loadImage("unreviewed.png"));
        } catch (IOException e) {
            LOGGER.warn("Error loading an image: {}", e.getMessage());
            LOGGER.debug("Error loading an image: {}", e.getMessage(), e);
        }

        return Map.copyOf(map);
    }

    private static Image loadImage(String name) throws IOException {
        try (InputStream is = ReviewedPhenotypeListCellGraphics.class.getResourceAsStream(name)) {
            return new Image(is);
        }
    }

    @Override
    protected void initialize() {
        super.initialize();

        reviewStatusListener = (obs, old, novel) -> reviewStatus.setImage(IMAGE_MAP.get(novel));
    }

    @Override
    protected void bind(ObservableReviewedPhenotypicFeature data) {
        if (data == null) {
            term.setName(TextMining.N_A);
            term.setText(TextMining.N_A);
            status.setText(null);
            onset.timeElementProperty().set(null);
            resolution.timeElementProperty().set(null);
        } else {
            reviewStatus.setImage(IMAGE_MAP.get(data.getReviewStatus()));
            data.reviewStatusProperty().addListener(reviewStatusListener);
            term.setName(data.getTermId().getValue());
            term.setText(data.getLabel());
            status.textProperty().bind(when(data.excludedProperty()).then(EXCLUDED).otherwise(PRESENT));
            onset.timeElementProperty().bind(data.onsetProperty());
            resolution.timeElementProperty().bind(data.resolutionProperty());
        }
    }

    @Override
    protected void unbind(ObservableReviewedPhenotypicFeature data) {
        if (data != null) {
            data.reviewStatusProperty().removeListener(reviewStatusListener);
            status.textProperty().unbind();
            onset.timeElementProperty().unbind();
            resolution.timeElementProperty().unbind();
        }
    }

}
