#!/bin/bash

set -e

git fetch --tags

version_promt=$( cat pom.xml | grep "<version>" | head -n 1 | awk -F"[<>]" '/version/{print $3}' | sed "s/-SNAPSHOT//g" )

last_tag=`git tag | sort | tail -n 1`

last_tag_hash=`git rev-list $last_tag | head -n 1`

touch .release-notes

echo -e "Release notes for galen version $version_promt\n\n" > .release-notes

git log --pretty=format:%s $last_tag_hash..HEAD >> .release-notes

vim .release-notes

echo "Done. Release notes are ready in .release-notes"
