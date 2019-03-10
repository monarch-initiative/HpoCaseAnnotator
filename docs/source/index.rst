.. Hpo Case Annotator documentation master file, created by
   sphinx-quickstart on Thu Aug  2 16:42:11 2018.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

Welcome to Hpo Case Annotator's documentation!
==============================================

*Hpo Case Annotator* is an application designed to help biocurate pathogenic variants published in scientific literature.
Each case that is curated will contain details of the disease-causing variants, phenotype, disease, and other metadata.
Curated data is stored in ``JSON`` format (one file for each case). The application is also designed to perform a
number of Q/C checks to ensure that the data is consistent. The app can export data in
`Phenopacket <https://github.com/phenopackets/phenopacket-schema>`_ format, but it contains a superset of the
information required for phenopackets. Future versions of this app will probably converge to the Phenopacket
format, and currently the app is still in a preliminary stage of development, although it works as advertized.

.. toctree::
   :maxdepth: 1
   :caption: Index:

   requirements
   installation
   entering_data
   validating


Indices and tables
==================

* :ref:`genindex`
* :ref:`modindex`
* :ref:`search`

