# UnifiedOpenDis
Creating a single composite project that combines all language implementations and is composed of prior projects.

* mvn clean
* mvn compile
* mvn exec:exec@enumgenerator
* mvn compile
* mvn exec:exec@pdugenerator
* mvn compile
* mvn exec:exec@entitygenerator
* mvn exec:exec@jammergenerator

