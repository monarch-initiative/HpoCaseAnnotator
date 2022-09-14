package org.monarchinitiative.hpo_case_annotator.core.publication;

import org.monarchinitiative.hpo_case_annotator.model.v2.Publication;

class V2PublicationDataParser implements PublicationDataParser<Publication> {

    @Override
    public Publication parse(String payload) throws PubMedParseException {
        PublicationData publicationData = EpubFormatUtils.parse(payload);
        return Publication.of(concatenateAuthors(publicationData),
                publicationData.title(),
                publicationData.journal(),
                parseYear(publicationData.year()),
                publicationData.volume(),
                publicationData.pages(),
                publicationData.pmid());
    }

    private String concatenateAuthors(PublicationData publicationData) {
        return String.join(", ", publicationData.authors());
    }

    private static int parseYear(String year) throws PubMedParseException {
        try {
            return Integer.parseInt(year);
        } catch (NumberFormatException e) {
            throw new PubMedParseException("Unparsable year: " + year);
        }
    }
}
