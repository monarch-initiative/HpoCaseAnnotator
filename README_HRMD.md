# Human Regulatory Mutation Database GUI

Human Regulatory mutation Database (*HRMD*) GUI is Java app created with aim to make the biocuration of published pathogenic human variants easier.

### Building & installation
Running *HRMD* requires *Java 1.8* or higher, the app is built with *Maven*.

- **build** - building a the *distribution ZIP archive* requires running:

```bash
git clone https://github.com/monarch-initiative/hrmd.git
cd hrmd/hrmd-gui
git checkout fxml  # change to the 'fxml' branch

mvn package
```
> After running of these commands, the *distribution ZIP archive* containing JAR file and all resources required to run the app will be created here: `target/HrmdGuiFX-1.0.4-full-distribution.zip`

- **installation** - the recommended way of using the *HRMD* is to copy the distribution file into some other directory (`/home/user/software` or similar). Then, to start the app from command line:

```bash
cd /home/user/software
unzip HrmdGuiFX-1.0.4-full-distribution.zip
cd HrmdGuiFX-1.0.4
java -jar HrmdGuiFX-1.0.4.jar
```

### Initialization
During the first start of the app the user is asked to:

- provide path to **data directory** - resource files such as **HP.obo** and **Entrez genes** will be downloaded there
- provide path to directory with **reference genome** *Fasta* files. *Fasta* files are used in Q/C steps, an archive with the files can be downloaded from [here](https://s3-eu-west-1.amazonaws.com/danisd/hg19.tar.gz)
- click on the *Download* button for **HPO.obo** and **Entrez genes**. Files will be downloaded to the *Data directory*
- set the path to *curated files directory* where the curated variants will be stored
- set the **biocurator ID**

### Content of GUI elements
The entries in many GUI elements (such as the chromosomes, pathomechanisms, etc.) can be modified by editing the *choice-basket.yml* file. This file is a part of the assembly ZIP file and must be placed in the same directory where the JAR file is. Please follow [YAML syntax](https://en.wikipedia.org/wiki/YAML) when editing the file.

Contact peter.robinson@jax.org or daniel.danis@jax.org for any further details. The project is currently in a preliminary stage.


