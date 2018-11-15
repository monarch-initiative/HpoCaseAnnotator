package org.monarchinitiative.hpo_case_annotator.core.refgenome;

import java.io.File;
import java.util.Objects;

public enum GenomeAssembly {

    HG19("hg19"), HG38("hg38");

    private final String value;

    private File fastaPath = null;


    GenomeAssembly(String value) {
        this.value = value;
    }


    public static GenomeAssembly defaultBuild() {
        return GenomeAssembly.HG38;
    }


    public static GenomeAssembly fromValue(String value) {
        Objects.requireNonNull(value, "Genome build cannot be null");
        switch (value.toLowerCase()) {
            case "hg19":
            case "hg37":
            case "grch37":
                return HG19;
            case "hg38":
            case "grch38":
                return HG38;
            default:
                throw new InvalidGenomeAssemblyException(String.format("'%s' is not a valid/supported genome assembly.", value));
        }
    }


    public File getFastaPath() {
        return fastaPath;
    }


    public void setFastaPath(File fastaPath) {
        this.fastaPath = fastaPath;
    }


    @Override
    public String toString() {
        return value;
    }


    public static class InvalidGenomeAssemblyException extends RuntimeException {

        public InvalidGenomeAssemblyException() {
        }


        public InvalidGenomeAssemblyException(String message) {
            super(message);
        }


        public InvalidGenomeAssemblyException(String message, Throwable cause) {
            super(message, cause);
        }


        public InvalidGenomeAssemblyException(Throwable cause) {
            super(cause);
        }


        public InvalidGenomeAssemblyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
