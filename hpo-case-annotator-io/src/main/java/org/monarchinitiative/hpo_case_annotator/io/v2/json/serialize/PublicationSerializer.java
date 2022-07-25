package org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.Publication;

import java.io.IOException;

public class PublicationSerializer extends StdSerializer<Publication> {

    public PublicationSerializer() {
        this(Publication.class);
    }

    public PublicationSerializer(Class<Publication> t) {
        super(t);
    }

    @Override
    public void serialize(Publication publication, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        // authors
        gen.writeStringField("authors", publication.getAuthors());

        // title, journal, year, volume, pages, pmid
        gen.writeStringField("title", publication.getTitle());
        gen.writeStringField("journal", publication.getJournal());
        gen.writeNumberField("year", publication.getYear());
        gen.writeStringField("volume", publication.getVolume());
        gen.writeStringField("pages", publication.getPages());
        gen.writeStringField("pmid", publication.getPmid());

        gen.writeEndObject();
    }
}
