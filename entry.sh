#!/bin/bash

cd /mnt
mvn package
echo
echo CONVERTING THIS TURTLE STAR FILE:
cat src/test/resources/TurtleStar/doubleNestedObject.ttls
echo
echo TO TURTLE:
echo
java -cp target/RDFstarTools-0.0.1-SNAPSHOT.jar se.liu.ida.rdfstar.tools.ConverterRDFStar2RDF -v   src/test/resources/TurtleStar/doubleNestedObject.ttls
