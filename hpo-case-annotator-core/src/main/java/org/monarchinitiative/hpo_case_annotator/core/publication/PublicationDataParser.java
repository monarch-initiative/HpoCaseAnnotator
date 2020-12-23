package org.monarchinitiative.hpo_case_annotator.core.publication;

import org.monarchinitiative.hpo_case_annotator.model.proto.Publication;

public interface PublicationDataParser {

    static PublicationDataParser forFormat(PublicationDataFormat format) {
        switch (format) {
            case EUTILS:
                return new PublicationDataParserPubmedEpubXml();
            case PUBMED_SUMMARY:
                return new PublicationDataParserPubmedFormat();
            default:
                throw new IllegalArgumentException("Unknown format " + format);
        }
    }

    /**
     * Attempt to parse <code>payload</code> into a {@link Publication} data.
     *
     * @param payload string with payload
     * @return publication data
     * @throws PubMedParseException in case it is not possible to parse the payload
     */
    Publication parse(String payload) throws PubMedParseException;
}
