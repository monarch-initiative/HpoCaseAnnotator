Welcome to Hpo Case Annotator's documentation!
==============================================

*Hpo Case Annotator* is an application for biocuration of case reports, families and cohorts of patients
published in scientific literature.
Each curated case contains details of the disease-causing variants, phenotype, disease, and other metadata.

Curated data is stored in `JSON` format (one file for each case). The application also performs a
number of Q/C checks to ensure data consistency.

The app can export data in `Phenopacket <https://phenopacket-schema.readthedocs.io/en/master>`_ format,
but it contains a superset of the information required for phenopackets.
Future versions of this app will probably converge to the Phenopacket format, and currently the app is still
in a preliminary stage of development, although it works as advertised.

.. toctree::
   :maxdepth: 1
   :caption: Index:

   requirements
   setup
   entering_data
   validating


Indices and tables
==================

* :ref:`genindex`
* :ref:`modindex`
* :ref:`search`

