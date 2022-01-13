package org.monarchinitiative.hpo_case_annotator.app.config;

import org.monarchinitiative.hpo_case_annotator.app.model.OptionalResources;
import org.monarchinitiative.hpo_case_annotator.app.model.genome.GenomicLocalResource;
import org.monarchinitiative.hpo_case_annotator.core.reference.GenomicAssemblyService;
import org.monarchinitiative.hpo_case_annotator.core.reference.InvalidFastaFileException;
import org.monarchinitiative.hpo_case_annotator.forms.GenomicAssemblyRegistry;
import org.monarchinitiative.hpo_case_annotator.core.liftover.LiftOverAdapter;
import org.monarchinitiative.hpo_case_annotator.forms.HCAControllerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class MainConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainConfiguration.class);

    @Bean
    public GenomicAssemblyRegistry genomicAssemblyRegistry(OptionalResources optionalResources) {
        GenomicAssemblyRegistry registry = new GenomicAssemblyRegistry();
        // hg18
        optionalResources.getGenomicLocalResources().hg18Property().addListener((obs, old, novel) -> {
            if (novel != null) {
                registry.setHg18Service(createGenomicAssemblyService(novel));
            }
        });
        // hg19
        optionalResources.getGenomicLocalResources().hg19Property().addListener((obs, old, novel) -> {
            if (novel != null) {
                registry.setHg19Service(createGenomicAssemblyService(novel));
            }
        });
        // hg38
        optionalResources.getGenomicLocalResources().hg38Property().addListener((obs, old, novel) -> {
            if (novel != null) {
                registry.setHg38Service(createGenomicAssemblyService(novel));
            }
        });

        return registry;
    }

    private static GenomicAssemblyService createGenomicAssemblyService(GenomicLocalResource novel) {
        try {
            return GenomicAssemblyService.of(novel.getAssemblyReport(), novel.getFasta(), novel.getFastaFai(), novel.getFastaDict());
        } catch (InvalidFastaFileException e) {
            LOGGER.warn("Error while setting hg19 genomic assembly service: ", e);
            return null;
        }
    }

    @Bean
    public OptionalResources optionalResources() {
        return new OptionalResources();
    }

    @Bean
    public HCAControllerFactory hcaControllerFactory(ApplicationContext applicationContext) {
        return applicationContext::getBean;
    }

    @Bean
    public LiftOverAdapter liftOverAdapter(File liftoverDir) {
        LOGGER.debug("Creating Liftover adapter from chain files located at `{}`", liftoverDir.getAbsolutePath());
        return LiftOverAdapter.ofChainFolder(liftoverDir);
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }
}