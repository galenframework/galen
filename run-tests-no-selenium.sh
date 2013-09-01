#!/bin/bash

set -e


mvn clean test -Dtestng.excluded=SELENIUM
