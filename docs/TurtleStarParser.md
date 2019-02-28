# Turtle* Parser
The Turtle* parser extends the RIOT parser framework of Apache Jena with support for the Turtle* format. If you have the RDFstarTools JAR in your build path, the parser should automatically recognize Turtle* files by the extension ".ttls". Then, the `Triple` objects produced by the parser may have a `Node` object as their subject or their object that is of the type `Node_Triple`.

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
