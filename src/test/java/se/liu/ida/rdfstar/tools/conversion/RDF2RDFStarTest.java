package se.liu.ida.rdfstar.tools.conversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Triple;
import org.apache.jena.vocabulary.RDF;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.liu.ida.rdfstar.tools.parser.lang.LangTurtleStarTest;

/**
 * 
 * @author Olaf Hartig
 * @author Ebba Lindström
 */
public class RDF2RDFStarTest
{	
	@Before
	public void setup() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void noReification()
	{
		final String filename = "NoReification.ttl";
        final Graph g = convertAndLoadIntoGraph(filename);

        assertEquals( 1, g.size() );

        final Triple t = g.find().next();
        assertFalse( t.getSubject() instanceof Node_Triple );
        assertFalse( t.getPredicate() instanceof Node_Triple );
        assertFalse( t.getObject() instanceof Node_Triple );

        assertEquals( RDF.Nodes.type, t.getPredicate() );
	}

	// TODO (more tests) ...



	// ---- helpers ----

	protected Graph convertAndLoadIntoGraph( String filename ) {
		final String fullFilename = getClass().getResource("/RDF/"+filename).getFile();
		final ByteArrayOutputStream os = new ByteArrayOutputStream();

		new RDF2RDFStar().convert(fullFilename, os);
		final String result = os.toString();

		try {
			os.close();
		}
		catch ( IOException e ) {
			fail( "Closing the output stream failed: " + e.getMessage() );
		}

		return LangTurtleStarTest.createGraphFromTurtleStarSnippet(result);
	}

}
