package org.monarchinitiative.hpo_case_annotator.core.publication;

import org.monarchinitiative.hpo_case_annotator.model.proto.Publication;

public interface PublicationDataParser<T> {

    static PublicationDataParser<Publication> forV1PublicationFormat(PublicationDataFormat format) {
        return switch (format) {
            case EUTILS -> new PublicationDataParserPubmedEpubXml();
            case PUBMED_SUMMARY -> new PublicationDataParserPubmedFormat();
            default -> throw new IllegalArgumentException("Unknown format " + format);
        };
    }

    static PublicationDataParser<org.monarchinitiative.hpo_case_annotator.model.v2.Publication> forV2PublicationFormat() {
        return new V2PublicationDataParser();
    }

    /**
     * Attempt to parse <code>payload</code> into a {@link Publication} data.
     *
     * @param payload string with payload
     * @return publication data
     * @throws PubMedParseException in case it is not possible to parse the payload
     */
    T parse(String payload) throws PubMedParseException;
}
