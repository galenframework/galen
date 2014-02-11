#!/bin/bash

set -e


mvn release:prepare -DskipTests=true -Darguments='-DskipTests=true'

mvn release:perform -DskipTests=true -Darguments='-DskipTests=true'

version=$(git describe --abbrev=0 --tags)

git checkout ${version}

./makeDist.sh

git checkout master
