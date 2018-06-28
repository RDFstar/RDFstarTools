package se.liu.ida.rdfstar.tools.conversion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.lang.LabelToNode;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;
import org.apache.jena.riot.out.NodeFormatter;
import org.apache.jena.riot.out.NodeFormatterTTL;
import org.apache.jena.riot.system.RiotLib;
import org.apache.jena.vocabulary.RDF;

import se.liu.ida.rdfstar.tools.parser.lang.LangTurtleStar;

/**
 * 
 * @author Jesper Eriksson
 * @author Amir Hakim
 * @author Ebba Lindström
 */

public class RDFStar2RDF {

	public static HashMap<Triple,Node> bNodes = new HashMap<Triple,Node>();
	protected static Node lastSubject = NodeFactory.createBlankNode(); //used to create turtle blocks
	protected static Node lastPredicate = NodeFactory.createBlankNode(); //used to create turtle blocks
	protected static boolean first_lap = true;
	protected static int BUFFER_SIZE = 16000; //Recommended size is 10% of the file size
	
	public void convert( final String inputFilename, OutputStream outStream ) {
		
		LangTurtleStar.init();
		
		//Creates iterator to read triples in a streaming fashion.
		//First lap is only to save all prefixes.
		PipedRDFIterator<Triple> iter = new PipedRDFIterator<>(BUFFER_SIZE);
	    final PipedRDFStream<Triple> inputStream = new PipedTriplesStream(iter);
		RDFParser.create().labelToNode( LabelToNode.createUseLabelEncoded() )				  
					      .source(inputFilename)
					      .checking(false)
					      .lang(LangTurtleStar.TURTLESTAR)
					      .build()
					      .parse(inputStream);
		
		while (iter.hasNext()) {
			iter.next();
			
	     }
		IndentedWriter iw = new IndentedWriter(outStream);
		NodeFormatter nttl = new NodeFormatterTTL(null,iter.getPrefixes());
		RiotLib.writePrefixes(iw, iter.getPrefixes());
		RiotLib.writeBase(iw, iter.getBaseIri());

		//Creates iterator to read triples in a streaming fashion.
		PipedRDFIterator<Triple> iter2 = new PipedRDFIterator<>(BUFFER_SIZE);
	    final PipedRDFStream<Triple> inputStream2 = new PipedTriplesStream(iter2);
		ExecutorService executor2 = Executors.newSingleThreadExecutor();
		
		Runnable parser2 = new Runnable() {
			  
	            @Override
	            public void run() {
	            	
	            	RDFParser.create()
				       .labelToNode( LabelToNode.createUseLabelEncoded() )				  
				       .source(inputFilename)
				       .checking(false)
				       .build()
				       .parse(inputStream2);
	            }
	        };
	    executor2.submit(parser2);
		//Send one triple at the time to print function to print into Turtle format.
		while (iter2.hasNext()) {	
            Triple next = iter2.next();
            
        	printTriples(next, iw, nttl, false);	
      }
		iw.write(" .");
		iw.flush();
		executor2.shutdown();
	}
	
	//Recursive function to unnest triples and print in pretty format. One triple at the time.
		public static Node printTriples(Triple triple, IndentedWriter iw, NodeFormatter nttl, boolean hasParent)
		{
			Node s = triple.getSubject();
			Node p = triple.getPredicate();
	        Node o = triple.getObject();
	        Node finished = NodeFactory.createLiteral("finished");
			
	        if ( s instanceof Node_Triple )
			{
	        	Triple subTriple = ((Node_Triple)s).get();
	        	s = printTriples(subTriple, iw, nttl, true);
			}
	       
	        if( o instanceof Node_Triple)
	        {
	        	Triple objTriple = ((Node_Triple)o).get();
	        	o = printTriples(objTriple, iw, nttl, true);
	        }
	        
	        
	        //if we have reached here, we are at the deepest level of recursion, or if its not nested at all
	        if(hasParent)
	        {
	        	Triple hashKey = Triple.create(s, p, o);
	        	Node bnode; 
	        	if((bnode = bNodes.get(hashKey)) != null)
	        	{
	        		return bnode;
	        	}
	        	else
	        	{
	        		bnode = NodeFactory.createBlankNode();
	            	bNodes.put(hashKey, bnode);
	            	if(!first_lap)
	            		iw.write(" .\n");
	            	
	            	Node type = RDF.Nodes.type;
	            	Node sub = RDF.Nodes.subject;
	            	Node pred = RDF.Nodes.predicate;
	            	Node state = RDF.Nodes.Statement;
	            	Node obj = RDF.Nodes.object;
	            	nttl.format(iw,bnode);
	            	iw.write(" ");
	            	nttl.format(iw, type);
	            	iw.write(" ");
	            	nttl.format(iw, state);
	            	iw.write(" ;\n" + StringUtils.leftPad("",bnode.toString().length()+4));
	            	nttl.format(iw, sub);
	            	iw.write(" ");
	            	nttl.format(iw,s);
	            	iw.write(" ;\n" + StringUtils.leftPad("",bnode.toString().length()+4));
	            	nttl.format(iw, pred);
	            	iw.write(" ");
	            	nttl.format(iw, p);
	            	iw.write(" ;\n" + StringUtils.leftPad("",bnode.toString().length()+4));
	            	nttl.format(iw, obj);
	            	iw.write(" ");
	            	nttl.format(iw, o);
	            	lastSubject = bnode;
	        		lastPredicate = NodeFactory.createBlankNode();
	            	return bnode;
	        	}
	        }
	        else //Enter here if not nested or top level of recursion
	        {
	        	if(lastSubject.matches(s))
				{
	        		int pad = 0;
	        		if(lastSubject.isBlank())
	        			pad = 3;
	        		
					if(lastPredicate.matches(p))
					{
						iw.write(" ,\n" + StringUtils.leftPad("", s.toString().length()+p.toString().length()+2+pad));
						nttl.format(iw, o);
					}
					else
					{
						iw.write(" ;\n" + StringUtils.leftPad("", s.toString().length()+1+pad));
						nttl.format(iw, p);
						iw.write(" ");
						nttl.format(iw, o);
					}
				}
	        	else
	        	{
	        		if(!first_lap)
	        			iw.write(" .\n");
	        		else
	        			first_lap = false;
	        	nttl.format(iw, s);
	        	iw.write(" ");
	        	nttl.format(iw, p);
	        	iw.write(" ");
	        	nttl.format(iw, o);
	        	}   
	        }
	        iw.flush();
	        lastSubject = s;
			lastPredicate = p;
	        first_lap=false;
	        return finished;
	        
		}
	
}
