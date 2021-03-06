package org.monarchinitiative.hpo_case_annotator.model.codecs;

import org.monarchinitiative.hpo_case_annotator.model.proto.*;
import org.monarchinitiative.hpo_case_annotator.model.xml_model.Variant;
import org.monarchinitiative.hpo_case_annotator.model.xml_model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This codec converts {@link DiseaseCase} into {@link DiseaseCaseModel}. {@link DiseaseCaseModel} is a deprecated format which was
 * used to store curated data before <em>advent</em> of {@link DiseaseCase}.
 * <p>
 * Both conversions are loseless.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public final class DiseaseCaseToDiseaseCaseModelCodec implements Codec<DiseaseCase, DiseaseCaseModel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiseaseCaseToDiseaseCaseModelCodec.class);

    DiseaseCaseToDiseaseCaseModelCodec() {
        // package-private no-op
    }

    private static Function<org.monarchinitiative.hpo_case_annotator.model.proto.Variant, Variant> variantToDCMVariant() {
        return variant -> {
            VariantValidation val = variant.getVariantValidation();

            switch (variant.getVariantValidation().getContext()) {
                case MENDELIAN:
                    MendelianVariant mv = new MendelianVariant();
                    mv.setRegulator(val.getRegulator());
                    mv.setReporterRegulation(val.getReporterRegulation());
                    mv.setReporterResidualActivity(val.getReporterResidualActivity());
                    mv.setEmsaValidationPerformed(val.getEmsaValidationPerformed() ? "yes" : "no");
                    mv.setEmsaTFSymbol(val.getEmsaTfSymbol());
                    mv.setEmsaGeneId(val.getEmsaGeneId());
                    mv.setOther(val.getOtherChoices());
                    mv.setOtherEffect(val.getOtherEffect());

                    return initializeCommonFields().apply(variant, mv);

                case SPLICING:
                    SplicingVariant splv = new SplicingVariant();
                    splv.setConsequence(variant.getConsequence());
                    splv.setCrypticPosition(String.valueOf(variant.getCrypticPosition()));
                    splv.setCrypticSpliceSiteType(variant.getCrypticSpliceSiteType().toString());
                    splv.setCrypticSpliceSiteSnippet(variant.getCrypticSpliceSiteSnippet());

                    SplicingValidation splicingValidation = new SplicingValidation();
                    splicingValidation.setMinigeneValidation(val.getMinigeneValidation());
                    splicingValidation.setSiteDirectedMutagenesisValidation(val.getSiteDirectedMutagenesisValidation());
                    splicingValidation.setRtPCRValidation(val.getRtPcrValidation());
                    splicingValidation.setSrProteinOverexpressionValidation(val.getSrProteinOverexpressionValidation());
                    splicingValidation.setSrProteinKnockdownValidation(val.getSrProteinKnockdownValidation());
                    splicingValidation.setCDNASequencingValidation(val.getCDnaSequencingValidation());
                    splicingValidation.setPcrValidation(val.getPcrValidation());
                    splicingValidation.setMutOfWTSpliceSiteValidation(val.getMutOfWtSpliceSiteValidation());
                    splicingValidation.setOtherValidation(val.getOtherValidation());
                    splv.setSplicingValidation(splicingValidation);

                    return initializeCommonFields().apply(variant, splv);

                case SOMATIC:
                    SomaticVariant somv = new SomaticVariant();
                    somv.setRegulator(val.getRegulator());
                    somv.setReporterRegulation(val.getReporterRegulation());
                    somv.setReporterResidualActivity(val.getReporterResidualActivity());
                    somv.setEmsaValidationPerformed(val.getEmsaValidationPerformed() ? "yes" : "no");
                    somv.setEmsaTFSymbol(val.getEmsaTfSymbol());
                    somv.setEmsaGeneId(val.getEmsaGeneId());
                    somv.setMPatients(String.valueOf(val.getMPatients()));
                    somv.setNPatients(String.valueOf(val.getNPatients()));
                    somv.setOther(val.getOtherChoices());
                    somv.setOtherEffect(val.getOtherEffect());

                    return initializeCommonFields().apply(variant, somv);

                default:
                    LOGGER.warn("Unknown context {}, returning an empty variant", variant.getVariantValidation().getContext());
                    return new org.monarchinitiative.hpo_case_annotator.model.xml_model.Variant();
            }
        };
    }

    /**
     * @return {@link BiFunction} accepting two variants ({@link org.monarchinitiative.hpo_case_annotator.model.proto.Variant},
     * {@link org.monarchinitiative.hpo_case_annotator.model.xml_model.Variant}). The data common to all subclasses of the {@link org.monarchinitiative.hpo_case_annotator.model.xml_model.Variant} class is copied from the {@link
     * org.monarchinitiative.hpo_case_annotator.model.proto.Variant} here. The BiFunction returns the 2nd argument (the
     * same instance) with initialized values.
     */
    private static BiFunction<org.monarchinitiative.hpo_case_annotator.model.proto.Variant, org.monarchinitiative.hpo_case_annotator.model.xml_model.Variant, org.monarchinitiative.hpo_case_annotator.model.xml_model.Variant> initializeCommonFields() {
        return (proto, older_format) -> {
            older_format.setChromosome(proto.getVariantPosition().getContig());
            older_format.setPosition(String.valueOf(proto.getVariantPosition().getPos()));
            older_format.setReferenceAllele(proto.getVariantPosition().getRefAllele());
            older_format.setAlternateAllele(proto.getVariantPosition().getAltAllele());
            older_format.setSnippet(proto.getSnippet());
            older_format.setGenotype(proto.getGenotype().toString());
            older_format.setVariantClass(proto.getVariantClass());
            older_format.setPathomechanism(proto.getPathomechanism());
            older_format.setCosegregation(proto.getVariantValidation().getCosegregation() ? "yes" : "no");
            older_format.setComparability(proto.getVariantValidation().getComparability() ? "yes" : "no");
            return older_format;
        };
    }

    private static Function<OntologyClass, HPO> ontologyClassToHpo() {
        return oc -> {
            HPO hpo = new HPO();
            hpo.setHpoId(oc.getId());
            hpo.setHpoName(oc.getLabel());
            hpo.setObserved(oc.getNotObserved() ? "NOT" : "YES");
            return hpo;
        };
    }

    private static Function<HPO, OntologyClass> hpoToOntologyClass() {
        return hpo -> OntologyClass.newBuilder()
                .setId(hpo.getHpoId())
                .setLabel(hpo.getHpoName())
                .setNotObserved(!hpo.getObserved().equals("YES"))
                .build();
    }

    private static Function<org.monarchinitiative.hpo_case_annotator.model.xml_model.Variant, org.monarchinitiative.hpo_case_annotator.model.proto.Variant> dcmVariantToVariant(String genomeAssembly) {
        return v -> {
            org.monarchinitiative.hpo_case_annotator.model.proto.Variant.Builder variantBuilder = org.monarchinitiative.hpo_case_annotator.model.proto.Variant.newBuilder();
            // variant
            try {
                variantBuilder = variantBuilder.setVariantPosition(
                        VariantPosition.newBuilder()
                                .setGenomeAssembly(convertGenomeAssemblyString(genomeAssembly))
                                .setContig(v.getChromosome())
                                .setPos(v.getPosition() == null ? 0 : Integer.parseInt(v.getPosition()))
                                .setRefAllele(v.getReferenceAllele())
                                .setAltAllele(v.getAlternateAllele())
                                .build())
                        .setSnippet(v.getSnippet())
                        .setGenotype(Genotype.valueOf(v.getGenotype()))
                        .setVariantClass(v.getVariantClass())
                        .setPathomechanism(v.getPathomechanism());
            } catch (NumberFormatException nfe) {
                LOGGER.warn("Unable to parse position '{}' of the variant", v.getPosition(), nfe);
            } catch (IllegalArgumentException iea) {
                LOGGER.warn("Unable to parse genotype '{}' of the variant", v.getGenotype(), iea);
            }

            VariantValidation.Builder validationBuilder = VariantValidation.newBuilder();

            /// SPLICING VARIANT DETAILS
            if (v instanceof MendelianVariant) {
                MendelianVariant mv = (MendelianVariant) v;

                validationBuilder = validationBuilder.setContext(VariantValidation.Context.MENDELIAN)
                        .setRegulator(mv.getRegulator() == null ? "" : mv.getRegulator())
                        .setReporterRegulation(mv.getReporterRegulation() == null ? "" : mv.getReporterRegulation())
                        .setReporterResidualActivity(mv.getReporterResidualActivity() == null ? "" : mv.getReporterResidualActivity())
                        .setEmsaValidationPerformed(mv.getEmsaValidationPerformed().equals("yes"))
                        .setEmsaTfSymbol(mv.getEmsaTFSymbol() == null ? "" : mv.getEmsaTFSymbol())
                        .setEmsaGeneId(mv.getEmsaGeneId() == null ? "" : mv.getEmsaGeneId())
                        .setOtherChoices(mv.getOther() == null ? "" : mv.getOther())
                        .setOtherEffect(mv.getOtherEffect() == null ? "" : mv.getOtherEffect());

                /// SPLICING VARIANT DETAILS
            } else if (v instanceof SomaticVariant) {
                SomaticVariant sv = (SomaticVariant) v;

                validationBuilder = validationBuilder.setContext(VariantValidation.Context.SOMATIC)
                        .setRegulator(sv.getRegulator() == null ? "" : sv.getRegulator())
                        .setReporterRegulation(sv.getReporterRegulation() == null ? "" : sv.getReporterRegulation())
                        .setReporterResidualActivity(sv.getReporterResidualActivity() == null ? "" : sv.getReporterResidualActivity())
                        .setEmsaValidationPerformed(sv.getEmsaValidationPerformed().equals("yes"))
                        .setEmsaTfSymbol(sv.getEmsaTFSymbol() == null ? "" : sv.getEmsaTFSymbol())
                        .setEmsaGeneId(sv.getEmsaGeneId() == null ? "" : sv.getEmsaGeneId())
                        .setOtherChoices(sv.getOther() == null ? "" : sv.getOther())
                        .setOtherEffect(sv.getOtherEffect() == null ? "" : sv.getOtherEffect());
                try {
                    validationBuilder.setNPatients(sv.getNPatients() == null ? -1 : Integer.parseInt(sv.getNPatients()));
                } catch (NumberFormatException e) {
                    LOGGER.warn("Unable to parse N patients from value '{}'. Setting to -1", sv.getNPatients(), e);
                    validationBuilder.setNPatients(-1);
                }
                try {
                    validationBuilder.setMPatients(sv.getMPatients() == null ? -1 : Integer.parseInt(sv.getMPatients()));
                } catch (NumberFormatException e) {
                    LOGGER.warn("Unable to parse M patients from value '{}'. Setting to -1", sv.getMPatients(), e);
                    validationBuilder.setMPatients(-1);
                }

                /// SPLICING VARIANT DETAILS
            } else if (v instanceof SplicingVariant) {
                SplicingVariant sv = (SplicingVariant) v;

                String ss = sv.getCrypticSpliceSiteType();
                try {
                    variantBuilder = variantBuilder.setConsequence(sv.getConsequence())
                            .setCrypticPosition(sv.getCrypticPosition() == null || sv.getCrypticPosition().isEmpty() ? 0 : Integer.parseInt(sv.getCrypticPosition()))
                            .setCrypticSpliceSiteType(ss == null || ss.isEmpty()
                                    ? CrypticSpliceSiteType.NO_CSS
                                    : CrypticSpliceSiteType.valueOf(ss))
                            .setCrypticSpliceSiteSnippet(sv.getCrypticSpliceSiteSnippet());
                } catch (NumberFormatException nfe) {
                    LOGGER.warn("Unable to parse CSS position from '{}'", sv.getCrypticPosition(), nfe);
                } catch (IllegalArgumentException iae) {
                    LOGGER.warn("Unable to parse CSS type from '{}'", sv.getCrypticSpliceSiteType(), iae);
                }

                SplicingValidation sval = sv.getSplicingValidation();
                validationBuilder = validationBuilder.setContext(VariantValidation.Context.SPLICING)
                        .setMinigeneValidation(sval.isMinigeneValidation())
                        .setSiteDirectedMutagenesisValidation(sval.isSiteDirectedMutagenesisValidation())
                        .setRtPcrValidation(sval.isRtPCRValidation())
                        .setSrProteinOverexpressionValidation(sval.isSrProteinOverexpressionValidation())
                        .setSrProteinKnockdownValidation(sval.isSrProteinKnockdownValidation())
                        .setCDnaSequencingValidation(sval.isCDNASequencingValidation())
                        .setPcrValidation(sval.isPcrValidation())
                        .setMutOfWtSpliceSiteValidation(sval.isMutOfWTSpliceSiteValidation())
                        .setOtherValidation(sval.isOtherValidation());
            }

            validationBuilder
                    .setCosegregation(v.getCosegregation() != null && !v.getCosegregation().isEmpty() && !v.getCosegregation().equals("no"))
                    .setComparability(v.getComparability() != null && !v.getComparability().isEmpty() && !v.getComparability().equals("no"));
            variantBuilder.setVariantValidation(validationBuilder.build());

            return variantBuilder.build();
        };
    }

    public static GenomeAssembly convertGenomeAssemblyString(String assembly) {
        switch (assembly.toLowerCase()) {
            case "grch37":
            case "hg19":
                return GenomeAssembly.GRCH_37;
            case "grch38":
            case "hg38":
                return GenomeAssembly.GRCH_38;
            default:
                return GenomeAssembly.UNKNOWN_GENOME_ASSEMBLY;
        }
    }

    @Override
    public DiseaseCaseModel encode(DiseaseCase dc) {
        final DiseaseCaseModel model = new DiseaseCaseModel();


        // genome build
        model.setGenomeBuild(dc.getVariantCount() > 0
                ? dc.getVariant(0).getVariantPosition().getGenomeAssembly().toString()
                : GenomeAssembly.UNKNOWN_GENOME_ASSEMBLY.toString());

        // Publication
        final org.monarchinitiative.hpo_case_annotator.model.xml_model.Publication p = new org.monarchinitiative.hpo_case_annotator.model.xml_model.Publication();
        p.setAuthorlist(dc.getPublication().getAuthorList());
        p.setTitle(dc.getPublication().getTitle());
        p.setJournal(dc.getPublication().getJournal());
        p.setYear(dc.getPublication().getYear());
        p.setVolume(dc.getPublication().getVolume());
        p.setPages(dc.getPublication().getPages());
        p.setPmid(dc.getPublication().getPmid());
        model.setPublication(p);

        // Metadata
        final Metadata m = new Metadata();
        m.setMetadataText(dc.getMetadata());
        model.setMetadata(m);

        // TargetGene
        final TargetGene gene = new TargetGene();
        gene.setGeneName(dc.getGene().getSymbol());
        gene.setEntrezID(String.valueOf(dc.getGene().getEntrezId()));
        model.setTargetGene(gene);

        // Variants
        final List<Variant> variants = dc.getVariantList().stream().map(variantToDCMVariant()).collect(Collectors.toList());
        model.setVariants(variants);

        // FamilyInfo
        final org.monarchinitiative.hpo_case_annotator.model.xml_model.FamilyInfo familyInfo = new org.monarchinitiative.hpo_case_annotator.model.xml_model.FamilyInfo();
        familyInfo.setFamilyOrPatientID(dc.getFamilyInfo().getFamilyOrProbandId());
        familyInfo.setAge(String.valueOf(dc.getFamilyInfo().getAge()));
        familyInfo.setSex(dc.getFamilyInfo().getSex().toString());
        model.setFamilyInfo(familyInfo);

        // HPO info
        final List<HPO> hpos = dc.getPhenotypeList().stream().map(ontologyClassToHpo()).collect(Collectors.toList());
        model.setHpoList(hpos);

        // Disease
        final org.monarchinitiative.hpo_case_annotator.model.xml_model.Disease disease = new org.monarchinitiative.hpo_case_annotator.model.xml_model.Disease();
        disease.setDatabase(dc.getDisease().getDatabase());
        disease.setDiseaseId(dc.getDisease().getDiseaseId());
        disease.setDiseaseName(dc.getDisease().getDiseaseName());
        model.setDisease(disease);

        // Biocurator
        final org.monarchinitiative.hpo_case_annotator.model.xml_model.Biocurator biocurator = new org.monarchinitiative.hpo_case_annotator.model.xml_model.Biocurator();
        biocurator.setBioCuratorId(dc.getBiocurator().getBiocuratorId());
        model.setBiocurator(biocurator);

        return model;
    }

    @Override
    public DiseaseCase decode(DiseaseCaseModel dcm) {
        DiseaseCase.Builder builder = DiseaseCase.newBuilder();

        // Publication
        builder = builder.setPublication(org.monarchinitiative.hpo_case_annotator.model.proto.Publication.newBuilder()
                .setAuthorList(dcm.getPublication().getAuthorlist())
                .setTitle(dcm.getPublication().getTitle())
                .setJournal(dcm.getPublication().getJournal())
                .setYear(dcm.getPublication().getYear())
                .setVolume(dcm.getPublication().getVolume())
                .setPages(dcm.getPublication().getPages())
                .setPmid(dcm.getPublication().getPmid())
                .build())
                // Metadata
                .setMetadata(dcm.getMetadata().getMetadataText());

        // TargetGene
        try {
            builder.setGene(Gene.newBuilder()
                    .setEntrezId(dcm.getTargetGene().getEntrezID() == null ? -1 : Integer.parseInt(dcm.getTargetGene().getEntrezID()))
                    .setSymbol(dcm.getTargetGene().getGeneName())
                    .build());
        } catch (NumberFormatException nfe) {
            LOGGER.warn("Unable to parse gene Entrez ID integer from '{}'. Not an integer?", dcm.getTargetGene().getEntrezID());
        }
        // Variants
        builder
                .addAllVariant(dcm.getVariants().stream()
                        .map(dcmVariantToVariant(dcm.getGenomeBuild()))
                        .collect(Collectors.toList()));
        try {
            // FamilyInfo
            builder.setFamilyInfo(org.monarchinitiative.hpo_case_annotator.model.proto.FamilyInfo.newBuilder()
                    .setFamilyOrProbandId(dcm.getFamilyInfo().getFamilyOrPatientID())
                    .setAge(dcm.getFamilyInfo().getAge() == null ? "" : dcm.getFamilyInfo().getAge())
                    .setSex(dcm.getFamilyInfo().getSex().isEmpty() || dcm.getFamilyInfo().getSex() == null ? Sex.UNKNOWN_SEX : Sex.valueOf(dcm.getFamilyInfo().getSex()))
                    .build());
        } catch (NumberFormatException nfe) { // problem parsing Age
            LOGGER.warn("Unable to parse proband's age from '{}'. Not an integer?", dcm.getFamilyInfo().getAge(), nfe);
        } catch (IllegalArgumentException iae) { // problem parsing Sex
            LOGGER.warn("Unable to parse proband's sex from string '{}'. Allowed: {}", dcm.getFamilyInfo().getSex(),
                    Arrays.stream(Sex.values()).map(Sex::toString).collect(Collectors.joining(",", "{", "}")), iae);
        }
        // HPO info
        builder
                .addAllPhenotype(dcm.getHpoList().stream().map(hpoToOntologyClass()).collect(Collectors.toList()))
                // Disease
                .setDisease(org.monarchinitiative.hpo_case_annotator.model.proto.Disease.newBuilder()
                        .setDatabase(dcm.getDisease().getDatabase())
                        .setDiseaseId(dcm.getDisease().getDiseaseId())
                        .setDiseaseName(dcm.getDisease().getDiseaseName())
                        .build())
                // Biocurator
                .setBiocurator(org.monarchinitiative.hpo_case_annotator.model.proto.Biocurator.newBuilder()
                        .setBiocuratorId(dcm.getBiocurator().getBioCuratorId() == null ? "" : dcm.getBiocurator().getBioCuratorId())
                        .build());

        return builder.build();
    }

}
