package org.monarchinitiative.hpo_case_annotator.model.v2;

import java.util.List;

/**
 * A study that involves a group of two or more individuals.
 */
public interface GroupStudy<T extends Individual> extends Study {

    List<? extends T> getMembers();

}
