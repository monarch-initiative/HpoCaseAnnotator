#!/usr/bin/env sh

##
# Launch HpoCaseAnnotator app either by double-clicking on the script in file browser
# or by opening the Terminal and running the script manually.

java --add-reads org.monarchinitiative.hca.app=ALL-UNNAMED \
  --add-exports=javafx.base/com.sun.javafx.event=org.controlsfx.controls \
  --module-path "lib" \
  --module org.monarchinitiative.hca.app
