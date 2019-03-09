package org.monarchinitiative.hpo_case_annotator.gui.controllers.variant;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.monarchinitiative.hpo_case_annotator.gui.util.HostServicesWrapper;
import org.monarchinitiative.hpo_case_annotator.gui.util.PopUps;
import org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariantUtil {
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
                "accession", "variant");
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
        String assemblyString = assembly.equals(GenomeAssembly.GRCH_37) ? "GRCh37" : "GRCh38";
        Pattern pat = Pattern.compile("(\\d+)(\\w+)>(\\w+)");
        Matcher m = pat.matcher(var);
        if (m.matches() ) {
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
            PopUps.showInfoMessage(String.format("Malformed HGVS String \"%s\"",var), "Could not parse position");
        }
    }

    static void getTranscriptDataAndGoToVariantValidatorWebsite(GenomeAssembly assembly,
                                                                String transcript,
                                                                HostServicesWrapper hostServices) {
        Optional<List<String>> opt = PopUps.getPairOfUserStringsWithoutWhitespace(null,
                "Transcript data for VariantValidator",
                "enter accession number and variant (e.g., NM_000088.3 and c.589G>T)",
                "accession", "variant");


       String var=PopUps.getStringFromUser("Variant data for VariantValidator",
               "HGVS:",String.format("Enter variant (%s)",
                       transcript));

        if (var==null) {
            PopUps.showInfoMessage("Error", "Could not extract HGVS data for VariantValidator");
            return;
        }

        if (var.startsWith("c.")) {
            var = var.substring(2);
        } else {
            PopUps.showInfoMessage("Malformed HGVS String", "Could not find \"c.\"");
            return;
        }
        String assemblyString = assembly.equals(GenomeAssembly.GRCH_37) ? "GRCh37" : "GRCh38";

        String vvURL = String.format("https://variantvalidator.org/variantvalidation/?variant=%s:%s&primary_assembly=%s",
                    transcript,
                    var,
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
     * @return a list of corresponding accession numbers.
     */
    public static void geneAccessionNumberGrabber(String entrezGeneId,GenomeAssembly assembly,HostServicesWrapper hostServices) {
        ImmutableSet.Builder<String> builder = new ImmutableSet.Builder<>();
        final String USER_AGENT = "Mozilla/5.0";
        String url = String.format("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=gene&id=%s&retmode=xml", entrezGeneId);
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            String resp = response.toString().replaceAll("\\s+", "");
            // This is XML but all we need are the NM_12345 accession numbers as follows
            Pattern pat = Pattern.compile("<Gene-commentary_accession>(NM_\\d+)</Gene-commentary_accession>");
            Matcher mat = pat.matcher(resp);
            while (mat.find()) {
                String refseqAccession = mat.group(1);
                builder.add(refseqAccession);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Set<String> ids = builder.build();
        GridPane gpane = new GridPane();
        gpane.setHgap(10);
        gpane.setVgap(10);
        int heigt=40+ids.size()*40;
        final Scene scene = new Scene(gpane,200,heigt);
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UTILITY);
        stage.setScene(scene);

        Label label = new Label("Choose accession");
        gpane.add(label,0,0);
        int i=1;
        final StringProperty chosenId=new SimpleStringProperty();
        for (String s:ids) {
            Button b = new Button(s);
            gpane.add(b,0,i++);
            b.setOnAction((e)->{chosenId.setValue(s);
            stage.close();
            getTranscriptDataAndGoToVariantValidatorWebsite(assembly,s, hostServices);
            e.consume();});
        }

        stage.showAndWait();
    }

}
