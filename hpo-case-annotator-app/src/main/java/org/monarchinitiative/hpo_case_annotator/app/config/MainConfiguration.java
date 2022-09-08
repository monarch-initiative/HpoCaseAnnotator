package org.monarchinitiative.hpo_case_annotator.app.config;

import org.monarchinitiative.hpo_case_annotator.app.UrlBrowser;
import org.monarchinitiative.hpo_case_annotator.app.controller.ControllerFactory;
import org.monarchinitiative.hpo_case_annotator.app.publication.PublicationBrowser;
import org.monarchinitiative.hpo_case_annotator.app.publication.PubmedPublicationBrowser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class MainConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainConfiguration.class);

    @Bean
    public ControllerFactory controllerFactory(ApplicationContext applicationContext) {
        return applicationContext::getBean;
    }

    @Bean
    public ExecutorService executorService() {
        int nThreads = Runtime.getRuntime().availableProcessors();
        LOGGER.debug("Creating executor with {} threads", nThreads);
        return Executors.newFixedThreadPool(nThreads);
    }

    @Bean
    public PublicationBrowser publicationBrowser(UrlBrowser urlBrowser) {
        return new PubmedPublicationBrowser(urlBrowser);
    }

}
