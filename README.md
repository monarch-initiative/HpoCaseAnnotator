[![CI CD](https://github.com/monarch-initiative/HpoCaseAnnotator/actions/workflows/maven.yml/badge.svg)](https://github.com/monarch-initiative/HpoCaseAnnotator/actions/workflows/maven.yml/badge.svg)

# Hpo Case Annotator

Hpo Case Annotator (HCA) is Java app created with aim to make the biocuration of published pathogenic human variants easier.

### Building & installation
Running *HCA* requires *Java 16* or better, the app is built with *Maven*.

- **build** - building a the *distribution ZIP archive* requires running:

```bash
git clone https://github.com/monarch-initiative/HpoCaseAnnotator.git
cd HpoCaseAnnotator

./mvnw clean package
```
> After running these commands, the executable JAR file are available at `hpo-case-annotator-app/target/hpo-case-annotator-app-2.0.0.jar`

- **installation** - the recommended way of using the *HCA* is to copy the executable JAR into a directory (`/home/user/software` or similar). Then, to start the app from command line:

```bash
cd /home/user/software
java -jar hpo-case-annotator-app-2.0.0.jar
```

### Initial set up
Some buttons and fields are disabled after the first start of the GUI. You have to download some resources in order to enable all the functions. Click on `File > Settings > Initialize resources` menu and:

- download reference genome files
- download HPO and *Entrez genes* files
- set the path to *curated files directory*, where the data for a project will be stored
- download *Liftover chain files*
- set your **biocurator ID**

## Issues?

Feel free to submit an issue into the [tracker](https://github.com/monarch-initiative/HpoCaseAnnotator/issues).

