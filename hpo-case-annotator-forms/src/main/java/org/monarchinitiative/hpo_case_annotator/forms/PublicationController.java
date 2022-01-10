package org.monarchinitiative.hpo_case_annotator.forms;

import org.monarchinitiative.hpo_case_annotator.model.v2.Publication;

public class PublicationController implements ComponentController<Publication> {

    private Publication publication;

    @Override
    public void presentComponent(Publication component) {
        // TODO - implement
        this.publication = component;
    }

    @Override
    public Publication getComponent() throws InvalidComponentDataException {
        // TODO - implement
        return publication;
    }
}
