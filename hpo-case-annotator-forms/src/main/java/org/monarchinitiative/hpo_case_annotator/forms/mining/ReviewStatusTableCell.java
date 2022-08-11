package org.monarchinitiative.hpo_case_annotator.forms.mining;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

class ReviewStatusTableCell extends TableCell<ObservableMinedTerm, ReviewStatus> {

    private static final Image APPROVED = new Image(Objects.requireNonNull(ReviewStatusTableCell.class.getResourceAsStream("approved.png")));
    private static final Image REJECTED = new Image(Objects.requireNonNull(ReviewStatusTableCell.class.getResourceAsStream("rejected.png")));

    ReviewStatusTableCell() {
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    @Override
    protected void updateItem(ReviewStatus item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null || item.equals(ReviewStatus.UNREVIEWED)) {
            // no-op
            setGraphic(null);
        } else {
            switch (item) {
                case APPROVED -> setGraphic(new ImageView(APPROVED));
                case REJECTED -> setGraphic(new ImageView(REJECTED));
            }
        }
    }
}
