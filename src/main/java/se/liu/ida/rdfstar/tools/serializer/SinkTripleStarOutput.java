package se.liu.ida.rdfstar.tools.serializer;

import java.io.OutputStream;

import org.apache.jena.atlas.io.AWriter;
import org.apache.jena.atlas.io.IO;
import org.apache.jena.atlas.lib.Sink;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.out.NodeFormatter;
import org.apache.jena.riot.out.NodeToLabel;
import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.riot.system.PrefixMapFactory;
import org.apache.jena.riot.system.Prologue;

/**
 * A {@link Sink} implementation that can be used for simple NTriples-style
 * writing of RDF* data by using the Turtle* format for nested triples.
 * 
 * To this end, by using a {@link NodeFormatterTurtleStar} internally, the method
 * {@link #send(Triple)} of this class can deal with {@link Triple} objects whose
 * subject or object may be {@link Node_Triple}. 
 * 
 * @author Olaf Hartig http://olafhartig.de/
 */
public class SinkTripleStarOutput implements Sink<Triple>
{
	final protected AWriter writer;
	final protected NodeToLabel labelPolicy;
	final protected NodeFormatter nodeFmt;

	public SinkTripleStarOutput(OutputStream out, Prologue prologue, NodeToLabel labels) {
		this( out,
		      (prologue!=null) ? prologue.getBaseURI()   : null ,
		      (prologue!=null) ? prologue.getPrefixMap() : PrefixMapFactory.emptyPrefixMap(),
		      labels );
    }

	public SinkTripleStarOutput(OutputStream out, String baseIRI, PrefixMap prefixMap, NodeToLabel labels) {
    	writer = IO.wrapUTF8(out);
        labelPolicy = labels;        
        nodeFmt = new NodeFormatterTurtleStarExtImpl(baseIRI, prefixMap, labels);
    }

    @Override
    public void send(Triple triple) {
        Node s = triple.getSubject() ;
        Node p = triple.getPredicate() ;
        Node o = triple.getObject() ;

        nodeFmt.format(writer, s) ;
        writer.print(" ") ;
        nodeFmt.format(writer, p) ;
        writer.print(" ") ;
        nodeFmt.format(writer, o) ;
        writer.print(" .\n") ;
    }

    @Override
    public void flush() {
        IO.flush(writer);
    }

    @Override
    public void close() {
        IO.flush(writer);
    }

}
