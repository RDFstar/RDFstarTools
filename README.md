# RDFstarTools
This package provides a collection of tools and Java libraries to process RDF* data. Currently, it contains two components: a parser for the Turtle* format and a simple Turtle*-like serializer.

## Turtle* Parser
The Turtle* parser extends the RIOT parser framework of Apache Jena with support for the Turtle* format. If you have the RDFstarTools JAR in your build path and you call `LangTurtleStar.init();` before using the parser (e.g., at the begin of your `main` function), the parser should automatically recognize Turtle* files by the extension ".ttls". Then, the `Triple` objects produced by the parser may have a `Node` object as their subject or their object that is of the type `Node_Triple`.

To consume the output of the (extended) parser via a `StreamRDF` object you may use the following source code snippet.
```
String filename = ... // the full file name of the Turtle* file (i.e., absolute path)
StreamRDF dest = ... // initialize your StreamRDF object here

RDFParser.create()
         .source( "file://" + filename )
         .checking(false)
         .parse(dest);
```

For instance, you may simply populate a new `Graph` as follows
```
String filename = ...
Graph g = ModelFactory.createDefaultModel().getGraph();
StreamRDF dest = StreamRDFLib.graph(g);

RDFParser.create()
         .source( "file://" + filename )
         .checking(false)
         .parse(dest);
```

## Simple Turtle* Serializer
The class `SinkTripleStarOutput` can be used to serialize RDF* data. This class implements Jena's `Sink<Triple>` interface in a way that performs simple NTriples-style writing of RDF* data by using the Turtle* format for nested triples. To this end, the method `send(Triple)` of `SinkTripleStarOutput` can deal with `Triple` objects that have a `Node_Triple` object as their subject or their object (internally, this is implemented by using an extension of Jena's `NodeFormatter` that is called `NodeFormatterTurtleStar`).

The following example code demonstrates how the class `SinkTripleStarOutput` can be used to serialize RDF* data.
```
OutputStream outstream = ...
String baseIRI = ...
PrefixMap pmap = ...

// write a Turtle/Turtle* prologue to the output stream
IndentedWriter w = new IndentedWriter(outstream);
RiotLib.writeBase(w, base);
RiotLib.writePrefixes(w, pmap);
w.flush();

SinkTripleStarOutput out = new SinkTripleStarOutput(
	outstream,
	baseIRI,
	pmap,
	NodeToLabel.createScopeByDocument());

// create a triple (which may be nested) and write it to the
// output stream by using the SinkTripleStarOutput object
Triple t = ...
out.send( t );

out.close();
```
