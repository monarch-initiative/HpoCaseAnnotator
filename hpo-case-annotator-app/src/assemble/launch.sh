#!/usr/bin/env sh

##
# This script launch HpoCaseAnnotator app. The app can be launched either by double-clicking on the `launch.sh` script
# in file browser (works on Linux and Mac) or by opening the Terminal and manually launching the script.

java --add-reads org.monarchinitiative.hca.app=ALL-UNNAMED \
  --add-exports=javafx.base/com.sun.javafx.event=org.controlsfx.controls \
  --add-opens javafx.graphics/com.sun.javafx.sg.prism=ALL-UNNAMED \
  --add-opens javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED \
  --add-opens javafx.graphics/com.sun.javafx.util=ALL-UNNAMED \
  --add-opens javafx.base/com.sun.javafx.logging=ALL-UNNAMED \
  -p "lib:hpo-case-annotator-app-@project.version@.jar" -m org.monarchinitiative.hca.app
