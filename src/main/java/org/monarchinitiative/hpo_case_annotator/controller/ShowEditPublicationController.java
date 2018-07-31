package org.monarchinitiative.hpo_case_annotator.controller;

import com.genestalker.springscreen.core.DialogController;
import com.genestalker.springscreen.core.FXMLDialog;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.monarchinitiative.hpo_case_annotator.model.Publication;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class is the controller for dialog that allows user to view/edit attributes of the current publication. Dialog
 * window with {@link javafx.scene.layout.GridPane} layout is presented where individual publication fields correspond
 * to attributes of {@link Publication} model class.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 1.0.1
 * @since 1.0
 */
public final class ShowEditPublicationController implements DialogController {

    private FXMLDialog dialog;

    private Publication publication;

    @FXML
    private TextField titleTextField;

    @FXML
    private TextField authorsTextField;

    @FXML
    private TextField journalTextField;

    @FXML
    private TextField yearTextField;

    @FXML
    private TextField volumeTextField;

    @FXML
    private TextField pagesTextField;

    @FXML
    private TextField pmidTextField;


    @FXML
    void closeButtonAction() {
        dialog.close();
    }


    /**
     * {@inheritDoc}
     *
     * @param dialog
     */
    @Override
    public void setDialog(FXMLDialog dialog) {
        this.dialog = dialog;
    }


    /**
     * {@inheritDoc}
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        titleTextField.setText(null);
        authorsTextField.setText(null);
        journalTextField.setText(null);
        yearTextField.setText(null);
        volumeTextField.setText(null);
        pagesTextField.setText(null);
        pmidTextField.setText(null);
    }


    /**
     * Get new instance of {@link Publication} containing the actual attribute values. Used for testing purposes.
     *
     * @return {@link Publication} with actual data
     */
    Publication getPublication() {
        return new Publication(publication.getAuthorlist(), publication.getTitle(), publication.getJournal(),
                publication.getYear(), publication.getVolume(), publication.getPages(), publication.getPmid());
    }


    /**
     * Set publication which will be viewed/edited.
     *
     * @param publication {@link Publication} to be viewed/edited.
     */
    public void setPublication(Publication publication) {
        this.publication = publication;
        titleTextField.textProperty().bindBidirectional(this.publication.titleProperty());
        authorsTextField.textProperty().bindBidirectional(this.publication.authorlistProperty());
        journalTextField.textProperty().bindBidirectional(this.publication.journalProperty());
        yearTextField.textProperty().bindBidirectional(this.publication.yearProperty());
        volumeTextField.textProperty().bindBidirectional(this.publication.volumeProperty());
        pagesTextField.textProperty().bindBidirectional(this.publication.pagesProperty());
        pmidTextField.textProperty().bindBidirectional(this.publication.pmidProperty());
    }
}
