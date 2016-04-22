#!/bin/bash

set +e

die() {
    echo -e "\e[31m$1\e[0m"
    exit 1
}

verify_changelog_for_version() {
    match=`echo $1 | grep -Ex '##.*\[([0-9]{1,}\.){2}[0-9]{1,}\]\[[0-9]{4}(\-[0-9]{2}){2}\]'`
    if [[ ! -n "$match" ]]; then
        die "Incorrect header in changelog: $1"
    fi
}

while read -r line ; do
    verify_changelog_for_version "$line"
done < <(grep "\\#.*\\[" CHANGELOG.md)


set -e

mvn release:prepare -DskipTests=true -Darguments='-DskipTests=true'

mvn release:perform -DskipTests=true -Darguments='-DskipTests=true'

last_tag=$(git describe --abbrev=0 --tags)

echo "Making dist for tag $last_tag"

git checkout ${last_tag}

./makeDist.sh

version=`echo $last_tag | sed 's/.*-//g'`

echo Singing and deploying galen-parent
mvn gpg:sign-and-deploy-file -Dfile=pom.xml -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/ -DgroupId=com.galenframework -DartifactId=galen-parent -Dversion=${version} -Dpackaging=pom -DrepositoryId=sonatype-nexus-staging

mvn javadoc:aggregate

git checkout master
