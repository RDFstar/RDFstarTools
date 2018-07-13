package se.liu.ida.rdfstar.tools.conversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

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

import jena.turtle;
import se.liu.ida.rdfstar.tools.parser.lang.LangTurtleStar;
import se.liu.ida.rdfstar.tools.parser.lang.LangTurtleStarTest;

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
	
	//this one works
	@Test
	public void noReification()
	{
		final String filename = "NoReification.ttls";
        final Graph g = convertAndLoadIntoGraph(filename);

        assertEquals( 1, g.size() );
        
        //this should be used for all tests, only Turtle* can contain triples in subject or object, not wanted RDF-data
        final Triple t = g.find().next();
        assertFalse( t.getSubject() instanceof Node_Triple );
        assertFalse( t.getPredicate() instanceof Node_Triple );
        assertFalse( t.getObject() instanceof Node_Triple );
        
        assertEquals( RDF.Nodes.type, t.getPredicate() );
	}
	
	//this one works
	@Test
	public void nestedSubject() {
		final String filename = "nestedSubject.ttls";
        final Graph g = convertAndLoadIntoGraph(filename);
        
        assertEquals( 5, g.size() );
        
        final Triple t = g.find().next();
        assertFalse( t.getSubject() instanceof Node_Triple );
        assertFalse( t.getPredicate() instanceof Node_Triple );
        assertFalse( t.getObject() instanceof Node_Triple );
		
	}
	
	//this one does not work! "Triples not terminated by DOT"
	//will work if other code before but otherwise not
	@Test
	public void nestedObject() {
		final String filename = "nestedObject.ttls";
        final Graph g = convertAndLoadIntoGraph(filename);
        
        assertEquals( 5, g.size() );
        
        final Triple t = g.find().next();
        assertFalse( t.getSubject() instanceof Node_Triple );
        assertFalse( t.getPredicate() instanceof Node_Triple );
        assertFalse( t.getObject() instanceof Node_Triple );
		
	}	
	//same here,  "Triples not terminated by DOT"
	@Test
	public void nestedSubjectAndObject() {
		final String filename = "nestedSubjectAndObject.ttls";
        final Graph g = convertAndLoadIntoGraph(filename);
        
        assertEquals( 9, g.size() );
        
        final Triple t = g.find().next();
        assertFalse( t.getSubject() instanceof Node_Triple );
        assertFalse( t.getPredicate() instanceof Node_Triple );
        assertFalse( t.getObject() instanceof Node_Triple );
		
	}
	
	@Test
	public void doubleNestedSubject() {
		final String filename = "doubleNestedSubject.ttls";
        final Graph g = convertAndLoadIntoGraph(filename);
		
        assertEquals( 8, g.size() );
        
        final Triple t = g.find().next();
        assertFalse( t.getSubject() instanceof Node_Triple );
        assertFalse( t.getPredicate() instanceof Node_Triple );
        assertFalse( t.getObject() instanceof Node_Triple );
	}
	
	@Test
	public void doubleNestedObject() {
		final String filename = "doubleNestedObject.ttls";
        final Graph g = convertAndLoadIntoGraph(filename);
        
        assertEquals( 5, g.size() );
        
        final Triple t = g.find().next();
        assertFalse( t.getSubject() instanceof Node_Triple );
        assertFalse( t.getPredicate() instanceof Node_Triple );
        assertFalse( t.getObject() instanceof Node_Triple );
		
	}
	
	// ---- helpers ----

	protected Graph convertAndLoadIntoGraph( String filename ) {
		final String fullFilename = getClass().getResource("/TurtleStar/"+filename).getFile();
		final ByteArrayOutputStream os = new ByteArrayOutputStream();

		new RDFStar2RDF().convert(fullFilename, os);
		final String result = os.toString();
		//TODO: delete the print when done with testing
		System.out.println(result);
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

}
