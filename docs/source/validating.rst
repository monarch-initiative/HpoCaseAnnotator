==========
Validation
==========

Various quality checks are being performed here.

This text needs to be further refined
#####################################

We will only include a mutation in the HRMD if there is adequate evidence for
its pathogenicity. As a general rule, there should be some experimental
evidence for the mutation changing gene regulation of a target gene in some
way. For some heavily studied genes, we will accept a mutation if it seems
to be very similar to other published mutations (e.g., it lies on the same
predicted transcription factor binding site as another mutation for which
experimental evidence is available). Add as much evidence as possible.
It is expected that at least one of the evidence categories will apply
to each mutation.

1. reporter - Luciferase assay (or the similar CAT assay) to judge transcriptional activity. Indicate whether the mutation is associated with increased activity (up) or decreased activity (down) as compared with the wildtype construct (in percent).
2. EMSA - EMSA (electrophoretic mobility shift assay). This is used to indicate whether a protein binds to a given DNA sequence. For our purposes, we are referring to the protein affected by the mutation. Enter the corresponding protein if there is a change in binding. Enter the Entrez Gene ID and Gene Symbol of the protein that is affected by the mutation (usually a transcription factor)
3. cosegregation - enter yes if the mutation cosegregates with the disease in the family being investigated.
4. comparability - this is the weakest evidence class. Enter yes if the reason for believing that the mutation is pathogenic is simply that it is comparable to other published regulatory mutations in the gene.
5. other - this is for any other kind of experimental assay that shows an effect of a regulatory or non-coding mutation. Note that for now the categories are hard coded into the Java code, this should be put into some kind of configuration file in the future. The categories are at present:

Telomerase. Telomerase lengthening assay.

