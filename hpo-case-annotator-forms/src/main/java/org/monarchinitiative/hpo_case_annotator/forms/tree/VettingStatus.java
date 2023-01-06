package org.monarchinitiative.hpo_case_annotator.forms.tree;

/**
 * An enum representing vetting status assigned by a curator.
 */
public enum VettingStatus {

    /**
     * The item is neither {@link #APPROVED} nor {@link #REJECTED}.
     */
    INDETERMINATE,

    /**
     * The curator approved the item.
     */
    APPROVED,

    /**
     * The curater rejected the entity.
     */
    REJECTED

}
