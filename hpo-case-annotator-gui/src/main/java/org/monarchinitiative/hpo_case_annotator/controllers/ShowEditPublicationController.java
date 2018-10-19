package org.monarchinitiative.hpo_case_annotator.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.monarchinitiative.hpo_case_annotator.model.proto.Publication;

/**
 * This class is the controller for dialog that allows user to view/edit attributes of the current publication. Dialog
 * window with {@link javafx.scene.layout.GridPane} layout is presented where individual publication fields correspond
 * to attributes of {@link Publication} model class.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 1.0.1
 * @since 1.0
 */
public final class ShowEditPublicationController {


    private final Publication publication;

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


    public ShowEditPublicationController(Publication publication) {
        this.publication = publication;
    }


    public void initialize() {
        authorsTextField.setText(publication.getAuthorList());
        titleTextField.setText(publication.getTitle());
        journalTextField.setText(publication.getJournal());
        yearTextField.setText(publication.getYear());
        volumeTextField.setText(publication.getVolume());
        pagesTextField.setText(publication.getPages());
        pmidTextField.setText(publication.getPmid());
    }


    /**
     * Get new instance of {@link Publication} containing the actual attribute values. Used for testing purposes.
     *
     * @return {@link Publication} with actual data
     */
    Publication getPublication() {
        return Publication.newBuilder()
                .setAuthorList(authorsTextField.getText())
                .setTitle(titleTextField.getText())
                .setJournal(journalTextField.getText())
                .setYear(yearTextField.getText())
                .setVolume(volumeTextField.getText())
                .setPages(pagesTextField.getText())
                .setPmid(pmidTextField.getText())
                .build();
    }

}
