# Package Hpo Case Annotator as a self-contained Java application

The packaging must be done on the target platform. E.g. to create the native app for Linux, the packaging must be
done on Linux, using the Linux JDK.

---

## Packaging

The repo contains scripts to package Hpo Case Annotator for Linux and Mac.

Check the contents of `package.sh`.

### Linux (DEB) 

Run the following script on a Linux machine to build a DEB installer:

```bash
cd HpoCaseAnnotator
bash package.sh 
```

The script builds a `hpo-case-annotator_1.0.15-SNAPSHOT-1_amd64.deb` package file in the current directory. To (un)install the package, run:

```bash
sudo dpkg -i hpo-case-annotator_1.0.15-SNAPSHOT-1_amd64.deb
sudo dpkg --purge hpo-case-annotator
```

The app is installed into `/opt` folder. A shortcut *Hpo Case Annotator* is available to launch the app from the launcher/dock.

### Mac OS (DMG)

Run the same script as for Linux:

```bash
cd HpoCaseAnnotator
bash package.sh 
```

The script builds a DMG file in the current directory. Install the DMG by mounting and/or dragging the app into the *Aplications* folder.

## Learn more

Based on docs [here](https://docs.oracle.com/en/java/javase/16/jpackage).
