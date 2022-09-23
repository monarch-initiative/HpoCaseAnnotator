package org.monarchinitiative.hpo_case_annotator.app.dialogs;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Optional;

public class Dialogs {

    public static void showInfoDialog(String title, String header, String content) {
        showDialog(title, header, content, Alert.AlertType.INFORMATION);
    }

    public static void showWarningDialog(String title, String header, String content) {
        showDialog(title, header, content, Alert.AlertType.WARNING);
    }

    public static void showErrorDialog(String title, String header, String content) {
        showDialog(title, header, content, Alert.AlertType.ERROR);
    }

    private static void showDialog(String windowTitle, String header, String contentText, Alert.AlertType alertType) {
        Alert a = new Alert(alertType);
        a.setTitle(windowTitle);
        a.setHeaderText(header);
        a.setContentText(contentText);
        a.showAndWait();
    }

    /**
     * Ask user a boolean question and get an answer.
     *
     * @param title Title of PopUp window
     * @return true or false according to the user input
     */
    public static Optional<ButtonType> getBooleanFromUser(String title, String header, String question) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(question);

        return alert.showAndWait();
    }

    /**
     * Ask user to choose a directory
     *
     * @param ownerWindow      - Stage with which the DirectoryChooser will be associated
     * @param initialDirectory - Where to start the search
     * @param title            - Title of PopUp window
     * @return {@link Path} selected by user or <code>null</code>, if user closed dialog window without choosing
     * anything
     */
    public static Path selectDirectory(Stage ownerWindow, Path initialDirectory, String title) {
        final DirectoryChooser dirchooser = new DirectoryChooser();
        dirchooser.setInitialDirectory(initialDirectory.toFile());
        dirchooser.setTitle(title);
        File file = dirchooser.showDialog(ownerWindow);

        return file == null ? null : file.toPath();
    }

    @Deprecated(since = "2.0.0", forRemoval = true)
    public static void showException(String windowTitle, String header, String contentText, Throwable exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(windowTitle);
        alert.setHeaderText(header);
        alert.setContentText(contentText);
        alert.getDialogPane().setPrefSize(1000, 800);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

}
