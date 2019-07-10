# RDFstarTools
This package provides a collection of command line tools and Java libraries to process RDF* data and SPARQL* queries.

**New to RDF\* and SPARQL\*?** If you want to know what RDF* and SPARQL* are about, please refer to our short [position statement: "The RDF* and SPARQL* Approach to Annotate Statements in RDF and to Reconcile RDF and Property Graphs."](http://blog.liu.se/olafhartig/2019/01/10/position-statement-rdf-star-and-sparql-star/)

## Tools
The command line tools available in this collection are:
* [a tool to convert RDF data into RDF* data](https://github.com/RDFstar/RDFstarTools/blob/master/docs/ConverterRDF2RDFStar.md),
* [a tool to convert RDF* data into RDF data](https://github.com/RDFstar/RDFstarTools/blob/master/docs/ConverterRDFStar2RDF.md),
* [a tool to rewrite SPARQL* queries into SPARQL queries](https://github.com/RDFstar/RDFstarTools/blob/master/docs/ConverterSPARQLStar2SPARQL.md),
* [a tool to execute SPARQL* queries over RDF* data](https://github.com/RDFstar/RDFstarTools/blob/master/docs/ExecuteSPARQLStar.md), and
* [a tool to execute SPARQL* queries over RDF reification data in a SPARQL endpoint](https://github.com/RDFstar/RDFstarTools/blob/master/docs/ExecuteSPARQLStarRemotely.md).

## Java libraries
The core components of the Java source code of the aforementioned tools are RDF*/SPARQL*-aware extensions for the Apache Jena framework for building RDF-based applications. Hence, the source code in this repository may also be used as a Java library for building RDF*-based and/or SPARQL*-based applications. In particular, the source code contains the following components:
* [a parser for the Turtle* format](https://github.com/RDFstar/RDFstarTools/blob/master/docs/TurtleStarParser.md),
* [a simple Turtle*-like serializer](https://github.com/RDFstar/RDFstarTools/blob/master/docs/SimpleTurtleStarSerializer.md), and
* [a parser for SPARQL* queries](https://github.com/RDFstar/RDFstarTools/blob/master/docs/SPARQLStarParser.md).

## Property Graphs?
For a collection of command line tools and Java libraries that connect the RDF* data model and Property Graphs refer to our [RDFstarPGConnectionTools](https://github.com/RDFstar/RDFstarPGConnectionTools).
