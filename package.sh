#!/usr/bin/env bash

##
# Run this script as `bash package.sh` to build a native app.
# The script works on both UNIX and OSX platforms.


SRC_DIR=hpo-case-annotator-gui
APP_NAME="Hpo Case Annotator"
CMD_NAME="hpo-case-annotator"
VERSION=1.0.15-SNAPSHOT
BUILD_DIR=${SRC_DIR}/target
JAR_NAME=hpo-case-annotator-gui-1.0.15-SNAPSHOT.jar
VENDOR="The Monarch Initiative"
DESCRIPTION="Hpo Case Annotator simplifies curation of published pathogenic human variants."
COPYRIGHT="Copyright 2021, All rights reserved"
ICON="${BUILD_DIR}/classes/img/app-icon" # Update icon once we have a better one
PACKAGE_DIR=/tmp/hca_app

# build
printf "Building Hpo Case Annotator\n"
./mvnw clean package -DskipTests


# prepare packaging folder
printf "Creating temporary directory at %s\n" ${PACKAGE_DIR}
mkdir -p $PACKAGE_DIR
cp $BUILD_DIR/$JAR_NAME $PACKAGE_DIR

# package
function detect_platform() {
    if [[ "$OSTYPE" == "linux-gnu" ]]; then
      echo "linux"
    elif [[ "$OSTYPE" =~ darwin.* ]]; then
      echo "osx";
    else
      # More people use OSX
      echo "osx"
    fi
}

PLATFORM=$(detect_platform)
printf "\nPackaging Hpo Case Annotator for %s\n" "${PLATFORM}"
if [[ "$PLATFORM" == "linux" ]]; then
  # setup Linux CLI
  jpackage --name "${APP_NAME}" --input "${PACKAGE_DIR}" --main-jar "${JAR_NAME}" --linux-menu-group Science --linux-shortcut --icon "${ICON}.png" --linux-package-name "${CMD_NAME}" --app-version "${VERSION}" --description "${DESCRIPTION}" --vendor "${VENDOR}" --license-file LICENSE --copyright "${COPYRIGHT}"
elif [[ "$PLATFORM" == "osx" ]]; then
  # setup OSX CLI
  jpackage --name "${APP_NAME}" --input "${PACKAGE_DIR}" --main-jar "${JAR_NAME}" --mac-package-name "${CMD_NAME}" --icon "${ICON}.icns" --app-version "${VERSION}" --description "${DESCRIPTION}" --vendor "${VENDOR}" --license-file LICENSE --copyright "${COPYRIGHT}"
else
  printf "Unknown platform %s\n. Abort." "${PLATFORM}"
  exit
fi

# clean up
printf "Removing the temporary directory %s\n" ${PACKAGE_DIR}
rm -r $PACKAGE_DIR
printf "Done!\n"
