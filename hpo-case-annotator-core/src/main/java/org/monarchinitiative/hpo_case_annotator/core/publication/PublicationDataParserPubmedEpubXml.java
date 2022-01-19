package org.monarchinitiative.hpo_case_annotator.core.publication;

import org.monarchinitiative.hpo_case_annotator.model.proto.Publication;

class PublicationDataParserPubmedEpubXml implements PublicationDataParser<Publication> {

    @Override
    public Publication parse(String payload) throws PubMedParseException {
        PublicationData publicationData = EpubFormatUtils.parse(payload);
        Publication.Builder builder = Publication.newBuilder();
        // author list
        builder.setAuthorList(String.join(", ", publicationData.authors()));

        // title
        builder.setTitle(publicationData.title());

        // journal
        builder.setJournal(publicationData.journal());

        // year
        builder.setYear(publicationData.year());

        // volume
        builder.setVolume(publicationData.volume());

        // pages
        builder.setPages(publicationData.pages());

        // pmid
        builder.setPmid(publicationData.pmid());

        return builder.build();
    }
}
