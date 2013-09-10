#!/bin/bash

set -e

if [ -d dist ]; then
    rm -rf dist/*
else 
    mkdir dist
fi


mvn assembly:assembly
