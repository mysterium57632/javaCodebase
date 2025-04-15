#!/bin/bash

GROUP_ID="de.paull"
ARTIFACT_ID="javaCodebase"
VERSION="0000"
LIB_DIR="./"  # directory where your JAR is stored

# Find the first .jar file in LIB_DIR
JAR_FILE=$(find "$LIB_DIR" -maxdepth 1 -name "*.jar" | head -n 1)

if [ -z "$JAR_FILE" ]; then
  echo "No .jar file found in $LIB_DIR"
  exit 1
fi

echo "Found JAR: $JAR_FILE"

if [[ JAR_FILE =~ -v([0-9]{4})\.jar ]]; then
    VERSION="${BASH_REMATCH[1]}"
else
    echo "No version number found in filename. Switch to $VERSION"
fi

echo "Installing v$VERSION to local Maven repo..."

mvn install:install-file \
  -Dfile="$JAR_FILE" \
  -DgroupId="$GROUP_ID" \
  -DartifactId="$ARTIFACT_ID" \
  -Dversion="$VERSION" \
  -Dpackaging=jar

printf("\nDone")
