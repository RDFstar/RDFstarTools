package se.liu.ida.rdfstar.tools.conversion;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.lang.LabelToNode;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedTriplesStream;
import org.apache.jena.riot.out.NodeFormatter;
import org.apache.jena.riot.out.NodeFormatterTTL;
import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.riot.system.RiotLib;
import org.apache.jena.vocabulary.RDF;

import se.liu.ida.rdfstar.tools.parser.lang.LangTurtleStar;

/**
 * Conversion of Turtle* files into Turtle such that the resulting
 * RDF data is a reification-based representation of the RDF* data.
 * 
 * @author Jesper Eriksson
 * @author Amir Hakim
 * @author Ebba Lindström
 * @author Olaf Hartigs
 */
public class RDFStar2RDF
{
	protected final static int BUFFER_SIZE = 16000;

	public void convert( String inputFilename, OutputStream outStream )
	{
		final FirstPass fp = new FirstPass(inputFilename, outStream);
		fp.execute();

		final FileConverter fc = new FileConverter(inputFilename,
		                                           outStream,
		                                           fp.getBaseIRI(),
		                                           fp.getPrefixMap());
		fc.execute();
	}

	protected class FileConverter
	{
		final protected String inputFilename;
		final protected OutputStream outStream;
		final protected String baseIRI;
		final protected PrefixMap pmap;

		// populated during the conversion to record for every reified
		// triple, the blank node that has been created as statement ID
		protected final Map<Triple,Node> bNodes = new HashMap<Triple,Node>();

		protected boolean first_lap = true;
		protected Node lastSubject = NodeFactory.createBlankNode(); //used to create turtle blocks
		protected Node lastPredicate = NodeFactory.createBlankNode(); //used to create turtle blocks

		public FileConverter( String inputFilename, OutputStream outStream, String baseIRI, PrefixMap pm ) {
			this.inputFilename = inputFilename;
			this.outStream = outStream;
			this.baseIRI = baseIRI;
			this.pmap = pm;
		}

		public void execute()
		{
			// print all prefixes and the base IRI to the output file
			final IndentedWriter writer = new IndentedWriter(outStream);
			RiotLib.writePrefixes(writer, pmap);
			RiotLib.writeBase(writer, baseIRI);

			// second pass over the file to perform the conversion in a streaming manner
			final PipedRDFIterator<Triple> it = new PipedRDFIterator<>(BUFFER_SIZE);

			RDFParser.create().labelToNode( LabelToNode.createUseLabelEncoded() )				  
			                  .source(inputFilename)
			                  .checking(false)
			                  .lang(LangTurtleStar.TURTLESTAR)
			                  .build()
			                  .parse( new PipedTriplesStream(it) );

			final NodeFormatter nFmt = new NodeFormatterTTL(baseIRI, pmap);

			while ( it.hasNext() ) {
				printTriples(it.next(), writer, nFmt, false);	
			}

			it.close();

			writer.write(" .");
			writer.flush();
			writer.close();
		}

		/**
		 * Recursively flattens nested triples and prints in pretty format. One triple at the time.
		 */
		protected Node printTriples(Triple triple, IndentedWriter writer, NodeFormatter nFmt, boolean hasParent)
		{
			Node s = triple.getSubject();
			Node p = triple.getPredicate();
	        Node o = triple.getObject();

	        if ( s instanceof Node_Triple )
			{
	        	final Triple subjTriple = ( (Node_Triple) s ).get();
	        	s = printTriples(subjTriple, writer, nFmt, true);
	        	first_lap = false;
			}

	        if ( o instanceof Node_Triple )
	        {
	        	final Triple objTriple = ( (Node_Triple) o ).get();
	        	o = printTriples(objTriple, writer, nFmt, true);
	        	first_lap = false;
	        }

	        //if we have reached here, we are at the deepest level of recursion, or if its not nested at all
	        if ( hasParent )
	        {
	        	final Triple hashKey = Triple.create(s, p, o);
	        	Node bnode; 
	        	if ( (bnode = bNodes.get(hashKey)) != null )
	        	{
	        		return bnode;
	        	}
	        	else
	        	{
	        		bnode = NodeFactory.createBlankNode();
	            	bNodes.put(hashKey, bnode);

	            	if( ! first_lap )
	            		writer.write(" .\n");

	            	nFmt.format(writer, bnode);
	            	writer.write(" ");
	            	nFmt.format(writer, RDF.Nodes.type);
	            	writer.write(" ");
	            	nFmt.format(writer, RDF.Nodes.Statement);
	            	writer.write(" ;\n" + StringUtils.leftPad("",bnode.toString().length()+4));
	            	nFmt.format(writer, RDF.Nodes.subject);
	            	writer.write(" ");
	            	nFmt.format(writer, s);
	            	writer.write(" ;\n" + StringUtils.leftPad("",bnode.toString().length()+4));
	            	nFmt.format(writer, RDF.Nodes.predicate);
	            	writer.write(" ");
	            	nFmt.format(writer, p);
	            	writer.write(" ;\n" + StringUtils.leftPad("",bnode.toString().length()+4));
	            	nFmt.format(writer, RDF.Nodes.object);
	            	writer.write(" ");
	            	nFmt.format(writer, o);

	            	lastSubject = bnode;
	        		lastPredicate = NodeFactory.createBlankNode();
	            	return bnode;
	        	}
	        }
	        else // Enter here if not nested or top level of recursion
	        {
	        	if ( lastSubject.matches(s) )
				{
	        		int pad = 0;

	        		if ( lastSubject.isBlank() )
	        			pad = 3;

					if (lastPredicate.matches(p) )
					{
						writer.write(" ,\n" + StringUtils.leftPad("", s.toString().length()+p.toString().length()+2+pad));
						nFmt.format(writer, o);
					}
					else
					{
						writer.write(" ;\n" + StringUtils.leftPad("", s.toString().length()+1+pad));
						nFmt.format(writer, p);
						writer.write(" ");
						nFmt.format(writer, o);
					}
				}
	        	else
	        	{
	        		if ( lastSubject.matches(o) || ! first_lap )
	        			writer.write(" .\n");
	        		else
	        			first_lap = false;

	        		nFmt.format(writer, s);
	        		writer.write(" ");
	        		nFmt.format(writer, p);
	        		writer.write(" ");
	        		nFmt.format(writer, o);
	        	}
	        }

	        writer.flush();

	        lastSubject = s;
			lastPredicate = p;
	        first_lap = false;

	        return null; // return to the execute method
		}

	} // end of class FileConverter


	/**
	 * Performs a first pass over the input file to collect all prefixes.
	 */
	protected class FirstPass
	{
		final protected String inputFilename;
		final protected OutputStream outStream;

		protected PrefixMap pmap;
		protected String baseIRI;

		public FirstPass( String inputFilename, OutputStream outStream ) {
			this.inputFilename = inputFilename;
			this.outStream = outStream;
		}

		public PrefixMap getPrefixMap() { return pmap; }
		public String getBaseIRI() { return baseIRI; }

		public void execute()
		{
			final PipedRDFIterator<Triple> it = new PipedRDFIterator<Triple>(BUFFER_SIZE);

			RDFParser.create().labelToNode( LabelToNode.createUseLabelEncoded() )
			                  .source(inputFilename)
						      .checking(false)
						      .lang(LangTurtleStar.TURTLESTAR)
			                  .build()
			                  .parse( new PipedTriplesStream(it) );

			// consume the iterator
			while ( it.hasNext() ) {
	        	it.next();
	        }

			pmap = it.getPrefixes();
			baseIRI = it.getBaseIri();

			it.close();
		}

	} // end of class FirstPass

}
