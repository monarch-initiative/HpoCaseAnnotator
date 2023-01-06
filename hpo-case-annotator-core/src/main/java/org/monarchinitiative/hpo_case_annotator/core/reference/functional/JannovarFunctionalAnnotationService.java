package org.monarchinitiative.hpo_case_annotator.core.reference.functional;

import de.charite.compbio.jannovar.annotation.*;
import de.charite.compbio.jannovar.annotation.builders.AnnotationBuilderOptions;
import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.JannovarDataSerializer;
import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.data.SerializationException;
import de.charite.compbio.jannovar.reference.PositionType;
import org.monarchinitiative.hpo_case_annotator.model.HpoCaseAnnotatorException;
import org.monarchinitiative.svart.CoordinateSystem;
import org.monarchinitiative.svart.GenomicVariant;
import org.monarchinitiative.svart.Strand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public class JannovarFunctionalAnnotationService implements FunctionalAnnotationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JannovarFunctionalAnnotationService.class);
    private static final AnnotationBuilderOptions OPTIONS = new AnnotationBuilderOptions(true, false);
    private final ReferenceDictionary referenceDictionary;
    private final VariantAnnotator variantAnnotator;

    public static JannovarFunctionalAnnotationService of(Path jannovarCache) throws HpoCaseAnnotatorException {
        try {
            return of(new JannovarDataSerializer(jannovarCache.toString()).load());
        } catch (SerializationException e) {
            throw new HpoCaseAnnotatorException(e);
        }
    }

    public static JannovarFunctionalAnnotationService of(JannovarData jannovarData) {
        return new JannovarFunctionalAnnotationService(jannovarData);
    }

    private JannovarFunctionalAnnotationService(JannovarData jannovarData) {
        referenceDictionary = jannovarData.getRefDict();
        variantAnnotator = new VariantAnnotator(jannovarData.getRefDict(), jannovarData.getChromosomes(), OPTIONS);
    }

    @Override
    public List<FunctionalAnnotation> annotate(GenomicVariant variant) {
        if (variant.isSymbolic())
            // Annotation of symbolic variants is not yet supported.
            return List.of();

        Integer contig = referenceDictionary.getContigNameToID().get(variant.contigName());
        if (contig == null) {
            LOGGER.warn("Unknown contig name {} for variant {}", variant.contigName(), variant);
            return List.of();
        }

        VariantAnnotations annotations;
        try {
            annotations = variantAnnotator.buildAnnotations(contig, variant.startOnStrandWithCoordinateSystem(Strand.POSITIVE, CoordinateSystem.oneBased()), variant.ref(), variant.alt(), PositionType.ONE_BASED);
        } catch (AnnotationException e) {
            if (!(e instanceof InvalidGenomeVariant))
                LOGGER.warn("Error while annotating variant: {}", e.getMessage(), e);
            return List.of();
        }
        return annotations.getAnnotations().stream()
                .map(toFunctionalAnnotation())
                .toList();
    }

    private static Function<Annotation, FunctionalAnnotation> toFunctionalAnnotation() {
        return ann -> {
            List<String> effects = ann.getEffects().stream()
                    .sorted()
                    .map(VariantEffect::toString)
                    .toList();
            return FunctionalAnnotation.of(ann.getGeneSymbol(), ann.getTranscript().getAccession(), effects, ann.getCDSNTChangeStr(), ann.getProteinChangeStr());
        };
    }
}
