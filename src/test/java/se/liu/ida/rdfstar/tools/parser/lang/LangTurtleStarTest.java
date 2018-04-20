package se.liu.ida.rdfstar.tools.parser.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.util.Iterator;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFParserRegistry;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFLib;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LangTurtleStarTest {
	
	@Before
	public void setup() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void registrationOK() {
		assertTrue( RDFLanguages.isRegistered(LangTurtleStar.TURTLESTAR) );
		assertTrue( RDFLanguages.isTriples(LangTurtleStar.TURTLESTAR) );
		assertTrue( RDFParserRegistry.isRegistered(LangTurtleStar.TURTLESTAR) );
		assertTrue( RDFParserRegistry.isTriples(LangTurtleStar.TURTLESTAR) );
		assertEquals( LangTurtleStar.TURTLESTAR, RDFLanguages.fileExtToLang("ttls") );
		assertEquals( LangTurtleStar.TURTLESTAR, RDFLanguages.filenameToLang("simple.ttls") );		
	}

	@Test
	public void stringParse1() {
        final String x = "<<<s> <p> <o>>> <p2> <o2> .";
        final Graph g = createGraphFromTurtleStarSnippet(x);

        assertEquals( 1, g.size() );

        final Triple t = g.find().next();
        assertTrue( t.getSubject() instanceof Node_Triple );
        assertFalse( t.getPredicate() instanceof Node_Triple );
        assertFalse( t.getObject() instanceof Node_Triple );
	}

	@Test
	public void stringParse2() {
        final String x = "<s2> <p2> <<<s> <p> <o>>> .";
        final Graph g = createGraphFromTurtleStarSnippet(x);

        assertEquals( 1, g.size() );

        final Triple t = g.find().next();
        assertFalse( t.getSubject() instanceof Node_Triple );
        assertFalse( t.getPredicate() instanceof Node_Triple );
        assertTrue( t.getObject() instanceof Node_Triple );
	}

	@Test
	public void stringParse3() {
        final String x = "<<<s> <p> <o>>> <p2> <<<s> <p> <o>>> .";
        final Graph g = createGraphFromTurtleStarSnippet(x);

        assertEquals( 1, g.size() );

        final Triple t = g.find().next();
        assertTrue( t.getSubject() instanceof Node_Triple );
        assertFalse( t.getPredicate() instanceof Node_Triple );
        assertTrue( t.getObject() instanceof Node_Triple );
	}

	@Test
	public void stringParse4() {
        final String x = "<< <s> <p> <<<s> <p> <o>>> >> <p2> <o2> .";
        final Graph g = createGraphFromTurtleStarSnippet(x);

        assertEquals( 1, g.size() );

        final Triple t = g.find().next();
        assertTrue( t.getSubject() instanceof Node_Triple );
        assertFalse( t.getPredicate() instanceof Node_Triple );
        assertFalse( t.getObject() instanceof Node_Triple );

        final Triple et = ( (Node_Triple) t.getSubject() ).get();
        assertFalse( et.getSubject() instanceof Node_Triple );
        assertFalse( et.getPredicate() instanceof Node_Triple );
        assertTrue( et.getObject() instanceof Node_Triple );
	}

	@Test
	public void stringParse5() {
        final String x = "<<<s> <p> <o>>> <p2> <o2> , <o3> .";
        final Graph g = createGraphFromTurtleStarSnippet(x);

        assertEquals( 2, g.size() );

        final Iterator<Triple> it = g.find();

        final Triple t1 = it.next();
        assertTrue( t1.getSubject() instanceof Node_Triple );
        assertFalse( t1.getPredicate() instanceof Node_Triple );
        assertFalse( t1.getObject() instanceof Node_Triple );

        final Triple t2 = it.next();
        assertTrue( t2.getSubject() instanceof Node_Triple );
        assertFalse( t2.getPredicate() instanceof Node_Triple );
        assertFalse( t2.getObject() instanceof Node_Triple );

        final Triple et1 = ( (Node_Triple) t1.getSubject() ).get();
        final Triple et2 = ( (Node_Triple) t2.getSubject() ).get();
        assertTrue( et1 == et2 );
	}

	@Test
	public void stringParse6() {
        final String x = "<<<s> <p> <o>>> <p2> <o2> ; <p3> <o3> .";
        final Graph g = createGraphFromTurtleStarSnippet(x);

        assertEquals( 2, g.size() );

        final Iterator<Triple> it = g.find();

        final Triple t1 = it.next();
        assertTrue( t1.getSubject() instanceof Node_Triple );
        assertFalse( t1.getPredicate() instanceof Node_Triple );
        assertFalse( t1.getObject() instanceof Node_Triple );

        final Triple t2 = it.next();
        assertTrue( t2.getSubject() instanceof Node_Triple );
        assertFalse( t2.getPredicate() instanceof Node_Triple );
        assertFalse( t2.getObject() instanceof Node_Triple );

        final Triple et1 = ( (Node_Triple) t1.getSubject() ).get();
        final Triple et2 = ( (Node_Triple) t2.getSubject() ).get();
        assertTrue( et1 == et2 );
	}

	@Test
	public void stringParse7() {
        final String x = "<s2> <p2> <o2> , <<<s> <p> <o>>> .";
        final Graph g = createGraphFromTurtleStarSnippet(x);

        assertEquals( 2, g.size() );

        final Iterator<Triple> it = g.find();

        final Triple t1 = it.next();
        assertFalse( t1.getSubject() instanceof Node_Triple );
        assertFalse( t1.getPredicate() instanceof Node_Triple );

        final Triple t2 = it.next();
        assertFalse( t2.getSubject() instanceof Node_Triple );
        assertFalse( t2.getPredicate() instanceof Node_Triple );

        if ( t1.getObject() instanceof Node_Triple )
        	assertFalse( t2.getObject() instanceof Node_Triple );
        else
        	assertTrue( t2.getObject() instanceof Node_Triple );
	}

	@Test
	public void fileParse1() {
		final String filename = "simple.ttls";
        final Graph g = loadGraphFromTurtleStarFile(filename);

        assertEquals( 1, g.size() );

        final Triple t = g.find().next();
        assertTrue( t.getSubject() instanceof Node_Triple );
        assertFalse( t.getPredicate() instanceof Node_Triple );
        assertFalse( t.getObject() instanceof Node_Triple );
	}


	// ---- helpers ----

	protected Graph createGraphFromTurtleStarSnippet( String snippet ) {
		final StringReader reader = new StringReader(snippet);
        final Graph g = ModelFactory.createDefaultModel().getGraph();
        final StreamRDF dest = StreamRDFLib.graph(g);

        RDFParser.create()
                 .source(reader)
                 .lang(LangTurtleStar.TURTLESTAR)
                 .checking(false)  // !!!!
                 .parse(dest);

		return g;
	}

	protected Graph loadGraphFromTurtleStarFile( String filename ) {

		final String fullFilename = getClass().getResource("/TurtleStar/"+filename).getFile();

        final Graph g = ModelFactory.createDefaultModel().getGraph();
        final StreamRDF dest = StreamRDFLib.graph(g);

		assertEquals( LangTurtleStar.TURTLESTAR, RDFLanguages.filenameToLang(fullFilename) );

        RDFParser.create()
                 .source( "file://" + fullFilename )
                 .checking(false)  // !!!!
                 .parse(dest);

		return g;
	}

}
