package org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata;

import java.util.Objects;

public abstract class VariantMetadata {

    private final VariantMetadataContext variantMetadataContext;
    private final String snippet;
    private final String variantClass;
    private final String pathomechanism;
    private final boolean cosegregation;
    private final boolean comparability;

    protected VariantMetadata(VariantMetadataContext variantMetadataContext,
                              String snippet,
                              String variantClass,
                              String pathomechanism,
                              boolean cosegregation,
                              boolean comparability) {
        this.variantMetadataContext = Objects.requireNonNull(variantMetadataContext, "Metadata context cannot be null");
        this.snippet = Objects.requireNonNull(snippet, "Snippet cannot be null");
        this.variantClass = Objects.requireNonNull(variantClass, "Variant class cannot be null");
        this.pathomechanism = Objects.requireNonNull(pathomechanism, "Pathomechanism cannot be null");
        this.cosegregation = cosegregation;
        this.comparability = comparability;
    }

    public static VariantMetadata of(VariantMetadataContext variantMetadataContext,
                                     String snippet,
                                     String variantClass,
                                     String pathomechanism,
                                     boolean cosegregation,
                                     boolean comparability) {
        return new VariantMetadataDefault(variantMetadataContext,
                snippet,
                variantClass,
                pathomechanism,
                cosegregation,
                comparability);
    }

    public VariantMetadataContext getVariantMetadataContext() {
        return variantMetadataContext;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getVariantClass() {
        return variantClass;
    }

    public String getPathomechanism() {
        return pathomechanism;
    }

    public boolean isCosegregation() {
        return cosegregation;
    }

    public boolean isComparability() {
        return comparability;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VariantMetadata that = (VariantMetadata) o;
        return cosegregation == that.cosegregation && comparability == that.comparability && variantMetadataContext == that.variantMetadataContext && Objects.equals(snippet, that.snippet) && Objects.equals(variantClass, that.variantClass) && Objects.equals(pathomechanism, that.pathomechanism);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variantMetadataContext, snippet, variantClass, pathomechanism, cosegregation, comparability);
    }

    @Override
    public String toString() {
        return "VariantMetadata{" +
                "variantMetadataContext=" + variantMetadataContext +
                ", snippet='" + snippet + '\'' +
                ", variantClass='" + variantClass + '\'' +
                ", pathomechanism='" + pathomechanism + '\'' +
                ", cosegregation=" + cosegregation +
                ", comparability=" + comparability +
                '}';
    }
}
