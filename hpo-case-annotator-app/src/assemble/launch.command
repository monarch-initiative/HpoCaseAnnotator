#!/usr/bin/env sh

##
# Launch HpoCaseAnnotator app either by double-clicking on the script in file browser
# or by opening the Terminal and running the script manually.

current_dir=$(dirname $(readlink ${0} || echo ${0}))
cd ${current_dir}

java --add-reads org.monarchinitiative.hca.app=ALL-UNNAMED \
  --add-exports=javafx.base/com.sun.javafx.event=org.controlsfx.controls \
  --module-path "lib" \
  --module org.monarchinitiative.hca.app
