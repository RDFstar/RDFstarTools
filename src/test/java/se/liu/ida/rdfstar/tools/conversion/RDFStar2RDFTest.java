package se.liu.ida.rdfstar.tools.conversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFLib;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.DCTerms;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Ebba Lindström
 */

public class RDFStar2RDFTest {
	
	@Before
	public void setup() {
	}

	@After
	public void tearDown() {
	}

	
	@Test
	public void noReification()
	{
		final String filename = "NoReification.ttls";
        final Graph g = convertAndLoadIntoGraph(filename);

        assertEquals( 1, g.size() );

        verifyNoNesting(g);
	}
	
	@Test
	public void nestedSubject()
	{
		final String filename = "nestedSubject.ttls";
        final Graph g = convertAndLoadIntoGraph(filename);

        assertEquals( 5, g.size() );

        verifyNoNesting(g);
	}
	
	@Test
	public void nestedObject()
	{
		final String filename = "nestedObject.ttls";
        final Graph g = convertAndLoadIntoGraph(filename);

        assertEquals( 5, g.size() );

        verifyNoNesting(g);
	}

	@Test
	public void nestedSubjectAndObject()
	{
		final String filename = "nestedSubjectAndObject.ttls";
        final Graph g = convertAndLoadIntoGraph(filename);

        assertEquals( 9, g.size() );

        verifyNoNesting(g);
	}

	@Test
	public void doubleNestedSubject()
	{
		final String filename = "doubleNestedSubject.ttls";
        final Graph g = convertAndLoadIntoGraph(filename);

        assertEquals( 9, g.size() );

        verifyNoNesting(g);
	}

	@Test
	public void doubleNestedObject()
	{
		final String filename = "doubleNestedObject.ttls";
        final Graph g = convertAndLoadIntoGraph(filename);

        assertEquals( 9, g.size() );

        verifyNoNesting(g);
	}


	// ---- helpers ----

	protected Graph convertAndLoadIntoGraph( String filename ) {
		final String fullFilename = getClass().getResource("/TurtleStar/"+filename).getFile();
		final ByteArrayOutputStream os = new ByteArrayOutputStream();

		new RDFStar2RDF().convert(fullFilename, os);
		final String result = os.toString();
		try {
			os.close();
		}
		catch ( IOException e ) {
			fail( "Closing the output stream failed: " + e.getMessage() );
		}

		final StringReader reader = new StringReader(result);
        final Graph g = ModelFactory.createDefaultModel().getGraph();
        final StreamRDF dest = StreamRDFLib.graph(g);

        RDFParser.create()
                 .source(reader)
                 .lang(Lang.TURTLE)
                 .parse(dest);

		return g;
	}
	
	protected void verifyNoNesting(Graph g)
	{		
		final Iterator<Triple> iter = g.find();
		
		while (iter.hasNext()) {
			final Triple t = iter.next();
			assertFalse( t.getSubject() instanceof Node_Triple );
			assertFalse( t.getPredicate() instanceof Node_Triple );
			assertFalse( t.getObject() instanceof Node_Triple );
		}
	}

}
