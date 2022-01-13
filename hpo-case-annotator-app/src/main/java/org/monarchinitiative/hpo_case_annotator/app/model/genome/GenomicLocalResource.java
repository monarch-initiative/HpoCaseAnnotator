package org.monarchinitiative.hpo_case_annotator.app.model.genome;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public class GenomicLocalResource {

    private final ObjectProperty<Path> assemblyReport = new SimpleObjectProperty<>(this, "assemblyReport");
    private final ObjectProperty<Path> fasta = new SimpleObjectProperty<>(this, "fasta");
    private final ObjectProperty<Path> fastaFai = new SimpleObjectProperty<>(this, "fastaFai");
    private final ObjectProperty<Path> fastaDict = new SimpleObjectProperty<>(this, "fastaDict");


    public static Optional<GenomicLocalResource> createFromFastaPath(File fastaPath) {
        return fastaPath == null
                ? Optional.empty()
                : createFromFastaPath(fastaPath.toPath());
    }


    public static Optional<GenomicLocalResource> createFromFastaPath(Path fastaPath) {
        if (fastaPath==null) return Optional.empty();

        File fastaFile = fastaPath.toFile();
        String fastaName = fastaFile.getName();
        if (!fastaName.endsWith(".fa")) {
            return Optional.empty();
        }

        Path parent = fastaFile.getParentFile().toPath();
        Path assemblyReport = parent.resolve(fastaName.replace(".fa", ".assembly-report.txt"));
        Path fastaFai = parent.resolve(fastaName + ".fai");
        Path fastaDict = parent.resolve(fastaName + ".dict");

        return Optional.of(new GenomicLocalResource(assemblyReport, fastaPath, fastaFai, fastaDict));
    }

    public GenomicLocalResource() {
    }

    public GenomicLocalResource(Path assemblyReport, Path fasta, Path fastaFai, Path fastaDict) {
        this.assemblyReport.set(assemblyReport);
        this.fasta.set(fasta);
        this.fastaFai.set(fastaFai);
        this.fastaDict.set(fastaDict);
    }

    public Path getAssemblyReport() {
        return assemblyReport.get();
    }

    public void setAssemblyReport(Path assemblyReport) {
        this.assemblyReport.set(assemblyReport);
    }

    public ObjectProperty<Path> assemblyReportProperty() {
        return assemblyReport;
    }

    public Path getFasta() {
        return fasta.get();
    }

    public void setFasta(Path fasta) {
        this.fasta.set(fasta);
    }

    public ObjectProperty<Path> fastaProperty() {
        return fasta;
    }

    public Path getFastaFai() {
        return fastaFai.get();
    }

    public void setFastaFai(Path fastaFai) {
        this.fastaFai.set(fastaFai);
    }

    public ObjectProperty<Path> fastaFaiProperty() {
        return fastaFai;
    }

    public Path getFastaDict() {
        return fastaDict.get();
    }

    public void setFastaDict(Path fastaDict) {
        this.fastaDict.set(fastaDict);
    }

    public ObjectProperty<Path> fastaDictProperty() {
        return fastaDict;
    }
}
