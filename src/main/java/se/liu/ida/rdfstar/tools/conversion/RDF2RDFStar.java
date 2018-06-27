package se.liu.ida.rdfstar.tools.conversion;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
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
import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.riot.system.RiotLib;
import org.apache.jena.vocabulary.RDF;

import se.liu.ida.rdfstar.tools.serializer.NodeFormatterTurtleStarExtImpl;

/**
 * 
 * @author Jesper Eriksson
 * @author Amir Hakim
 * @author Ebba Lindström
 * @author Olaf Hartig
 */
public class RDF2RDFStar
{
	protected final static int BUFFER_SIZE = 16000;
	protected final static Node type = RDF.Nodes.type;
	protected final static Node sub = RDF.Nodes.subject;
	protected final static Node pred = RDF.Nodes.predicate;
	protected final static Node state = RDF.Nodes.Statement;
	protected final static Node obj = RDF.Nodes.object;

	final protected HashMap<Node,Node[]> reifieds = new HashMap<Node,Node[]>();

	public void convert( final String inputFilename, OutputStream outStream )
	{
		//First reading of file, to get all reified statements and prefixes
		PipedRDFIterator<Triple> iter = new PipedRDFIterator<>(BUFFER_SIZE);
	    final PipedRDFStream<Triple> inputStream = new PipedTriplesStream(iter);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Runnable parser = new Runnable() {
				  
		            @Override
		            public void run() {
		            	
		            	RDFParser.create()
					       .labelToNode( LabelToNode.createUseLabelEncoded() )
					       .source(inputFilename)
					       //.lang(RDFLanguages.TURTLE)
					       .build()
					       .parse(inputStream);
		            }
		};
		        
		executor.submit(parser);
		
		//Get all reified statements and save in hashmap.
		while (iter.hasNext()) {	
	            Triple next = iter.next();
	        	getReified(next);
	      }
		executor.shutdown();

		IndentedWriter iw = new IndentedWriter(outStream);
		NodeFormatter nttl = new NodeFormatterTurtleStarExtImpl(null,iter.getPrefixes());
		
		//prints out all prefixes
		RiotLib.writePrefixes(iw, iter.getPrefixes());
		RiotLib.writeBase(iw, iter.getBaseIri());
		//second reading of file starts.
		PipedRDFIterator<Triple> iter2 = new PipedRDFIterator<>(BUFFER_SIZE);
	    final PipedRDFStream<Triple> inputStream2 = new PipedTriplesStream(iter2);
		ExecutorService executor2 = Executors.newSingleThreadExecutor();
		Runnable parser2 = new Runnable() {

            @Override
            public void run() {
            	
            	RDFParser.create()
			       .labelToNode( LabelToNode.createUseLabelEncoded() )
			       .source(inputFilename)
			       //.lang(RDFLanguages.TURTLE)
			       .build()
			       .parse(inputStream2);
            }
        };
        
        executor2.submit(parser2);
		
		//Here we print all triples	
        printTriples(iter2.getPrefixes(), iw, nttl, iter2);
        iw.write(" .");
        iw.flush();
		executor2.shutdown();
	}

	//Prints all triples in pretty format, one triple at the time.
	public void printTriples(PrefixMap pm, IndentedWriter iw, NodeFormatter nttl, Iterator<Triple> it)
	{
			Node lastSubject = NodeFactory.createBlankNode();
			Node lastPredicate = NodeFactory.createBlankNode();
			int subPadding = 0;
			int predPadding = 0;
			boolean first_lap = true;
				while(it.hasNext())
				{
		
					Triple currentTriple = it.next();
					if((currentTriple.getPredicate().matches(type) && currentTriple.getObject().matches(state)) ||
						currentTriple.getPredicate().matches(obj) ||
						currentTriple.getPredicate().matches(pred) ||
						currentTriple.getPredicate().matches(sub))
							continue;
					else
					{	
						Node[] temp1 = null;
						if(lastSubject.matches(currentTriple.getSubject()))
						{
							if(lastPredicate.matches(currentTriple.getPredicate()))
							{
								iw.write(" ,\n");
								iw.write(StringUtils.leftPad("", predPadding));
								nttl.format(iw, currentTriple.getObject());
							}
							else
							{
								iw.write(" ;\n");
								iw.write(StringUtils.leftPad("", subPadding));
								nttl.format(iw, currentTriple.getPredicate());				
								iw.write(" ");
								
								if((temp1 = reifieds.get(currentTriple.getObject())) != null)
								{
									temp1 = nestTriple(temp1);
									Node_Triple nt = new Node_Triple(new Triple(temp1[0],temp1[1],temp1[2]));
									nttl.format(iw, nt);
								}
								else
									nttl.format(iw, currentTriple.getObject());
							}
						}
						else
						{		
							if(!first_lap)
								iw.write(" . \n");
							else
								first_lap = false;
							if((temp1 = reifieds.get(currentTriple.getSubject())) != null)
							{
								temp1 = nestTriple(temp1);							
								Node_Triple nt = new Node_Triple(new Triple(temp1[0],temp1[1],temp1[2]));
								nttl.format(iw, nt);
							}
							else
								nttl.format(iw, currentTriple.getSubject());	
							
							iw.write(" ");
							subPadding = iw.getCol();
							
							nttl.format(iw, currentTriple.getPredicate());
							iw.write(" ");
							predPadding = iw.getCol();
							
							if((temp1 = reifieds.get(currentTriple.getObject())) != null)
							{
								temp1 = nestTriple(temp1);
								Node_Triple nt = new Node_Triple(new Triple(temp1[0],temp1[1],temp1[2]));
								nttl.format(iw, nt);
							}
							else
								nttl.format(iw, currentTriple.getObject());						
						}				
					}
					lastPredicate = currentTriple.getPredicate();
					lastSubject = currentTriple.getSubject();
					iw.flush();
				}
		}	
		
		public Node[] nestTriple(Node[] value) {	
			
			Node[] n1 = new Node[3];
			Node[] n2 = new Node[3];
			if((n1 = reifieds.get(value[0])) != null) //go into the subjects triple and check if it also has a subject which exists in hashmap
			{
				Node[] temp = nestTriple(n1);
				Node_Triple nt = new Node_Triple(new Triple(temp[0],temp[1],temp[2]));
				value[0] = nt;
			}
			if((n2 = reifieds.get(value[2])) != null) //same as above but at object place
			{
				Node[] temp = nestTriple(n2);
				Node_Triple nt = new Node_Triple(new Triple(temp[0],temp[1],temp[2]));
				value[2] = nt; 
			}
			return value;
	}
		
		//Check if the triple part of reification, if so, save necessary values into hashmap.
		public void getReified(Triple currentTriple)
		{
				//if rdf statement is found basically, add subj with a empty Node_Triple.
				if((currentTriple.getPredicate().matches(type) && currentTriple.getObject().matches(state))) 
				{
					if(reifieds.get(currentTriple.getSubject()) == null)
					{
						reifieds.put(currentTriple.getSubject(), new Node[3]);
					}
				}
				else if	(currentTriple.getPredicate().matches(obj))	//if rdf object found
				{
					if(reifieds.get(currentTriple.getSubject()) == null) //if this subj for the reified statement doesnt exist in hashmap
					{
						Node [] n = new Node[3];
						n[2] = currentTriple.getObject();
						reifieds.put(currentTriple.getSubject(), n);
					}
					else //subj does exist in hashmap, update HashMap
					{				
						reifieds.get(currentTriple.getSubject())[2] = currentTriple.getObject();
					}
				}
				else if (currentTriple.getPredicate().matches(pred)) //same as above but for predicate
				{
					if(reifieds.get(currentTriple.getSubject()) == null)
					{
						Node [] n = new Node[3];
						n[1] = currentTriple.getObject();
						reifieds.put(currentTriple.getSubject(), n);
					}
					else
					{
						reifieds.get(currentTriple.getSubject())[1] = currentTriple.getObject();
					}
				}
				else if(currentTriple.getPredicate().matches(sub)) //same as above but for subject.
				{
					if(reifieds.get(currentTriple.getSubject()) == null)
					{
						Node [] n = new Node[3];
						n[0] = currentTriple.getObject();
						reifieds.put(currentTriple.getSubject(), n);
					}
					else
					{
						reifieds.get(currentTriple.getSubject())[0] = currentTriple.getObject();
					}	
				}				
		}

}
