package org.monarchinitiative.hpo_case_annotator.convert;

import org.monarchinitiative.hpo_case_annotator.model.Hg18GenomicAssembly;
import org.monarchinitiative.hpo_case_annotator.model.convert.Codec;
import org.monarchinitiative.hpo_case_annotator.model.convert.ModelTransformationException;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;
import org.monarchinitiative.hpo_case_annotator.model.proto.*;
import org.monarchinitiative.hpo_case_annotator.model.ModelUtils;
import org.monarchinitiative.hpo_case_annotator.model.v2.Publication;
import org.monarchinitiative.hpo_case_annotator.model.v2.Sex;
import org.monarchinitiative.hpo_case_annotator.model.v2.*;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.*;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.svart.*;
import org.monarchinitiative.svart.assembly.GenomicAssemblies;
import org.monarchinitiative.svart.assembly.GenomicAssembly;

import java.text.NumberFormat;
import java.time.Instant;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.*;

class V1toV2Codec implements Codec<DiseaseCase, Study> {

    private static final NumberFormat NF = NumberFormat.getNumberInstance(Locale.US);
    private static final String DEFAULT_PARENTAL_ID = "";
    private static final V1toV2Codec INSTANCE = new V1toV2Codec();
    private static final Map<GenomeAssembly, GenomicAssembly> ASSEMBLIES = Map.of(
            GenomeAssembly.NCBI_36, Hg18GenomicAssembly.hg18GenomicAssembly(),
            GenomeAssembly.GRCH_37, GenomicAssemblies.GRCh37p13(),
            GenomeAssembly.GRCH_38, GenomicAssemblies.GRCh38p13()
    );

    private V1toV2Codec() {
    }

    static V1toV2Codec getInstance() {
        return INSTANCE;
    }

    private static Publication transformPublication(org.monarchinitiative.hpo_case_annotator.model.proto.Publication publication) throws ModelTransformationException {
        int year;
        try {
            year = Integer.parseInt(publication.getYear());
        } catch (NumberFormatException e) {
            throw new ModelTransformationException("Invalid publication year: " + publication.getYear());
        }

        return Publication.of(publication.getAuthorList(), publication.getTitle(), publication.getJournal(), year, publication.getVolume(), publication.getPages(), publication.getPmid());
    }

    private static StudyMetadata transformMetadata(String metadata, Biocurator biocurator, String softwareVersion) {
        return StudyMetadata.of(metadata, EditHistory.of(biocurator.getBiocuratorId(), softwareVersion, Instant.now()), List.of());
    }

    static CuratedVariant transformVariant(Variant variant) throws ModelTransformationException {
        return switch (variant.getVariantValidation().getContext()) {
            case MENDELIAN, SOMATIC, SPLICING -> transformSequenceVariant(variant);
            case INTRACHROMOSOMAL, TRANSLOCATION -> transformSymbolicVariant(variant);
            case UNRECOGNIZED, NO_CONTEXT -> throw new ModelTransformationException("Cannot transform variant with `" + variant.getVariantValidation().getContext() + "` context");
        };
    }

    private static CuratedVariant transformSequenceVariant(Variant variant) throws ModelTransformationException {
        VariantPosition variantPosition = variant.getVariantPosition();
        Contig contig = parseContig(variantPosition.getGenomeAssembly(), variantPosition.getContig());

        ConfidenceInterval startCi = ConfidenceInterval.of(variantPosition.getCiBeginOne(), variantPosition.getCiBeginTwo());
        Coordinates coordinates = Coordinates.of(CoordinateSystem.oneBased(), variantPosition.getPos(), startCi, variantPosition.getPos(), startCi);


        // The assembly will not be null, as it is null-checked in `parseContig` method above
        GenomicAssembly assembly = ASSEMBLIES.get(variantPosition.getGenomeAssembly());
        String variantId = createIntrachromosomalVariantId(variantPosition.getGenomeAssembly(),
                contig.name(),
                coordinates.start(),
                coordinates.end(),
                variantPosition.getRefAllele(),
                variantPosition.getAltAllele());
        GenomicVariant v = GenomicVariant.of(contig,
                variantId,
                Strand.POSITIVE,
                coordinates,
                variantPosition.getRefAllele(),
                variantPosition.getAltAllele());

        VariantMetadata variantMetadata = parseVariantMetadata(variant);
        return CuratedVariant.of(assembly.name(), v, variantMetadata);
    }

    private static CuratedVariant transformSymbolicVariant(Variant variant) throws ModelTransformationException {
        // can be symbolic variant or breakend/translocation
        return switch (variant.getVariantValidation().getContext()) {
            case INTRACHROMOSOMAL -> parseSymbolicVariant(variant);
            case TRANSLOCATION -> parseBreakendVariant(variant);
            default -> throw new ModelTransformationException("Unexpected validation context for symbolic/breakend variant: " + variant.getVariantValidation().getContext());
        };

    }

    private static CuratedVariant parseSymbolicVariant(Variant variant) throws ModelTransformationException {
        VariantPosition variantPosition = variant.getVariantPosition();
        Contig contig = parseContig(variantPosition.getGenomeAssembly(), variantPosition.getContig());
        Coordinates coordinates = getSymbolicVariantCoordinates(variantPosition);

        int changeLength = switch (variant.getSvType()) {
            case DEL -> -(variantPosition.getPos2() - variantPosition.getPos() + 1); // change length is negative for DELs
            case INS -> {
                if (variant.getNInserted() > 0)
                    yield variant.getNInserted();
                else
                    throw new ModelTransformationException("N of inserted bases should be positive: " + variant.getNInserted());
            }
            case DUP -> variantPosition.getPos2() - variantPosition.getPos() + 1;
            case INV -> 0; // inversion alleles have the same length
            case CNV, STR, TRA, BND, UNKNOWN, NON_STRUCTURAL, UNRECOGNIZED -> throw new ModelTransformationException("Unexpected SV type for symbolic variant: " + variant.getSvType());
        };

        String altAllele = String.format("<%s>", variantPosition.getAltAllele());
        String variantId = createIntrachromosomalVariantId(variantPosition.getGenomeAssembly(),
                contig.name(),
                coordinates.start(),
                coordinates.end(),
                variantPosition.getRefAllele(),
                altAllele);

        GenomicVariant v = GenomicVariant.of(contig,
                variantId,
                Strand.POSITIVE,
                coordinates,
                variantPosition.getRefAllele(),
                altAllele,
                changeLength);

        // The assembly will not be null, as it is null-checked in `parseContig` method above
        GenomicAssembly assembly = ASSEMBLIES.get(variantPosition.getGenomeAssembly());
        VariantMetadata variantMetadata = parseVariantMetadata(variant);
        return CuratedVariant.of(assembly.name(), v, variantMetadata);
    }

    private static Coordinates getSymbolicVariantCoordinates(VariantPosition variantPosition) {
        ConfidenceInterval startCi = ConfidenceInterval.of(variantPosition.getCiBeginOne(), variantPosition.getCiBeginTwo());
        ConfidenceInterval endCi = ConfidenceInterval.of(variantPosition.getCiEndOne(), variantPosition.getCiEndTwo());
        return Coordinates.of(CoordinateSystem.oneBased(), variantPosition.getPos(), startCi, variantPosition.getPos2(), endCi);
    }

    private static CuratedVariant parseBreakendVariant(Variant variant) throws ModelTransformationException {
        VariantPosition variantPosition = variant.getVariantPosition();

        // left breakend
        Contig leftContig = parseContig(variantPosition.getGenomeAssembly(), variantPosition.getContig());
        ConfidenceInterval leftCi = ConfidenceInterval.of(variantPosition.getCiBeginOne(), variantPosition.getCiEndOne());
        Coordinates leftCoordinates = Coordinates.of(CoordinateSystem.zeroBased(),
                variantPosition.getPos(), leftCi,
                variantPosition.getPos(), leftCi);
        Strand leftStrand = directionToStrand(variantPosition.getContigDirection());
        GenomicBreakend left = GenomicBreakend.of(leftContig,
                createBreakendId(variantPosition.getGenomeAssembly(),
                        leftContig.name(),
                        leftStrand,
                        leftCoordinates.start()),
                leftStrand,
                leftCoordinates);

        // right breakend
        Contig rightContig = parseContig(variantPosition.getGenomeAssembly(), variantPosition.getContig2());
        ConfidenceInterval rightCi = ConfidenceInterval.of(variantPosition.getCiBeginTwo(), variantPosition.getCiEndTwo());
        Coordinates rightCoordinates = Coordinates.of(CoordinateSystem.zeroBased(),
                variantPosition.getPos2(), rightCi,
                variantPosition.getPos2(), rightCi);
        Strand rightStrand = directionToStrand(variantPosition.getContig2Direction());
        GenomicBreakend right = GenomicBreakend.of(rightContig,
                createBreakendId(variantPosition.getGenomeAssembly(),
                        rightContig.name(),
                        rightStrand,
                        rightCoordinates.start()),
                rightStrand,
                rightCoordinates);

        // assemble breakend variant
        String variantId = createBreakendVariantId(variantPosition.getGenomeAssembly(),
                leftContig.name(),
                leftCoordinates.start(),
                rightContig.name(),
                rightCoordinates.start(),
                variantPosition.getRefAllele(),
                variantPosition.getAltAllele());

        GenomicVariant v = GenomicVariant.of(variantId,
                left,
                right,
                variantPosition.getRefAllele(),
                variantPosition.getAltAllele());

        // The assembly will not be null, as it is null-checked in `parseContig` method above
        GenomicAssembly assembly = ASSEMBLIES.get(variantPosition.getGenomeAssembly());
        VariantMetadata variantMetadata = parseVariantMetadata(variant);
        return CuratedVariant.of(assembly.name(), v, variantMetadata);
    }

    private static Strand directionToStrand(VariantPosition.Direction direction) throws ModelTransformationException {
        return switch (direction) {
            case FWD -> Strand.POSITIVE;
            case REV -> Strand.NEGATIVE;
            default -> throw new ModelTransformationException("Unknown direction value `" + direction + "`");
        };
    }

    private static Contig parseContig(GenomeAssembly genomeAssembly, String contigName) throws ModelTransformationException {
        GenomicAssembly assembly = ASSEMBLIES.get(genomeAssembly);
        if (assembly == null)
            throw new ModelTransformationException("Unknown genome assembly `" + genomeAssembly + "`");

        return assembly.contigByName(contigName);
    }

    private static VariantMetadata parseVariantMetadata(Variant variant) throws ModelTransformationException {
        VariantValidation variantValidation = variant.getVariantValidation();
        return switch (variantValidation.getContext()) {
            case MENDELIAN -> MendelianVariantMetadata.of(variant.getSnippet(), variant.getVariantClass(), variant.getPathomechanism(),
                    variantValidation.getCosegregation(), variantValidation.getComparability(),
                    variantValidation.getRegulator(), variantValidation.getReporterRegulation(),
                    variantValidation.getReporterResidualActivity(), variantValidation.getEmsaValidationPerformed(),
                    variantValidation.getEmsaTfSymbol(), variantValidation.getEmsaGeneId(),
                    variantValidation.getOtherChoices(), variantValidation.getOtherEffect());

            case SOMATIC -> SomaticVariantMetadata.of(variant.getSnippet(), variant.getVariantClass(), variant.getPathomechanism(),
                    variantValidation.getCosegregation(), variantValidation.getComparability(),
                    variantValidation.getRegulator(), variantValidation.getReporterRegulation(),
                    variantValidation.getReporterResidualActivity(), variantValidation.getEmsaGeneId(),
                    variantValidation.getOtherChoices(),
                    variantValidation.getNPatients(), variantValidation.getMPatients());

            case SPLICING -> SplicingVariantMetadata.of(variant.getSnippet(), variant.getVariantClass(), variant.getPathomechanism(),
                    variantValidation.getCosegregation(), variantValidation.getComparability(),
                    variant.getCrypticPosition(), transformCrypticSpliceType(variant.getCrypticSpliceSiteType()), variant.getCrypticSpliceSiteSnippet(),
                    variant.getConsequence(),
                    variantValidation.getMinigeneValidation(), variantValidation.getSiteDirectedMutagenesisValidation(),
                    variantValidation.getRtPcrValidation(), variantValidation.getSrProteinOverexpressionValidation(),
                    variantValidation.getSrProteinKnockdownValidation(), variantValidation.getCDnaSequencingValidation(),
                    variantValidation.getPcrValidation(), variantValidation.getMutOfWtSpliceSiteValidation(),
                    variantValidation.getOtherValidation());

            case INTRACHROMOSOMAL, TRANSLOCATION -> StructuralVariantMetadata.of(variant.getSnippet(), variant.getVariantClass(), variant.getPathomechanism(),
                    variantValidation.getCosegregation(), variantValidation.getComparability());

            case UNRECOGNIZED, NO_CONTEXT -> throw new ModelTransformationException("The code should not have arrived here. Please report to the development team");
        };

    }

    private static SplicingVariantMetadata.CrypticSpliceSiteType transformCrypticSpliceType(CrypticSpliceSiteType crypticSpliceSiteType) throws ModelTransformationException {
        return switch (crypticSpliceSiteType) {
            case FIVE_PRIME -> SplicingVariantMetadata.CrypticSpliceSiteType.FIVE_PRIME;
            case THREE_PRIME -> SplicingVariantMetadata.CrypticSpliceSiteType.THREE_PRIME;
            case NO_CSS -> SplicingVariantMetadata.CrypticSpliceSiteType.NO_CSS;
            case UNRECOGNIZED -> throw new ModelTransformationException("Unknown cryptic splice site type `" + crypticSpliceSiteType + "`");
        };
    }

    private static String createIntrachromosomalVariantId(GenomeAssembly assembly, String contig, int start, int end, String ref, String alt) {
        return String.join("-", assembly.name(), contig, NF.format(start), NF.format(end), ref, alt);
    }

    private static String createBreakendId(GenomeAssembly assembly, String contig, Strand strand, int pos) {
        return String.join("-", assembly.name(), contig, NF.format(pos), strand.isPositive() ? "[+]" : "[-]");
    }

    private static String createBreakendVariantId(GenomeAssembly assembly, String leftContig, int leftPos, String rightContig, int rightPos, String ref, String alt) {
        return String.join("-", assembly.name(), leftContig, NF.format(leftPos), rightContig, NF.format(rightPos), ref, alt);
    }

    private static List<CuratedVariant> transformVariants(List<Variant> variantList) throws ModelTransformationException {
        List<CuratedVariant> variants = new ArrayList<>(variantList.size());
        for (Variant variant : variantList) {
            CuratedVariant curatedVariant = transformVariant(variant);
            variants.add(curatedVariant);
        }
        return variants;
    }

    private static Pedigree transformPedigree(FamilyInfo familyInfo,
                                              org.monarchinitiative.hpo_case_annotator.model.proto.Disease disease,
                                              List<OntologyClass> phenotypes,
                                              List<Variant> variants,
                                              List<CuratedVariant> curatedVariants) throws ModelTransformationException {
        // We have at most single person in the v1 cases.
        TimeElement age;
        try {
            Period period = Period.parse(familyInfo.getAge()).normalized();
            Age a = Age.ofYearsMonthsDays(period.getYears(), period.getMonths(), period.getDays());
            age = TimeElement.of(TimeElement.TimeElementCase.AGE, null, a, null, null);
        } catch (DateTimeParseException e) {
            throw new ModelTransformationException(e);
        }

        Map<String, Genotype> genotypes = new HashMap<>(variants.size());
        for (int i = 0; i < variants.size(); i++) {
            String hex = curatedVariants.get(i).md5Hex();
            Genotype genotype = transformGenotype(variants.get(i).getGenotype());
            genotypes.put(hex, genotype);
        }

        // here we only can assume that the proband had all the features since birth
        List<PhenotypicFeature> observations = transformPhenotypes(phenotypes);

        PedigreeMember member = PedigreeMember.of(familyInfo.getFamilyOrProbandId(),
                DEFAULT_PARENTAL_ID,
                DEFAULT_PARENTAL_ID,
                true, observations, List.of(transformDisease(disease)), genotypes, age,
                // by definition as we only store probands in v1 model
                transformSex(familyInfo.getSex()));

        return Pedigree.of(List.of(member));
    }

    private static Genotype transformGenotype(org.monarchinitiative.hpo_case_annotator.model.proto.Genotype genotype) {
        return switch (genotype) {
            case HETEROZYGOUS -> Genotype.HETEROZYGOUS;
            case HOMOZYGOUS_ALTERNATE -> Genotype.HOMOZYGOUS_ALTERNATE;
            case HOMOZYGOUS_REFERENCE -> Genotype.HOMOZYGOUS_REFERENCE;
            case HEMIZYGOUS -> Genotype.HEMIZYGOUS;

            case UNDEFINED, UNKNOWN_GENOTYPE, UNRECOGNIZED -> Genotype.UNKNOWN;
        };
    }

    private static List<PhenotypicFeature> transformPhenotypes(List<OntologyClass> phenotypeList) {
        List<PhenotypicFeature> phenotypes = new ArrayList<>(phenotypeList.size());

        for (OntologyClass phenotype : phenotypeList)
            // We do not have onset, and we certainly do not have the resolution.
            phenotypes.add(PhenotypicFeature.of(TermId.of(phenotype.getId()), phenotype.getNotObserved(), null, null));

        return phenotypes;
    }

    private static Sex transformSex(org.monarchinitiative.hpo_case_annotator.model.proto.Sex sex) throws ModelTransformationException {
        return switch (sex) {
            case MALE -> Sex.MALE;
            case FEMALE -> Sex.FEMALE;
            case UNRECOGNIZED, UNKNOWN_SEX -> throw new ModelTransformationException("Unknown sex value: " + sex);
        };
    }

    private static DiseaseStatus transformDisease(org.monarchinitiative.hpo_case_annotator.model.proto.Disease disease) throws ModelTransformationException {
        try {
            DiseaseIdentifier diseaseIdentifier = DiseaseIdentifier.of(TermId.of(disease.getDatabase(), disease.getDiseaseId()), disease.getDiseaseName());
            return DiseaseStatus.of(diseaseIdentifier, false);
        } catch (RuntimeException e) {
            throw new ModelTransformationException(e);
        }
    }

    @Override
    public Study encode(DiseaseCase diseaseCase) throws ModelTransformationException {
        String id = ModelUtils.getFileNameFor(diseaseCase);
        Publication publication = transformPublication(diseaseCase.getPublication());
        List<CuratedVariant> variants = transformVariants(diseaseCase.getVariantList());
        Pedigree pedigree = transformPedigree(diseaseCase.getFamilyInfo(), diseaseCase.getDisease(), diseaseCase.getPhenotypeList(), diseaseCase.getVariantList(), variants);
        StudyMetadata metadata = transformMetadata(diseaseCase.getMetadata(), diseaseCase.getBiocurator(), diseaseCase.getSoftwareVersion());

        return FamilyStudy.of(id, publication, variants, pedigree, metadata);
    }

}
