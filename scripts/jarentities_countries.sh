#!/bin/sh
cd "${0%/*}"
cd ../
jar cf jars/entities_countries.jar @scripts/countries_classes.list
jar cfv jars/entities_countries_src.jar @scripts/countries_src.list
