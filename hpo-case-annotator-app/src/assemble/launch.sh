#!/usr/bin/env sh

##
# Launch HpoCaseAnnotator app either by double-clicking on the script in file browser
# or by opening the Terminal and running manually.

java --add-reads org.monarchinitiative.hca.app=ALL-UNNAMED \
  --add-exports=javafx.base/com.sun.javafx.event=org.controlsfx.controls \
  --add-opens javafx.graphics/com.sun.javafx.sg.prism=ALL-UNNAMED \
  --add-opens javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED \
  --add-opens javafx.graphics/com.sun.javafx.util=ALL-UNNAMED \
  --add-opens javafx.base/com.sun.javafx.logging=ALL-UNNAMED \
  -p "lib:hpo-case-annotator-app-@project.version@.jar" -m org.monarchinitiative.hca.app