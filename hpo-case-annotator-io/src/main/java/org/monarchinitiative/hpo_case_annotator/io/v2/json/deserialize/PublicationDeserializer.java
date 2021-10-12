package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.Publication;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class PublicationDeserializer extends StdDeserializer<Publication> {

    public PublicationDeserializer() {
        this(Publication.class);
    }

    protected PublicationDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Publication deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);

        List<String> authors = new LinkedList<>();
        Iterator<JsonNode> authorsIterator = node.get("authors").elements();
        while (authorsIterator.hasNext()) {
            authors.add(authorsIterator.next().asText());
        }

        String title = node.get("title").asText();
        String journal = node.get("journal").asText();
        int year = node.get("year").asInt();
        String volume = node.get("volume").asText();
        String pages = node.get("pages").asText();
        String pmid = node.get("pmid").asText();

        return Publication.of(authors, title, journal, year, volume, pages, pmid);
    }

}
