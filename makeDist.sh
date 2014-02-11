#!/bin/bash

set -e

if [ -d dist ]; then
    rm -rf dist/*
fi


version=$( cat pom.xml | grep "<version>" | head -n 1 | awk -F"[<>]" '/version/{print $3}' | sed "s/-SNAPSHOT//g" )


bin=galen-bin-${version}
src=galen-src-${version}


mkdir -p dist/$bin
mkdir -p dist/$src

echo New dist is $version
echo Assemblying new dist

mvn assembly:assembly -DskipTests=true

cp target/galen-jar-with-dependencies.jar dist/$bin/galen.jar
cp galen dist/$bin/.
cp galen.bat dist/$bin/.
cp LICENSE-2.0.txt dist/$bin/.
cp .README dist/$bin/README

cp LICENSE-2.0.txt dist/$src/.
cp .README dist/$src/README
cp -r src dist/$src/src


cd dist

zip -r -9 $bin.zip $bin
zip -r -9 $src.zip $src
