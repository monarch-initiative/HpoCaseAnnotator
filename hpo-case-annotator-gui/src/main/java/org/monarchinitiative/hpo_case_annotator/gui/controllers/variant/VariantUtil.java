package org.monarchinitiative.hpo_case_annotator.gui.controllers.variant;

import com.google.common.collect.ImmutableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.monarchinitiative.hpo_case_annotator.gui.util.HostServicesWrapper;
import org.monarchinitiative.hpo_case_annotator.gui.util.PopUps;
import org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class VariantUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(VariantUtil.class);
/*
    protected void showVariantValidator() {
        String assembl=this.elementValues.getGenomeBuild().get(0).toString();
        String chrom =this.elementValues.getChromosome().get(0);

    }*/

    /**
     * This method can be used by any of the variant controllers. Its function is to open a
     * webpage on the VariantValidator website
     *
     * @param assembly The Genome build (hg37 or hg38)
     * @param chrom    The chromosome as a string (chr1 or 1)
     * @param pos      the position of the variant on the chromosome
     * @param ref      The reference base(s)
     * @param alt      The alternative base(s)
     */
    static void goToVariantValidatorWebsite(GenomeAssembly assembly,
                                            String chrom,
                                            int pos,
                                            String ref,
                                            String alt,
                                            HostServicesWrapper hostServices) {
        String genomeAssmblyString = "GRCh37";
        if (assembly.equals(GenomeAssembly.GRCH_38)) {
            genomeAssmblyString = "GRCh38";
        }
        if (chrom.startsWith("chr")) {
            chrom = chrom.substring(3);
        }
        // Create a URI for VariantValidator -- it will look like the following.
        //https://variantvalidator.org/variantvalidation/?variant=GRCh37:1:150550916:G:A
        String vvURL = String.format("https://variantvalidator.org/variantvalidation/?variant=%s:%s:%d:%s:%s",
                genomeAssmblyString,
                chrom,
                pos,
                ref,
                alt);
        // The following opens the system browser to the corresponding VariantValidator page
        hostServices.showDocument(vvURL);
    }


    /**
     * Currently, this app does not have information about the transcripts that are associated with a given
     * gene symbol.
     * TODO -- use an API to get all relevant accession numbers and offer them to the user as a combo box.
     */
    static void getTranscriptDataAndGoToVariantValidatorWebsite(GenomeAssembly assembly,
                                                                HostServicesWrapper hostServices) {
        Optional<List<String>> opt = PopUps.getPairOfUserStringsWithoutWhitespace(null,
                "Transcript data for VariantValidator",
                "enter accession number and variant (e.g., NM_000088.3 and c.589G>T)",
                "accession", "variant", "");
        if (!opt.isPresent()) {
            PopUps.showInfoMessage("Error", "Could not extract HGVS data for VariantValidator");
            return;
        }

        List<String> results = opt.get();
        if (results.size() != 2) {
            PopUps.showInfoMessage("Malformed HGVS String", "Could not parse HVGS String");
            return;
        }
        String transcript = results.get(0);
        String var = results.get(1);
        if (var.startsWith("c.")) {
            var = var.substring(2);
        } else {
            PopUps.showInfoMessage("Malformed HGVS String", "Could not find \"c.\"");
            return;
        }
        // replace '+' with '%2B', which will be a valid URL
        var = var.replaceAll("\\+","%2B");
        String assemblyString = assembly.equals(GenomeAssembly.GRCH_37) ? "GRCh37" : "GRCh38";
        Pattern pat = Pattern.compile("(\\d+)(\\w+)>(\\w+)");
        Matcher m = pat.matcher(var);
        if (m.matches()) {
            String pos = m.group(1);
            String ref = m.group(2);
            String alt = m.group(3);
            String vvURL = String.format("https://variantvalidator.org/variantvalidation/?variant=%s:%s:%s:%s&primary_assembly=%s",
                    transcript,
                    pos,
                    ref,
                    alt,
                    assemblyString);
            hostServices.showDocument(vvURL);
        } else if (var.contains("del") || var.contains("ins") || var.contains("dup")) {
            String vvURL = String.format("https://variantvalidator.org/variantvalidation/?variant=%s:%s&primary_assembly=%s",
                    transcript,
                    var,
                    assemblyString);
            hostServices.showDocument(vvURL);
        } else {
            PopUps.showInfoMessage(String.format("Malformed HGVS String \"%s\"", var), "Could not parse position");
        }
    }

    private static void getTranscriptDataAndGoToVariantValidatorWebsite(GenomeAssembly assembly,
                                                                        String accessionId,
                                                                        HostServicesWrapper hostServices, Window window) {

        Optional<List<String>> opt = PopUps.getPairOfUserStringsWithoutWhitespace(window,
                "Data for VariantValidator",
                "",
                "Tx accesssion ID", "HGVS variant", accessionId);
        if (!opt.isPresent()) {
            return;
        }

        List<String> results = opt.get();
        String approvedAccessionId = results.get(0);
        String hgvsVariant = results.get(1);

        if (hgvsVariant.startsWith("c.")) {
            hgvsVariant = hgvsVariant.substring(2);
        } else {
            PopUps.showInfoMessage("Malformed HGVS String", "Could not find \"c.\"");
            return;
        }
        // The following replaces '+' with %2B, which works in a URL
        hgvsVariant = hgvsVariant.replaceAll("\\+","%2B");
        String assemblyString = assembly.equals(GenomeAssembly.GRCH_37) ? "GRCh37" : "GRCh38";

        String vvURL = String.format("https://variantvalidator.org/variantvalidation/?variant=%s:%s&primary_assembly=%s",
                approvedAccessionId,
                hgvsVariant,
                assemblyString);
        hostServices.showDocument(vvURL);
    }


    /**
     * Call NCBI's eUtil to get a list of RefSeq accession numbers that correspond to a certain EntrezGene id
     * We intend to use this to make it easier for the user to enter HGVS string and send this to VariantValidator
     * for quality control. The GUI will present the user with a list of accession numbers and the user has to choose
     * the right one from a drop down list and then enter only the c.123A>G part of the HGVS string
     *
     * @param entrezGeneId an id such as 2202
     */
    static void geneAccessionNumberGrabber(String entrezGeneId, GenomeAssembly assembly, HostServicesWrapper hostServices, Window window) {
        ImmutableList.Builder<String> accessionIdsBuilder = new ImmutableList.Builder<>();
        final String USER_AGENT = "Mozilla/5.0";
        String url = String.format("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=gene&id=%s&retmode=xml", entrezGeneId);

        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);

            StringBuilder response = new StringBuilder();
            List<String> lines = new ArrayList<>();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    lines.add(inputLine);
                    response.append(inputLine);
                }
            }

            String resp = response.toString().replaceAll("\\s+", "");
            // This is XML but all we need are the NM_12345 accession numbers as follows


            String currentAccession = null;
            Pattern accessionPattern = Pattern.compile("<Gene-commentary_accession>(NM_\\d+)</Gene-commentary_accession>");
            Pattern versionPattern = Pattern.compile("<Gene-commentary_version>(\\d+)</Gene-commentary_version>");
            for (String L : lines) {
                Matcher accessionMatcher = accessionPattern.matcher(L);
                Matcher versionMatcher = versionPattern.matcher(L);

                if (accessionMatcher.find()) {
                    currentAccession = accessionMatcher.group(1);
                } else if (versionMatcher.find()) {
                    String version = versionMatcher.group(1);
                    if (currentAccession != null) {
                        String accessionWithVersion = currentAccession + "." + version;
                        accessionIdsBuilder.add(accessionWithVersion);
                        currentAccession = null;
                    }
                }
            }
        } catch (Exception e) {
            String err = String.format("Unknown entrez id '%s'", entrezGeneId);
            PopUps.showException("Error", "Unable to fetch accession ID", err, e);
            LOGGER.warn(err);
            return;
        }
        List<String> accessionIds = accessionIdsBuilder.build().stream().sorted().distinct().collect(Collectors.toList());
        GridPane gpane = new GridPane();
        gpane.setHgap(10);
        gpane.setVgap(10);
        int heigt = 40 + accessionIds.size() * 40;
        final Scene scene = new Scene(gpane, 200, heigt);
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UTILITY);
        stage.initOwner(window);
        stage.setScene(scene);

        Label label = new Label("Choose accession");
        gpane.add(label, 0, 0);
        int i = 1;
        final StringProperty chosenId = new SimpleStringProperty();
        for (String s : accessionIds) {
            Button b = new Button(s);
            gpane.add(b, 0, i++);
            b.setOnAction((e) -> {
                chosenId.setValue(s);
                stage.close();
                // we need to use accession+version for VariantValidator but do not get one
                // This can provoke an error that is easy to fix. Alternatively, fix it in URL
                // TODO -- can we do better than this?
                e.consume();
            });
        }

        stage.showAndWait();

        getTranscriptDataAndGoToVariantValidatorWebsite(assembly, chosenId.get(), hostServices, window);
    }

}
