#!/bin/bash

set -e

if [ -d dist ]; then
    rm -rf dist/*
fi


modules=(galen-core galen-rainbow4j galen-java-support)

version=$( cat pom.xml | grep "<version>" | head -n 1 | awk -F"[<>]" '/version/{print $3}' | sed "s/-SNAPSHOT//g" )


bin=galen-bin-${version}
src=galen-src-${version}


mkdir -p dist/$bin
mkdir -p dist/$src

echo New dist is $version
echo Assemblying new dist

mvn clean package -DskipTests=true

cp galen-distribution/target/galen-bin.jar dist/$bin/galen.jar
cp -r fordist/* dist/$bin/.
cp .README dist/$bin/README

cp LICENSE-2.0.txt dist/$src/.
cp .README dist/$src/README


for module in "${modules[@]}" 
do
    mkdir dist/$src/$module
    cp -r $module/src dist/$src/$module/src
done



cd dist

zip -r -9 $bin.zip $bin
zip -r -9 $src.zip $src

cd ..
