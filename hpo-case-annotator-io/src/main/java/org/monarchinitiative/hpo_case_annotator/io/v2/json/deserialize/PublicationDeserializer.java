package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.Publication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class PublicationDeserializer extends StdDeserializer<Publication> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublicationDeserializer.class);

    public PublicationDeserializer() {
        this(Publication.class);
    }

    protected PublicationDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Publication deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        String authors;
        JsonNode authorsNode = node.get("authors");
        if (authorsNode.isArray()) {
            authors = decodeAuthorsArray(authorsNode);
            LOGGER.warn("Decoded obsolete publication data model. Convert the data into the new model ASAP.");
        } else
            authors = authorsNode.asText();


        String title = Util.readNullableString(node, "title");
        String journal = Util.readNullableString(node, "journal");
        int year = node.get("year").asInt();
        String volume = Util.readNullableString(node, "volume");
        String pages = Util.readNullableString(node, "pages");
        String pmid = Util.readNullableString(node, "pmid");

        return Publication.of(authors, title, journal, year, volume, pages, pmid);
    }

    private static String decodeAuthorsArray(JsonNode authorsNode) {
        List<String> authors = new LinkedList<>();
        Iterator<JsonNode> authorsIterator = authorsNode.elements();
        while (authorsIterator.hasNext()) {
            authors.add(authorsIterator.next().asText());
        }
        return String.join(", ", authors);
    }

}
