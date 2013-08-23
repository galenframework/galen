#!/bin/bash

set -e


mvn clean test -Dtestng.excludedGroups=SELENIUM
