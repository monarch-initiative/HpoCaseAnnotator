# HRMD gui

### v1.0.6
- add `ID_SUMMARY` column to table exported for GPI project by `PhenoModelExporter`
- create *uber-JAR*

### v1.0.4
- useHpoTextMining v0.2.0 from Maven central

### v1.0.3
- use HpoTextMining v0.1.0 (displaying ontology tree)
- adjust logging configuration

### v1.0.2
- improve viewing curated publications (proband ID, variants, variant classes)
- allow to view and edit current publication attributes
- fix some failing tests

### v1.0.1
- don't allow whitespaces in PMID lookup text field
- add *export in GPI format*
- fix bug in *Genomic Position Validator*
    - removing HPO terms from model
- offer creation of new model after setting a new publication
- *TSV exporter* - use file name instead of first author as a feature

## v1.0.0
- rewrite major part of the code
- revise data model structure
- add tests to crucial parts of code
- add lookup of publication by PMID