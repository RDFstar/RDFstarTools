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
import org.apache.jena.vocabulary.DCTerms;
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

	@Test
	public void nestedSubject() {
		
		final String filename = "NestedSubject.ttl";
        final Graph g = convertAndLoadIntoGraph(filename);
        
        assertEquals( 1, g.size() );
        
        final Triple t = g.find().next();
        assertTrue( t.getSubject() instanceof Node_Triple );
        assertFalse( t.getPredicate() instanceof Node_Triple );
        assertFalse( t.getObject() instanceof Node_Triple );

        assertEquals( DCTerms.source.asNode(), t.getPredicate() );

        final Triple et = ( (Node_Triple) t.getSubject() ).get();
        assertEquals( DCTerms.creator.asNode(), et.getPredicate() );
	}
	
	@Test
	public void nestedObject() {
		
		final String filename = "NestedObject.ttl";
        final Graph g = convertAndLoadIntoGraph(filename);
		
        assertEquals( 1, g.size() );
        
        final Triple t = g.find().next();
        assertFalse( t.getSubject() instanceof Node_Triple );
        assertFalse( t.getPredicate() instanceof Node_Triple );
        assertTrue( t.getObject() instanceof Node_Triple );
        
        final Triple et = ( (Node_Triple) t.getObject() ).get();
        assertEquals( DCTerms.creator.asNode(), et.getPredicate() );
        
	}
	
	@Test
	public void nestedSubjectAndObject() {
		
		final String filename = "NestedSubjectAndObject.ttl";
        final Graph g = convertAndLoadIntoGraph(filename);
		
        assertEquals( 1, g.size() );
        
        final Triple t = g.find().next();
        assertTrue( t.getSubject() instanceof Node_Triple );
        assertFalse( t.getPredicate() instanceof Node_Triple );
        assertTrue( t.getObject() instanceof Node_Triple );
        
        Triple et = ( (Node_Triple) t.getSubject() ).get();
        assertEquals( DCTerms.creator.asNode(), et.getPredicate() );
        
        et = ( (Node_Triple) t.getObject() ).get();
        assertEquals( DCTerms.created.asNode(), et.getPredicate() );
	}

	@Test
	public void doubleNestedSubject() {
		
		final String filename = "DoubleNestedSubject.ttl";
        final Graph g = convertAndLoadIntoGraph(filename);
		
        assertEquals( 1, g.size() );
        
        final Triple t = g.find().next();
        assertTrue( t.getSubject() instanceof Node_Triple );
        assertFalse( t.getPredicate() instanceof Node_Triple );
        assertFalse( t.getObject() instanceof Node_Triple );
	
        final Triple et = ( (Node_Triple) t.getSubject() ).get();
        assertFalse( et.getSubject() instanceof Node_Triple );
        assertFalse( et.getPredicate() instanceof Node_Triple );
        assertTrue( et.getObject() instanceof Node_Triple );
        
        final Triple eet = ( (Node_Triple) et.getObject() ).get();
        assertEquals( DCTerms.created.asNode(), eet.getPredicate() );
	}
	
	@Test
	public void doubleNestedObject() {
		
		final String filename = "DoubleNestedObject.ttl";
        final Graph g = convertAndLoadIntoGraph(filename);
		
        assertEquals( 1, g.size() );
        
        final Triple t = g.find().next();
        assertFalse( t.getSubject() instanceof Node_Triple );
        assertFalse( t.getPredicate() instanceof Node_Triple );
        assertTrue( t.getObject() instanceof Node_Triple );
        
        final Triple et = ( (Node_Triple) t.getObject() ).get();
        assertFalse( et.getSubject() instanceof Node_Triple );
        assertFalse( et.getPredicate() instanceof Node_Triple );
        assertTrue( et.getObject() instanceof Node_Triple );
        
        final Triple eet = ( (Node_Triple) et.getObject() ).get();
        assertEquals( DCTerms.created.asNode(), eet.getPredicate() );
	}


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
