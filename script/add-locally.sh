#!/usr/bin/env bash

## Add a jar file to the local Maven repository
# Example: ./add-locally.sh com.outdatedversion stupid-thing 1.0 thing.jar

mvn install:install-file \
   -DgroupId=$1 \
   -DartifactId=$2 \
   -Dpackaging=jar \
   -Dversion=$3 \
   -Dfile=$4 \
   -DgeneratePom=true