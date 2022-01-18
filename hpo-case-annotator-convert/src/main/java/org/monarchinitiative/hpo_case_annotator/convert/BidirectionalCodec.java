package org.monarchinitiative.hpo_case_annotator.convert;

public interface BidirectionalCodec<SOURCE, TARGET> extends Codec<SOURCE, TARGET> {

    SOURCE decode(TARGET data) throws ModelTransformationException;
}
