# Hpo Case Annotator GUI

Hpo Case Annotator (HCA) GUI is Java app created with aim to make the biocuration of published pathogenic human variants easier.

### Building & installation
Running *HCA* requires *Java 1.8*, the app is built with *Maven*.

- **build** - building a the *distribution ZIP archive* requires running:

```bash
git clone https://github.com/monarch-initiative/HpoCaseAnnotator.git
cd HpoCaseAnnotator

mvn package
```
> After running these commands, the *distribution ZIP archive* containing JAR file and all resources required to run the app will be created at `hpo-case-annotator-gui/target/HpoCaseAnnotator-Gui-1.0.11-distribution.zip`

- **installation** - the recommended way of using the *HCA* is to copy the distribution file into some other directory (`/home/user/software` or similar). Then, to start the app from command line:

```bash
cd /home/user/software
unzip HpoCaseAnnotator-Gui-1.0.11-distribution.zip
cd HpoCaseAnnotator-Gui-1.0.11-distribution
java -jar HpoCaseAnnotator-Gui-1.0.11.jar
```

### Initial set up
Some buttons and fields are disabled after the first start of the GUI. You have to download some resources in order to enable all the functions. Click on `Settings | Set resources` menu and:

- download reference genome files
- download ontology and *Entrez genes* file
- set the path to *curated files directory* where the data for a project will be stored
- set your **biocurator ID**

### Content of GUI elements
The entries in many GUI elements (such as the chromosomes, pathomechanisms, etc.) can be modified by editing the *choice-basket.yml* file. This file is a part of the assembly ZIP file and must be placed in the same directory where the JAR file is. Please follow [YAML syntax](https://en.wikipedia.org/wiki/YAML) when editing the file.

Contact peter.robinson@jax.org or daniel.danis@jax.org for any further details. The project is currently in a preliminary stage.

