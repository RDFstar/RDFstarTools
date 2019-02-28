# RDFstarTools
This package provides a collection of command line tools and Java libraries to process RDF* data and SPARQL* queries.

**New to RDF\*?** If you want to know what RDF* is about, please refer to our short [position statement: "The RDF* and SPARQL* Approach to Annotate Statements in RDF and to Reconcile RDF and Property Graphs."](http://blog.liu.se/olafhartig/2019/01/10/position-statement-rdf-star-and-sparql-star/)

## Tools
The command line tools available in this collection are:
* a tool to convert RDF data into RDF* data,
* a tool to convert RDF* data into RDF data,
* a tool to rewrite SPARQL* queries into SPARQL queries,
* a tool to execute SPARQL* queries over RDF* data, and
* a tool to execute SPARQL* queries over RDF reification data in a SPARQL endpoint.

## Java libraries
The Java source code of the aforementioned tools is an RDF*/SPARQL*-aware extension of the Apache Jena framework for building RDF-based applications. Hence, the Java classes in this repository may also be used directly for building RDF*-based and/or SPARQL*-based applications. In particular, the source code contains contains the following components:
* [a parser for the Turtle* format](https://github.com/RDFstar/RDFstarTools/blob/master/docs/SPARQLStarParser.md),
* [a simple Turtle*-like serializer](https://github.com/RDFstar/RDFstarTools/blob/master/docs/SimpleTurtleStarSerializer.md), and
* [a parser for SPARQL* queries](https://github.com/RDFstar/RDFstarTools/blob/master/docs/TurtleStarParser.md).
