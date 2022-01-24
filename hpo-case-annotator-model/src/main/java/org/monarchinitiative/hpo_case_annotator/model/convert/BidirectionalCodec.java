package org.monarchinitiative.hpo_case_annotator.model.convert;

public interface BidirectionalCodec<SOURCE, TARGET> extends Codec<SOURCE, TARGET> {

    SOURCE decode(TARGET data) throws ModelTransformationException;
}
