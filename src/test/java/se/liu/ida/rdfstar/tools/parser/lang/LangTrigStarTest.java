package se.liu.ida.rdfstar.tools.parser.lang;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFParserRegistry;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFLib;
import org.apache.jena.sparql.core.DatasetGraph;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import se.liu.ida.rdfstar.tools.graph.RDFStarUtils;

import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * TODO: Braces around triple in the default graph are required if the subject is a nested triple.
 *
 * @author Robin Keskisärkkä
 */
public class LangTrigStarTest {
	
	@Before
	public void setup() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void registrationOK() {
		assertTrue( RDFLanguages.isRegistered(LangTrigStar.TRIGSTAR) );
		assertTrue( RDFLanguages.isTriples(LangTrigStar.TRIGSTAR) );
		assertTrue( RDFParserRegistry.isRegistered(LangTrigStar.TRIGSTAR) );
		assertTrue( RDFParserRegistry.isTriples(LangTrigStar.TRIGSTAR) );
		assertEquals( LangTrigStar.TRIGSTAR, RDFLanguages.fileExtToLang("trigs") );
        assertEquals( LangTrigStar.TRIGSTAR, RDFLanguages.filenameToLang("OneNestedTriple1.trigs") ); // extend
	}

    /**
     * stringParse1, ..., stringParse7 and fileParse1 are the same as the tests for Turtle*
     */
	@Test
	public void stringParse1() {
        final String x = "<<<s> <p> <o>>> <p2> <o> . ";
        final DatasetGraph ds = RDFStarUtils.createDatasetGraphFromTrigStarSnippet(x);

        assertEquals( 1, ds.getDefaultGraph().size() );

        final Triple t = ds.getDefaultGraph().find().next();
        assertTrue( t.getSubject() instanceof Node_Triple );
        assertFalse( t.getPredicate() instanceof Node_Triple );
        assertFalse( t.getObject() instanceof Node_Triple );
	}

	@Test
	public void stringParse2() {
        final String x = "<s2> <p2> <<<s> <p> <o>>> .";
        final DatasetGraph ds = RDFStarUtils.createDatasetGraphFromTrigStarSnippet(x);

        assertEquals( 1, ds.getDefaultGraph().size() );

        final Triple t = ds.getDefaultGraph().find().next();
        assertFalse( t.getSubject() instanceof Node_Triple );
        assertFalse( t.getPredicate() instanceof Node_Triple );
        assertTrue( t.getObject() instanceof Node_Triple );
	}

	@Test
	public void stringParse3() {
        final String x = "<<<s> <p> <o>>> <p2> <<<s> <p> <o>>> .";
        final DatasetGraph ds = RDFStarUtils.createDatasetGraphFromTrigStarSnippet(x);

        assertEquals( 1, ds.getDefaultGraph().size() );

        final Triple t = ds.getDefaultGraph().find().next();
        assertTrue( t.getSubject() instanceof Node_Triple );
        assertFalse( t.getPredicate() instanceof Node_Triple );
        assertTrue( t.getObject() instanceof Node_Triple );
	}

	@Test
	public void stringParse4() {
        final String x = "<< <s> <p> <<<s> <p> <o>>> >> <p2> <o2> .";
        final DatasetGraph ds = RDFStarUtils.createDatasetGraphFromTrigStarSnippet(x);

        assertEquals( 1, ds.getDefaultGraph().size() );

        final Triple t = ds.getDefaultGraph().find().next();
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
        final DatasetGraph ds = RDFStarUtils.createDatasetGraphFromTrigStarSnippet(x);

        assertEquals( 2, ds.getDefaultGraph().size() );

        final Iterator<Triple> it = ds.getDefaultGraph().find();

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
        final String x = "<<<s> <p> <o>>> <p2> <o2> ; <p3> <o3> . ";
        final DatasetGraph ds = RDFStarUtils.createDatasetGraphFromTrigStarSnippet(x);

        assertEquals( 2, ds.getDefaultGraph().size() );

        final Iterator<Triple> it = ds.getDefaultGraph().find();

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
        final DatasetGraph ds = RDFStarUtils.createDatasetGraphFromTrigStarSnippet(x);

        assertEquals( 2, ds.getDefaultGraph().size() );

        final Iterator<Triple> it = ds.getDefaultGraph().find();

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

    /**
     * Named graph tests
     */
    @Test
    public void stringParse8() {
        final String x = "<g> { <<<s> <p> <o>>> <p2> <o> . }";
        final DatasetGraph ds = RDFStarUtils.createDatasetGraphFromTrigStarSnippet(x);

        Node n = ds.listGraphNodes().next();
        Graph g = ds.getGraph(n);
        assertEquals( 1, g.size() );

        final Triple t = g.find().next();
        assertTrue( t.getSubject() instanceof Node_Triple );
        assertFalse( t.getPredicate() instanceof Node_Triple );
        assertFalse( t.getObject() instanceof Node_Triple );
    }

    @Test
    public void stringParse9() {
        final String x = "<g> { <s2> <p2> <<<s> <p> <o>>> . }";
        final DatasetGraph ds = RDFStarUtils.createDatasetGraphFromTrigStarSnippet(x);

        Node n = ds.listGraphNodes().next();
        Graph g = ds.getGraph(n);
        assertEquals( 1, g.size() );

        final Triple t = g.find().next();
        assertFalse( t.getSubject() instanceof Node_Triple );
        assertFalse( t.getPredicate() instanceof Node_Triple );
        assertTrue( t.getObject() instanceof Node_Triple );
    }

    @Test
    public void stringParse10() {
        final String x = "<g> { <<<s> <p> <o>>> <p2> <<<s> <p> <o>>> . }";
        final DatasetGraph ds = RDFStarUtils.createDatasetGraphFromTrigStarSnippet(x);

        Node n = ds.listGraphNodes().next();
        Graph g = ds.getGraph(n);
        assertEquals( 1, g.size() );

        final Triple t = g.find().next();
        assertTrue( t.getSubject() instanceof Node_Triple );
        assertFalse( t.getPredicate() instanceof Node_Triple );
        assertTrue( t.getObject() instanceof Node_Triple );
    }

    @Test
    public void stringParse11() {
        final String x = "<g> { << <s> <p> <<<s> <p> <o>>> >> <p2> <o2> . }";
        final DatasetGraph ds = RDFStarUtils.createDatasetGraphFromTrigStarSnippet(x);

        Node n = ds.listGraphNodes().next();
        Graph g = ds.getGraph(n);
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
    public void stringParse12() {
        final String x = "<g> { <<<s> <p> <o>>> <p2> <o2> , <o3> . }";
        final DatasetGraph ds = RDFStarUtils.createDatasetGraphFromTrigStarSnippet(x);

        Node n = ds.listGraphNodes().next();
        Graph g = ds.getGraph(n);
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
    public void stringParse13() {
        final String x = "<g> { <<<s> <p> <o>>> <p2> <o2> ; <p3> <o3> . }";
        final DatasetGraph ds = RDFStarUtils.createDatasetGraphFromTrigStarSnippet(x);

        Node n = ds.listGraphNodes().next();
        Graph g = ds.getGraph(n);
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
    public void stringParse14() {
        final String x = "<g> { <s2> <p2> <o2> , <<<s> <p> <o>>> . }";
        final DatasetGraph ds = RDFStarUtils.createDatasetGraphFromTrigStarSnippet(x);

        Node n = ds.listGraphNodes().next();
        Graph g = ds.getGraph(n);
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
		final String filename = "OneNestedTriple1.trigs";
        final DatasetGraph ds = loadDatasetGraphFromTrigStarFile(filename);

        assertEquals( 1, ds.getDefaultGraph().size() );

        final Triple t = ds.getDefaultGraph().find().next();
        assertTrue( t.getSubject() instanceof Node_Triple );
        assertFalse( t.getPredicate() instanceof Node_Triple );
        assertFalse( t.getObject() instanceof Node_Triple );
	}

	// TRIG* tests with named graphs
    @Test
    public void fileParse2() {
        final String filename = "OneNestedTripleInNamedGraph1.trigs";
        final DatasetGraph ds = loadDatasetGraphFromTrigStarFile(filename);

        final Node n = ds.listGraphNodes().next();
        final Graph g = ds.getGraph(n);
        assertEquals(1, g.size());

        final Triple t = g.find().next();
        assertTrue( t.getSubject() instanceof Node_Triple );
        assertFalse( t.getPredicate() instanceof Node_Triple );
        assertFalse( t.getObject() instanceof Node_Triple );
    }

    @Test
    public void fileParse3() {
        final String filename = "OneNestedTripleInNamedGraph2.trigs";
        final DatasetGraph ds = loadDatasetGraphFromTrigStarFile(filename);

        final Node n = ds.listGraphNodes().next();
        final Graph g = ds.getGraph(n);
        assertEquals(1, g.size());

        final Triple t = g.find().next();
        assertTrue( t.getSubject() instanceof Node_Triple );
        assertFalse( t.getPredicate() instanceof Node_Triple );
        assertFalse( t.getObject() instanceof Node_Triple );
    }

    @Test
    public void fileParse4() {
        final String filename = "OneNestedTripleInNamedGraph3.trigs";
        final DatasetGraph ds = loadDatasetGraphFromTrigStarFile(filename);

        final Node n = ds.listGraphNodes().next();
        final Graph g = ds.getGraph(n);
        assertEquals(1, g.size());

        final Triple t = g.find().next();
        assertTrue( t.getSubject() instanceof Node_Triple );
        assertFalse( t.getPredicate() instanceof Node_Triple );
        assertFalse( t.getObject() instanceof Node_Triple );
    }

    @Test
    public void fileParse5() {
        final String filename = "OneNestedTripleInNamedGraph4.trigs";
        final DatasetGraph ds = loadDatasetGraphFromTrigStarFile(filename);

        final Node n = ds.listGraphNodes().next();
        final Graph g = ds.getGraph(n);
        assertEquals(1, g.size());

        final Triple t = g.find().next();
        assertTrue( t.getSubject() instanceof Node_Triple );
        assertFalse( t.getPredicate() instanceof Node_Triple );
        assertFalse( t.getObject() instanceof Node_Triple );
    }


	// ---- helpers ----

	protected DatasetGraph loadDatasetGraphFromTrigStarFile( String filename ) {

		final String fullFilename = getClass().getResource("/TrigStar/"+filename).getFile();

        final DatasetGraph ds = DatasetFactory.create().asDatasetGraph();
        final StreamRDF dest = StreamRDFLib.dataset(ds);

		assertEquals( LangTrigStar.TRIGSTAR, RDFLanguages.filenameToLang(fullFilename) );

        RDFParser.create()
                 .source( "file://" + fullFilename )
                 .parse(dest);

		return ds;
	}

}
