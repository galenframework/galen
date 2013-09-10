#!/bin/bash

set -e

if [ -d dist ]; then
    rm -rf dist/*
fi



mvn assembly:assembly


version=$( git tag | sort -r | head -n 1 )

bin=${version}-bin
src=${version}-src

mkdir -p dist/$bin
mkdir -p dist/$src

echo New dist is $version
cp target/galen-jar-with-dependencies.jar dist/$bin/galen.jar
cp galen dist/$bin/.
cp LICENSE-2.0.txt dist/$bin/.
cp README dist/$bin/.

cp LICENSE-2.0.txt dist/$src/.
cp README dist/$src/.
cp -r src dist/$src/src


cd dist

zip -r -9 $bin.zip $bin
zip -r -9 $src.zip $src
