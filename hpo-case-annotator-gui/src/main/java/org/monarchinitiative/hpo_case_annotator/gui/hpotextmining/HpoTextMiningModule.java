package org.monarchinitiative.hpo_case_annotator.gui.hpotextmining;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.monarchinitiative.hpo_case_annotator.gui.hpotextmining.biolark.BiolarkHPOMiner;
import org.monarchinitiative.hpo_case_annotator.gui.hpotextmining.controllers.Configure;
import org.monarchinitiative.hpo_case_annotator.gui.hpotextmining.controllers.Main;
import org.monarchinitiative.hpo_case_annotator.gui.hpotextmining.controllers.OntologyTree;
import org.monarchinitiative.hpo_case_annotator.gui.hpotextmining.controllers.Present;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.0.1
 * @since 0.0
 */
public class HpoTextMiningModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Main.class)
                .in(Singleton.class);
        bind(OntologyTree.class)
                .in(Singleton.class);
        bind(Configure.class)
                .in(Singleton.class);
        bind(Present.class)
                .in(Singleton.class);
    }


    @Provides
    private HPOMiner hpoMiner(Properties properties) throws MalformedURLException {
        return new BiolarkHPOMiner(new URL(properties.getProperty("text.mining.url")));
    }
}
