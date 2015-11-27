#!/bin/bash

set +e

die() {
    echo -e "\e[31m$1\e[0m"
    exit 1
}

verify_version_data() {
    match=`echo $1 | grep -Ex '##.*\[([0-9]{1,}\.){2}[0-9]{1,}\]\[[0-9]{4}(\-[0-9]{2}){2}\]'`
    if [[ ! -n "$match" ]]; then
        die "Incorrect header in changelog: $1"
    fi
}

while read -r line ; do
    verify_version_data "$line"
done < <(grep "\\#.*\\[" CHANGELOG.md)


set -e

mvn release:prepare -DskipTests=true -Darguments='-DskipTests=true'

mvn release:perform -DskipTests=true -Darguments='-DskipTests=true'

version=$(git describe --abbrev=0 --tags)

git checkout ${version}

./makeDist.sh

git checkout master
