package org.monarchinitiative.hpo_case_annotator.app;

import java.net.URL;

@FunctionalInterface
public interface UrlBrowser {

    /**
     * Show <em>url</em> in a system browser.
     * @param url document URL
     */
    void showDocument(URL url);

}
