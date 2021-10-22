package org.monarchinitiative.hpo_case_annotator.convert;

import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;
import org.monarchinitiative.hpo_case_annotator.model.proto.*;
import org.monarchinitiative.hpo_case_annotator.model.v2.DiseaseStatus;
import org.monarchinitiative.hpo_case_annotator.model.v2.Publication;
import org.monarchinitiative.hpo_case_annotator.model.v2.Sex;
import org.monarchinitiative.hpo_case_annotator.model.v2.*;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.*;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.svart.*;

import java.time.Instant;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.*;

class V1toV2Codec implements Codec<DiseaseCase, Study> {

    private static final String DEFAULT_VARIANT_ID = "";
    private static final String DEFAULT_PARENTAL_ID = "";
    private static final V1toV2Codec INSTANCE = new V1toV2Codec();
    private static final Map<GenomeAssembly, GenomicAssembly> ASSEMBLIES = Map.of(
            GenomeAssembly.NCBI_36, GenomicAssembly.readAssembly(Objects.requireNonNull(V1toV2Codec.class.getResourceAsStream("GCF_000001405.12_NCBI36_assembly_report.txt"), "Missing genome hg18 assembly report file. Contact developers")),
            GenomeAssembly.GRCH_37, GenomicAssemblies.GRCh37p13(),
            GenomeAssembly.GRCH_38, GenomicAssemblies.GRCh38p13()
    );

    private V1toV2Codec() {
    }

    static V1toV2Codec getInstance() {
        return INSTANCE;
    }

    private static Publication transformPublication(org.monarchinitiative.hpo_case_annotator.model.proto.Publication publication) throws ModelTransformationException {
        List<String> authors = Arrays.stream(publication.getAuthorList().split(","))
                .map(String::trim)
                .toList();

        int year;
        try {
            year = Integer.parseInt(publication.getYear());
        } catch (NumberFormatException e) {
            throw new ModelTransformationException("Invalid publication year: " + publication.getYear());
        }

        return Publication.of(authors, publication.getTitle(), publication.getJournal(), year, publication.getVolume(), publication.getPages(), publication.getPmid());
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

        VariantMetadata variantMetadata = parseVariantMetadata(variant);

        return CuratedVariant.sequenceSymbolic(contig,
                DEFAULT_VARIANT_ID,
                Strand.POSITIVE,
                coordinates,
                variantPosition.getRefAllele(),
                variantPosition.getAltAllele(),
                // this changelength calculation should be OK since these variants should be symbolic
                variantPosition.getRefAllele().length() - variantPosition.getAltAllele().length(),
                variantMetadata);
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

        VariantMetadata variantMetadata = parseVariantMetadata(variant);
        return CuratedVariant.sequenceSymbolic(contig,
                DEFAULT_VARIANT_ID,
                Strand.POSITIVE,
                coordinates,
                variantPosition.getRefAllele(),
                String.format("<%s>", variantPosition.getAltAllele()),
                changeLength,
                variantMetadata);
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
        Breakend left = Breakend.of(leftContig,
                DEFAULT_VARIANT_ID,
                directionToStrand(variantPosition.getContigDirection()),
                leftCoordinates);

        // right breakend
        Contig rightContig = parseContig(variantPosition.getGenomeAssembly(), variantPosition.getContig2());
        ConfidenceInterval rightCi = ConfidenceInterval.of(variantPosition.getCiBeginTwo(), variantPosition.getCiEndTwo());
        Coordinates rightCoordinates = Coordinates.of(CoordinateSystem.zeroBased(),
                variantPosition.getPos2(), rightCi,
                variantPosition.getPos2(), rightCi);
        Breakend right = Breakend.of(rightContig,
                DEFAULT_VARIANT_ID,
                directionToStrand(variantPosition.getContig2Direction()),
                rightCoordinates);

        VariantMetadata variantMetadata = parseVariantMetadata(variant);
        return CuratedVariant.breakend(DEFAULT_VARIANT_ID,
                left,
                right,
                variantPosition.getRefAllele(),
                variantPosition.getAltAllele(),
                variantMetadata);
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
        // we only have a single person in the v1 cases
        Period age;
        try {
            age = Period.parse(familyInfo.getAge());
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
        List<PhenotypicObservation> observations = transformPhenotypes(age, phenotypes);

        PedigreeMember member = PedigreeMember.of(familyInfo.getFamilyOrProbandId(),
                DEFAULT_PARENTAL_ID,
                DEFAULT_PARENTAL_ID,
                age,
                List.of(transformDisease(disease)),
                genotypes,
                observations,
                true, // by definition as we only store probands in v1 model
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

    private static List<PhenotypicObservation> transformPhenotypes(Period age, List<OntologyClass> phenotypeList) {
        Set<PhenotypicFeature> phenotypes = new HashSet<>(phenotypeList.size());
        for (OntologyClass phenotype : phenotypeList) {
            phenotypes.add(PhenotypicFeature.of(TermId.of(phenotype.getId()), phenotype.getNotObserved()));
        }
        return List.of(PhenotypicObservation.of(AgeRange.sinceBirthUntilAge(age), phenotypes));
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
            return DiseaseStatus.of(TermId.of(disease.getDatabase(), disease.getDiseaseId()), disease.getDiseaseName(), false);
        } catch (RuntimeException e) {
            throw new ModelTransformationException(e);
        }
    }

    @Override
    public Study encode(DiseaseCase diseaseCase) throws ModelTransformationException {
        Publication publication = transformPublication(diseaseCase.getPublication());
        List<CuratedVariant> variants = transformVariants(diseaseCase.getVariantList());
        Pedigree pedigree = transformPedigree(diseaseCase.getFamilyInfo(), diseaseCase.getDisease(), diseaseCase.getPhenotypeList(), diseaseCase.getVariantList(), variants);
        StudyMetadata metadata = transformMetadata(diseaseCase.getMetadata(), diseaseCase.getBiocurator(), diseaseCase.getSoftwareVersion());

        return FamilyStudy.of(publication, variants, pedigree, metadata);
    }

    @Override
    public DiseaseCase decode(Study data) throws ModelTransformationException {
        throw new ModelTransformationException("Unsupported operation");
    }
}
