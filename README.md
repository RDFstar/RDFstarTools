# RDFstarTools
This package provides a collection of tools and Java libraries to process RDF* data. Currently, it contains three components: a parser for the Turtle* format, a simple Turtle*-like serializer, and a parser for SPARQL* queries.

**New to RDF\*?** If you want to know what RDF* is about, please refer to our short (4-page) paper ["RDF* and SPARQL*: An Alternative Approach to Annotate Statements in RDF."](http://olafhartig.de/files/Hartig_ISWC2017_RDFStarPosterPaper.pdf)

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

## SPARQL* Parser
The SPARQL* parser is implemented on top of Apache Jena and can also be registered in Jena's query parser framework. The main class of this parser is `ParserSPARQLStar` and there exist two options to use it: First, the simplest option is to call the `parse` method of the class as demonstrated in the following code snippet.
```
String queryString = ...

ParserSPARQLStar parser = new ParserSPARQLStar();
Query query = parser.parse(new Query(), queryString);
```
The second option is to use the parser via Jena's query parser framework. To this end, the parser has to be registered in this framework, which should happen automatically when you use the symbol `ParserSPARQLStar.syntaxSPARQLstar` as demonstrated in the following (if it does not work, try to call `ParserSPARQLStar.init();`).
```
String queryString = ...

Query query = QueryFactory.create(queryString, ParserSPARQLStar.syntaxSPARQLstar);
```
An advantage of the second option (i.e., using the SPARQL* parser via Jena's query parser framework) is that the query can be loaded directly from a file:
```
String filename = ...

Query query = QueryFactory.read("file://" + filename, ParserSPARQLStar.syntaxSPARQLstar);
```
In all cases, the result of parsing is a regular Jena `Query` object. However, the `ElementGroup` that can be obtained by `query.getQueryPattern()` (and that represents the WHERE clause of the query) may contain subelements of type `TriplePath` that, when represented as `Triple` objects, have a `Node_Triple` object as their subject or their object. These `Triple` objects are the result of nested triple patterns in the given SPARQL* query. A minimal example is given as follows.
```
String queryString = "SELECT * WHERE { <<?s ?p ?o>> ?p2 ?o2 }";
Query query = QueryFactory.create(queryString, ParserSPARQLStar.syntaxSPARQLstar);

ElementGroup eg = (ElementGroup) query.getQueryPattern();
ElementPathBlock epb = (ElementPathBlock) eg.get(0);
Triple tp = epb.getPattern().get(0).asTriple();

Node_Triple tp2 = (Node_Triple) tp.getSubject();
```
A second new type of subelements in the `ElementGroup` are `ElementBind` objects whose expression is a `NodeValue` that, when represented as a `Node`, is of type `Node_Triple`. Such `ElementBind` objects are the result of SPARQL*-specific BIND clauses in the given query. A minimal example is given as follows.
```
String queryString = "SELECT * WHERE { BIND( <<?s <p> <o>>> AS ?t) }";
Query query = QueryFactory.create(queryString, ParserSPARQLStar.syntaxSPARQLstar);

ElementGroup eg = (ElementGroup) query.getQueryPattern();
ElementBind eb = (ElementBind) eg.get(0);
NodeValue nv = (NodeValue) eb.getExpr()

Node_Triple tp = (Node_Triple) nv.getNode();
```
