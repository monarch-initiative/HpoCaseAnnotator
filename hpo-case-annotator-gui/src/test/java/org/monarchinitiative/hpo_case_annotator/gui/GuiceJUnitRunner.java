package org.monarchinitiative.hpo_case_annotator.gui;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.0.1
 * @since 0.0
 */
public class GuiceJUnitRunner extends BlockJUnit4ClassRunner {

    private Injector injector;


    public GuiceJUnitRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
        Class<?>[] classes = getModulesFor(clazz);
        injector = createInjectorFor(classes);
    }


    @Override
    public Object createTest() throws Exception {
        Object obj = super.createTest();
        injector.injectMembers(obj);
        return obj;
    }


    private Injector createInjectorFor(Class<?>[] classes) throws InitializationError {
        Module[] modules = new Module[classes.length];
        for (int i = 0; i < classes.length; i++) {
            try {
                modules[i] = (Module) (classes[i]).newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new InitializationError(e);
            }
        }
        return Guice.createInjector(modules);
    }


    private Class<?>[] getModulesFor(Class<?> clazz) throws InitializationError {
        GuiceModules annotation = clazz.getAnnotation(GuiceModules.class);
        if (annotation == null)
            throw new InitializationError(
                    "Missing @GuiceModules annotation for unit test '" + clazz.getName()
                            + "'");
        return annotation.value();
    }
}