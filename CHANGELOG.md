# HRMD gui

### v1.0.14
- use `phenopacket-schema v1.0.0-RC3`
- during export, store phenopacket-schema version within phenopacket


### v1.0.13
- add value from *proband/family info* into file name when saving data
- use `phenopacket-schema v1.0.0-RC1`
- update versions of Maven plugins and dependencies
- improve parsing of publication data from PubMed
- set *OMIM* as the default disease database  

### v1.0.12
- add `software_version` attribute to data model for storing name and version of software used to create data
- store app's data in a version-indepenedent location that makes upgrading the app easier (`.hpo-case-annotator` instead of `.hpo-case-annotator-${project.version}`)
- use `phenopacket-schema v0.4.0`, `protobuf 3.7.0`, `hpotextmining-gui 0.2.6`

### v1.0.11
- use `GRCH_37` as the default genome assembly
- remove obsolete `NCBI36 (HG18)` genome assembly
- use `phenopacket-schema v0.3.0` for export, `phenol v1.3.3` for HPO
- add support for [Variant validator](https://variantvalidator.org/) API when curating variants either using genome, or transcript coordinates
- fix bugs, improve user experience

### v1.0.9
- add support for GRCh38, GRCh37
- store models in *JSON* format
- implement PhenoPacket export
- use *HpoTextMining* v0.2.5
- use *Phenol* for ontology parsing
- actually, many more improvements..

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
