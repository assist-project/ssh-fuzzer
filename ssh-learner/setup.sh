#!/bin/bash
# this script installs the .jars to the local maven repo
LIB_DIR=./lib
mvn install:install-file -Dfile=$LIB_DIR/learnlib-distribution-20110714.jar -DgroupId=de.ls5 -DartifactId=jlearn -Dversion=0.1 -Dpackaging=jar -DgeneratePom=true
