package org.monarchinitiative.hpo_case_annotator;

import ontologizer.io.obo.OBOParser;
import ontologizer.io.obo.OBOParserException;
import ontologizer.io.obo.OBOParserFileInput;
import ontologizer.ontology.Ontology;
import ontologizer.ontology.TermContainer;
import org.monarchinitiative.hpo_case_annotator.controller.*;
import org.monarchinitiative.hpo_case_annotator.gui.application.HRMDResourceManager;
import org.monarchinitiative.hpo_case_annotator.io.JSONModelParser;
import org.monarchinitiative.hpo_case_annotator.io.ModelParser;
import org.monarchinitiative.hpo_case_annotator.io.XMLModelParser;
import org.monarchinitiative.hpo_case_annotator.validation.CompletenessValidator;
import org.monarchinitiative.hpo_case_annotator.validation.GenomicPositionValidator;
import org.monarchinitiative.hpo_case_annotator.validation.PubMedValidator;
import org.monarchinitiative.hpo_case_annotator.validation.ValidationRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@PropertySource("classpath:/test-hrmd.properties")
public class TestApplicationConfig {

    @Autowired
    private Environment env;


    @Bean
    public HRMDResourceManager initializedHrmdResourceManager() throws IOException {
        return new HRMDResourceManager(new File(env.getProperty("test.hrmd.resources")));
    }


    @Bean
    public JSONModelParser jsonModelParser() {
        return new JSONModelParser(env.getProperty("test.json.model.dir"));
    }


    @Bean
    public XMLModelParser xmlModelParser() {
        return new XMLModelParser(env.getProperty("test.xml.model.dir"));
    }


    @Bean
    public ModelParser modelParser() {
        return new XMLModelParser(env.getProperty("test.xml.model.dir"));
    }


    @Bean
    public ExecutorService executorService() {
        return Executors.newCachedThreadPool(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        });
    }


    @Bean
    @Lazy
    public Ontology ontology() throws IOException, OBOParserException {
        OBOParser parser = new OBOParser(new OBOParserFileInput(env.getProperty("test.hp.obo.path")),
                OBOParser.PARSE_DEFINITIONS);
        String result = parser.doParse();
        TermContainer termContainer = new TermContainer(parser.getTermMap(), parser.getFormatVersion(), parser
                .getDate());
        return Ontology.create(termContainer);
    }


    @Bean
    public ValidationRunner validationRunner() {
        return new ValidationRunner();
    }


    @Bean
    public GenomicPositionValidator genomicPositionValidator() {
        return new GenomicPositionValidator(env.getProperty("test.genome.dir"));
    }


    @Bean
    public CompletenessValidator completenessValidator() {
        return new CompletenessValidator();
    }


    @Bean
    public PubMedValidator pubMedValidator() {
        return new PubMedValidator();
    }

    // ___________________________________ CONTROLLERS ________________________________________________ //


    /**
     * @return {@link SetResourcesController} instance.
     */
    @Bean
    public SetResourcesController setResourcesController() {
        return new SetResourcesController();
    }


    @Bean
    public ShowResourcesController showResourcesController() {
        return new ShowResourcesController();
    }


    @Bean
    public DataController dataController() {
        return new DataController();
    }


    @Bean
    public ShowHtmlContentController showHtmlContentController() {
        return new ShowHtmlContentController();
    }


    @Bean
    public ShowEditPublicationController showEditPublicationController() {
        return new ShowEditPublicationController();
    }

}
