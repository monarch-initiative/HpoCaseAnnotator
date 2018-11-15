package org.monarchinitiative.hpo_case_annotator.gui;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface GuiceModules {

    Class<?>[] value();
}
