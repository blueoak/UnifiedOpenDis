#!/bin/sh
cd "${0%/*}"
cd ../
mvn exec:exec@pdugenerator
