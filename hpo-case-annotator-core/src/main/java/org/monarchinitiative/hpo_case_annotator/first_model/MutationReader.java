package org.monarchinitiative.hpo_case_annotator.first_model;


import org.monarchinitiative.hpo_case_annotator.first_model.mutation.Mutation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;


/**
 * The purpose of this class is simply to read in all of the individual Mutation XML files and to return either a List<Mutation>
 * or a Map<String,Mutation> that will be used by other parts of the program to perform quality control.
 *
 * @author Peter Robinson
 * @version 0.1.0.8 (31 December 2015)
 */
public class MutationReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(MutationReader.class);

    /** Path to directory where all the Mutation files are stored */
    private final File mutationXmlDirectory;


    public MutationReader(File mutationDir) {
        mutationXmlDirectory = mutationDir;
    }


    /**
     * Create one Mutation object from the corresponding XML file. Note that the name of the class is a slight misnomer,
     * since the class contains a case, some of which are compound heterozygous and thus have two distinct disease
     * causing variants.
     *
     * @param inputStream containing {@link Mutation} data in XML format
     * @return initialized {@link Mutation} or <code>null</code>
     */
    public Mutation getSingleMutation(InputStream inputStream) throws IOException, SAXException {
//        File fullPath = new File(mutationXmlDirectory, path);
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
//        SimpleErrorHandler seh = new SimpleErrorHandler(fullPath.getAbsolutePath());
        SimpleErrorHandler seh = new SimpleErrorHandler(inputStream);
        xmlReader.setErrorHandler(seh);
//        FileReader reader = new FileReader(fullPath);
        // according to the older code, DTD path should be set here. However, I am not familiar with
        // SAX XML parsers and this is a little hack just to make things work. We require hgrm.dtd to be present
        // in XML directory for mendelian mutations and cancer-mutations.dtd to be present for somatic mutations.
        InputSource inputSource = new InputSource(inputStream);
//        inputSource.setSystemId(fullPath.getAbsolutePath());
        MutationContentHandler handler = new MutationContentHandler();
        xmlReader.setContentHandler(handler);
        xmlReader.setFeature("http://xml.org/sax/features/validation", true);
        xmlReader.parse(inputSource);
        if (!seh.parseSuccesful()) {
//            LOGGER.warn("Unsuccesful parsing of file {}. Error: {}", fullPath.getAbsolutePath(), seh.getError());
        } else {
//            LOGGER.debug("Parsing of file {} was successful", fullPath);
        }
        return handler.getMutation();
    }


    /**
     * @return a list of all Mutations (representing all XML files)
     */
//    public Collection<Mutation> getAllMutations() throws IOException, SAXException {
//        List<Mutation> mutations = new ArrayList<>();
//        for (String path : getAllXMLPaths()) {
//            Mutation mutation = getSingleMutation(path);
//            mutations.add(mutation);
//        }
//        return mutations;
//    }


    /**
     * @return a list of all mutation XML files from the  directory indicated.
     */
    public Collection<String> getAllXMLPaths() {
        if (mutationXmlDirectory != null && mutationXmlDirectory.isDirectory()) {
            File[] files = mutationXmlDirectory.listFiles(f -> f.getName().endsWith(".xml"));
            return Arrays.stream(files)
                    .map(File::getName)
                    .collect(Collectors.toList());
        } else {
            LOGGER.warn("Path to directory with XML files has not been initialized (either null or not pointing to directory)");
        }
        return Collections.emptyList();
    }

}
/* eof */
