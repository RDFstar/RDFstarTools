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
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.lang.LabelToNode;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedTriplesStream;
import org.apache.jena.riot.out.NodeFormatter;
import org.apache.jena.riot.system.ErrorHandler;
import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.riot.system.RiotLib;
import org.apache.jena.vocabulary.RDF;

import se.liu.ida.rdfstar.tools.serializer.NodeFormatterTurtleStarExtImpl;

/**
 * Conversion of any type of RDF files into Turtle* files.
 * The implemented algorithm assumes that any statement-level metadata
 * in the given RDF data is represented using standard RDF reification. 
 * 
 * @author Jesper Eriksson
 * @author Amir Hakim
 * @author Ebba Lindström
 * @author Olaf Hartig
 * @author Robin Keskisärkkä
 */
public class RDF2RDFStar
{
	protected final static int BUFFER_SIZE = 16000;
	protected final static boolean DEFAULT_VALUE_enableChecking = false;

	public void convert( String inputFilename, OutputStream outStream)
	{
		convert( inputFilename, outStream, null, null, null, DEFAULT_VALUE_enableChecking );
	}

	public void convert( String inputFilename, OutputStream outStream, Lang inputLang )
	{
		convert( inputFilename, outStream, inputLang, null, null, DEFAULT_VALUE_enableChecking );
	}

	public void convert( String inputFilename, OutputStream outStream, String baseIRI )
	{
		convert( inputFilename, outStream, null, baseIRI, null, DEFAULT_VALUE_enableChecking );
	}

	public void convert( String inputFilename,
			             OutputStream outStream,
                         Lang inputLang,
			             String baseIRI,
			             ErrorHandler errHandler,
			             boolean enableChecking )
	{
		final FirstPass fp = new FirstPass(inputFilename, inputLang, baseIRI, errHandler, enableChecking);
		fp.execute();

		// print all prefixes and the base IRI to the output file
		final IndentedWriter writer = new IndentedWriter(outStream);
		RiotLib.writePrefixes(writer, fp.getPrefixMap());
		RiotLib.writeBase(writer, fp.getBaseIRI());

		// second pass over the file to perform the conversion in a streaming manner
		// (PipedTriplesStream and PipedRDFIterator need to be on different threads!!)
		final PipedRDFIterator<Triple> it = new PipedRDFIterator<>(BUFFER_SIZE);
		final PipedTriplesStream triplesStream = new PipedTriplesStream(it);

		final Parser p = new Parser(inputFilename, triplesStream, enableChecking);

		if ( baseIRI != null )
			p.setBaseIRI(baseIRI);

		if ( inputLang != null )
			p.setLang(inputLang);

		if ( errHandler != null )
			p.setErrorHandler(errHandler);

		final ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(p);

		final NodeFormatter nFmt = new NodeFormatterTurtleStarExtImpl(fp.getBaseIRI(), fp.getPrefixMap());
		printTriples(writer, nFmt, it, fp.getReifiedTriples());

		it.close();
		executor.shutdown();

		writer.write(" .");
		writer.flush();
		writer.close();
	}

	/**
	 * Prints all triples in pretty format, one triple at the time.
	 */
	public void printTriples(IndentedWriter writer,
	                         NodeFormatter nFmt,
	                         Iterator<Triple> it,
	                         ReifiedTriples reified)
	{
		Node lastSubject = NodeFactory.createBlankNode();
		Node lastPredicate = NodeFactory.createBlankNode();
		int subPadding = 0;
		int predPadding = 0;
		boolean first_lap = true;

		while( it.hasNext() )
		{
			final Triple t = it.next();
			final Node curS = t.getSubject();
			final Node curP = t.getPredicate();
			final Node curO = t.getObject();

			// ignore reification-related triples 
			if ( (curP.matches(RDF.Nodes.type) && curO.matches(RDF.Nodes.Statement))
			     || curP.matches(RDF.Nodes.subject)
			     || curP.matches(RDF.Nodes.predicate)
			     || curP.matches(RDF.Nodes.object)  )
				continue;

			Node[] temp1 = null;
			if ( lastSubject.matches(curS) )
			{
				if ( lastPredicate.matches(curP) )
				{
					writer.write(" ,\n");
					writer.write(StringUtils.leftPad("", predPadding));
					nFmt.format(writer, curO);
				}
				else
				{
					writer.write(" ;\n");
					writer.write(StringUtils.leftPad("", subPadding));
					nFmt.format(writer, curP);
					writer.write(" ");

					if ( (temp1 = reified.get(curO)) != null ) {
						temp1 = nestTriple(temp1, reified);
						final Node_Triple nt = new Node_Triple( new Triple(temp1[0],temp1[1],temp1[2]) );
						nFmt.format(writer, nt);
					}
					else
						nFmt.format(writer, t.getObject());
				}
			}
			else
			{
				if ( ! first_lap )
					writer.write(" . \n");
				else
					first_lap = false;

				if ( (temp1 = reified.get(curS)) != null ) {
					temp1 = nestTriple(temp1, reified);
					final Node_Triple nt = new Node_Triple( new Triple(temp1[0],temp1[1],temp1[2]) );
					nFmt.format(writer, nt);
				}
				else
					nFmt.format(writer, t.getSubject());

				writer.write(" ");
				subPadding = writer.getCol();

				nFmt.format(writer, t.getPredicate());
				writer.write(" ");
				predPadding = writer.getCol();

				if ( (temp1 = reified.get(t.getObject())) != null ) {
					temp1 = nestTriple(temp1, reified);
					final Node_Triple nt = new Node_Triple( new Triple(temp1[0],temp1[1],temp1[2]) );
					nFmt.format(writer, nt);
				}
				else
					nFmt.format(writer, t.getObject());
			}

			lastPredicate = curP;
			lastSubject = curS;
			writer.flush();
		}
	}	
		
	public Node[] nestTriple(Node[] triple, ReifiedTriples reified)
	{
		// If the subject of the given triple identifies another reified triple,
		// replace the subject in the given triple by the reified triple itself. 
		final Node[] reifiedTriple1 = reified.get(triple[0]);
		if ( reifiedTriple1 != null )
		{
			final Node[] reifiedTripleNested = nestTriple(reifiedTriple1, reified);
			triple[0] = new Node_Triple( new Triple(reifiedTripleNested[0],
			                                        reifiedTripleNested[1],
			                                        reifiedTripleNested[2]) );
		}

		// If the object of the given triple identifies another reified triple,
		// replace the object in the given triple by the reified triple itself. 
		final Node[] reifiedTriple2 = reified.get(triple[2]);
		if ( reifiedTriple2 != null )
		{
			final Node[] reifiedTripleNested = nestTriple(reifiedTriple2, reified);
			triple[2] = new Node_Triple( new Triple(reifiedTripleNested[0],
			                                        reifiedTripleNested[1],
			                                        reifiedTripleNested[2]) );
		}

		return triple;
	}


	protected interface ReifiedTriples
	{
		public boolean contains(Node id);
		public Node[] get(Node id);	
	}


	/**
	 * Performs a first pass over the input file to collect all reification
	 * statements and all prefixes
	 */
	protected class FirstPass
	{
		final protected PipedRDFIterator<Triple> it = new PipedRDFIterator<>(BUFFER_SIZE);
		final protected Parser parser;

		// using 3-element arrays to avoid having to create multiple Java objects for each reification statement found 
		final protected HashMap<Node,Node[]> reificationStmts = new HashMap<Node,Node[]>();
		protected PrefixMap pmap;
		protected String baseIRI;

		protected ReifiedTriples rt;

		public FirstPass( String inputFilename ) { this(inputFilename, null, null, null, DEFAULT_VALUE_enableChecking); }

		public FirstPass( String inputFilename, Lang inputLang, String baseIRI, ErrorHandler errHandler, boolean enableChecking )
		{
			parser = new Parser( inputFilename, new PipedTriplesStream(it), enableChecking );

			if ( inputLang != null )
				parser.setLang(inputLang);

			if ( baseIRI != null ) {
				this.baseIRI = baseIRI;
				parser.setBaseIRI(baseIRI);
			}

			if ( errHandler != null )
				parser.setErrorHandler(errHandler);
		}

		public PrefixMap getPrefixMap() { return pmap; }
		public String getBaseIRI() { return baseIRI; }

		public ReifiedTriples getReifiedTriples()
		{
			if ( rt == null ) {
				rt = new ReifiedTriples() {
					public boolean contains(Node id) { return reificationStmts.containsKey(id); }
					public Node[] get(Node id) { return reificationStmts.get(id); }
				};
			}

			return rt;
		}

		public void execute()
		{
			// PipedTriplesStream and PipedRDFIterator need to be on different threads
			final ExecutorService executor = Executors.newSingleThreadExecutor();
			executor.submit(parser);

			// Record all reification statements in the hashmap
			while (it.hasNext()) {
	        	recordIfReificationStmt(it.next());
			}

			pmap = it.getPrefixes();
			if(baseIRI == null) {
				baseIRI = it.getBaseIri();
			}

			it.close();
			executor.shutdown();
		}

		/**
		 * If the given triple is part of a reification statement,
		 * record it in the corresponding part of the hashmap.
		 */
		protected void recordIfReificationStmt(Triple t)
		{
			final Node s = t.getSubject();
			final Node p = t.getPredicate();
			final Node o = t.getObject();

			if ( (p.matches(RDF.Nodes.type) && o.matches(RDF.Nodes.Statement)) )
			{
				if ( ! reificationStmts.containsKey(s) )
					reificationStmts.put( s, new Node[3] );
			}
			else if	( p.matches(RDF.Nodes.object) )
			{
				if ( reificationStmts.containsKey(s) )
					reificationStmts.get(s)[2] = o;
				else {
					final Node[] n = new Node[3];
					n[2] = o;
					reificationStmts.put(s, n);
				}
			}
			else if ( p.matches(RDF.Nodes.predicate) )
			{
				if( reificationStmts.containsKey(s) )
					reificationStmts.get(s)[1] = o;
				else
				{
					final Node[] n = new Node[3];
					n[1] = o;
					reificationStmts.put(s, n);
				}	
			}
			else if ( p.matches(RDF.Nodes.subject) )
			{
				if( reificationStmts.containsKey(s) )
					reificationStmts.get(s)[0] = o;
				else
				{
					final Node []n = new Node[3];
					n[0] = o;
					reificationStmts.put(s, n);
				}
			}				
		}

	} // end of class FirstPass


	/**
	 * Performs a first pass over the input file to collect all reification
	 * statements and all prefixes
	 */
	protected class Parser implements Runnable
	{
		final String inputFilename;
		final PipedTriplesStream triplesStream;
		final RDFParserBuilder builder;

		public Parser( String inputFilename, PipedTriplesStream triplesStream, boolean enableChecking )
		{
			this.inputFilename = inputFilename;
			this.triplesStream = triplesStream;

			builder = RDFParser.create();
			builder.labelToNode( LabelToNode.createUseLabelEncoded() );
			builder.source(inputFilename);
			builder.checking(enableChecking);
		}

		public void setLang( Lang lang ) { builder.lang(lang); }

		public void setBaseIRI( String baseIRI ) { builder.base(baseIRI); }

		public void setErrorHandler( ErrorHandler handler ) { builder.errorHandler(handler); }

		@Override
		public void run() { builder.build().parse(triplesStream); }

	} // end of class Parser

}
