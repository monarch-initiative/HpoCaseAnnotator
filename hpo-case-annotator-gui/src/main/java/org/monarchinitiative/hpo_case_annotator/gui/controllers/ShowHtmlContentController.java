package org.monarchinitiative.hpo_case_annotator.gui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * This class is the controller for dialog that presents content using {@link WebView}. Therefore, content should be in
 * HTML format.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.0.2
 * @since 0.0
 */
public final class ShowHtmlContentController {

    private static final Logger log = LoggerFactory.getLogger(ShowHtmlContentController.class);


    @FXML
    private WebView contentWebView;

    /**
     * Non-visual object capable of managing one website at once.
     */
    private WebEngine webEngine;


    public void initialize() {
        webEngine = contentWebView.getEngine();
    }


    /**
     * Set HTML content that should be displayed in dialog window.
     *
     * @param htmlContent String with HTML content to be displayed in dialog window.
     * @return true if the String was submitted to JavaFX Thread to be displayed.
     */
    public boolean setContent(String htmlContent) {
        if (webEngine != null) {
            Platform.runLater(() -> webEngine.loadContent(htmlContent));
            return true;
        } else {
            log.warn("Unable to load HTML content, process controller class with FXMLLoader first.");
            return false;
        }
    }


    /**
     * Set URL pointing to content that should be displayed in dialog window.
     *
     * @param url {@link URL} object pointing to content.
     * @return true if the URL was submitted to JavaFX Thread to be displayed.
     */
    public boolean setContent(URL url) {
        if (webEngine != null) {
            Platform.runLater(() -> webEngine.load(url.toString()));
            return true;
        } else {
            log.warn(String.format("Unable to load URL %s, process controller class with FXMLLoader first.", url));
            return false;
        }
    }

}
