============
Installation
============

Requirements
------------

Hpo Case Annotator needs Java 8 to run. You can determine what version of Java you have on your computer by entering the following command::

  $ java -version
  java version "1.8.0_161"
  Java(TM) SE Runtime Environment (build 1.8.0_161-b12)
  Java HotSpot(TM) 64-Bit Server VM (build 25.161-b12, mixed mode)


Getting the app for your operating system
-----------------------------------------

- **Linux**

Most users should use the prebuilt App `HpoCaseAnnotator-1.0.6-uber.jar` available from https://github.com/monarch-initiative/HpoCaseAnnotator/releases. The `JAR` file is executed::

  $ java -jar HpoCaseAnnotator-1.0.6-uber.jar

It is also possible to build the VPV application from source using Maven::

  $ git clone https://github.com/monarch-initiative/HpoCaseAnnotator.git
  cd HpoCaseAnnotator
  mvn package

Built ``JAR`` file will be present in ``target`` directory.

- **Mac OSX**

Mac users should download the same prebuilt ``JAR`` file as Linux users.

- **Windows**

Windows users can use `HpoCaseAnnotator-1.0.6.exe` available from https://github.com/monarch-initiative/HpoCaseAnnotator/releases. User will be prompted to download Java Runtime Environment, if Java is not installed on the machine.

Initial setup
-------------

After startup a dialog window will be opened.

.. image:: img/startup.png

Not all the functionality is *not* enabled upon the first startup, since there are some resources that need to be downloaded first. Click on ``Settings | Set resources``:

.. image:: img/set_resources.png

Reference genome
^^^^^^^^^^^^^^^^
Hpo Case Annotator needs a sequence of the reference genome in order to perform quality checks. Here we tell the app where we have stored the Genome FASTA files and the corresponding fasta index (fai) file. We will use this to check whether the wildtype sequence entered for each mutation matches the corresponding genomic position.

We will use a FASTA file for each chromosome of the human genome. For now, we will use build 37, i.e., *GRCh37*, which was also known as *hg19*. We will assume that there is a fasta index file in the same directory that has the exact same same as the fasta file except that it ends with "fai" instead of "fa".

If you do not have this data on your computer anyway, then you can do the following to set things up:

- Download the file **chromFa.tar.gz** from UCSC. It is 905 M. Remember to use the file from *GRCh37* (i.e., hg19) and not *GRCh38*! Here is the URL: http://hgdownload.soe.ucsc.edu/goldenPath/hg19/bigZips/.
- Use ``tar xvfz`` to unpack these files (warning: they will unpack without a surrounding directory!) You will get a lot of FASTA files including many for scaffolds (we only need the canonical chromosomes for now, but the scaffold files will not hurt either).
- We will use ``samtools`` to create a fasta index file (fai). Download/build it here: https://github.com/samtools/samtools
- The command to create an index is then (for example.fa) simply ``samtools faindex example.fa``
- The index file basically will allow us to quickly navigate to the right part of the genome FASTA file to extract the sequence we need to check our mutation data (HRMDgui uses the HTS-JDK library to do this!).

If you have downloaded and unpacked the genome fasta files as above (I recommend to use a directory called ``hg19`` for this), then you can use the following shell command to create FAI files for each of the FASTA files (note that I leave out the chrUn and random scaffolds, they do not have anything of interest for this project).

::

  for f in `find . -name '*.fa' | grep -v chrUn | grep -v random`
  do
    echo "$f"
    samtools faidx $f
  done

Obviously you will need to adjust the command if ``samtools`` is not in your path. This is all we need for the genomic position Q/C routines.

HPO obo
^^^^^^^
The app will automatically download the newest version of Human Phenotype Ontology (HPO) in ``OBO`` format.
Go to the <b>setup</b> menu and choose <i>HPO download</i>.
This needs to be done once (and can be updated as necessary). We download a copy of the <b>hp.obo</b> file so that the
App can autocomplete the HPO terms names (the user should just enter the HPO ID (e.g., HP:0001234).
