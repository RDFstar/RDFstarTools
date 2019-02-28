# Simple Turtle* Serializer
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
