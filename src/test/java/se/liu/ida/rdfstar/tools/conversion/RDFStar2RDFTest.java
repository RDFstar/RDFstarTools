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
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;

/**
 * 
 * @author Ebba Lindström
 * @author Olaf Hartig
 */

public class RDFStar2RDFTest
{	
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

        int cntTypeStmt = 0;
        int cntSubjectStmt = 0;
        int cntPredicateStmt = 0;
        int cntObjectStmt = 0;
        int cntMetaStmt = 0;

        final Iterator<Triple> it = g.find();
        while ( it.hasNext() )
        {
        	final Triple t = it.next();
        	if ( t.getPredicate().equals(RDF.type.asNode()) && t.getObject().equals(RDF.Statement.asNode()) )
        		cntTypeStmt++;
        	else if ( t.getPredicate().equals(RDF.subject.asNode()) )
        		cntSubjectStmt++;
        	else if ( t.getPredicate().equals(RDF.predicate.asNode()) && t.getObject().equals(DCTerms.creator.asNode()) )
        		cntPredicateStmt++;
        	else if ( t.getPredicate().equals(RDF.object.asNode()) )
        		cntObjectStmt++;
        	else if ( t.getPredicate().equals(DCTerms.source.asNode()) )
        		cntMetaStmt++;
        }

        assertEquals( 1, cntTypeStmt );
        assertEquals( 1, cntSubjectStmt );
        assertEquals( 1, cntPredicateStmt );
        assertEquals( 1, cntObjectStmt );
        assertEquals( 1, cntMetaStmt );
	}

	@Test
	public void nestedObject()
	{
		final String filename = "nestedObject.ttls";
        final Graph g = convertAndLoadIntoGraph(filename);

        assertEquals( 5, g.size() );

        verifyNoNesting(g);

        int cntTypeStmt = 0;
        int cntSubjectStmt = 0;
        int cntPredicateStmt = 0;
        int cntObjectStmt = 0;
        int cntMetaStmt = 0;

        final Iterator<Triple> it = g.find();
        while ( it.hasNext() )
        {
        	final Triple t = it.next();
        	if ( t.getPredicate().equals(RDF.type.asNode()) && t.getObject().equals(RDF.Statement.asNode()) )
        		cntTypeStmt++;
        	else if ( t.getPredicate().equals(RDF.subject.asNode()) )
        		cntSubjectStmt++;
        	else if ( t.getPredicate().equals(RDF.predicate.asNode()) && t.getObject().equals(DCTerms.creator.asNode()) )
        		cntPredicateStmt++;
        	else if ( t.getPredicate().equals(RDF.object.asNode()) )
        		cntObjectStmt++;
        	else if ( t.getPredicate().equals(DCTerms.source.asNode()) )
        		cntMetaStmt++;
        }

        assertEquals( 1, cntTypeStmt );
        assertEquals( 1, cntSubjectStmt );
        assertEquals( 1, cntPredicateStmt );
        assertEquals( 1, cntObjectStmt );
        assertEquals( 1, cntMetaStmt );
	}

	@Test
	public void nestedSubjectAndObject()
	{
		final String filename = "nestedSubjectAndObject.ttls";
        final Graph g = convertAndLoadIntoGraph(filename);

        assertEquals( 9, g.size() );

        verifyNoNesting(g);

        int cntTypeStmt = 0;
        int cntSubjectStmt = 0;
        int cntPredicateStmt1 = 0;
        int cntPredicateStmt2 = 0;
        int cntObjectStmt = 0;
        int cntMetaStmt = 0;

        final Iterator<Triple> it = g.find();
        while ( it.hasNext() )
        {
        	final Triple t = it.next();
        	if ( t.getPredicate().equals(RDF.type.asNode()) && t.getObject().equals(RDF.Statement.asNode()) )
        		cntTypeStmt++;
        	else if ( t.getPredicate().equals(RDF.subject.asNode()) )
        		cntSubjectStmt++;
        	else if ( t.getPredicate().equals(RDF.predicate.asNode()) && t.getObject().equals(DCTerms.creator.asNode()) )
        		cntPredicateStmt1++;
        	else if ( t.getPredicate().equals(RDF.predicate.asNode()) && t.getObject().equals(DCTerms.created.asNode()) )
        		cntPredicateStmt2++;
        	else if ( t.getPredicate().equals(RDF.object.asNode()) )
        		cntObjectStmt++;
        	else if ( t.getPredicate().equals(DCTerms.requires.asNode()) )
        		cntMetaStmt++;
        }

        assertEquals( 2, cntTypeStmt );
        assertEquals( 2, cntSubjectStmt );
        assertEquals( 1, cntPredicateStmt1 );
        assertEquals( 1, cntPredicateStmt2 );
        assertEquals( 2, cntObjectStmt );
        assertEquals( 1, cntMetaStmt );
	}

	@Test
	public void doubleNestedSubject()
	{
		final String filename = "doubleNestedSubject.ttls";
        final Graph g = convertAndLoadIntoGraph(filename);

        assertEquals( 9, g.size() );

        verifyNoNesting(g);

        int cntTypeStmt = 0;
        int cntSubjectStmt = 0;
        int cntPredicateStmt1 = 0;
        int cntPredicateStmt2 = 0;
        int cntObjectStmt = 0;
        int cntMetaStmt = 0;

        final Iterator<Triple> it = g.find();
        while ( it.hasNext() )
        {
        	final Triple t = it.next();
        	if ( t.getPredicate().equals(RDF.type.asNode()) && t.getObject().equals(RDF.Statement.asNode()) )
        		cntTypeStmt++;
        	else if ( t.getPredicate().equals(RDF.subject.asNode()) )
        		cntSubjectStmt++;
        	else if ( t.getPredicate().equals(RDF.predicate.asNode()) && t.getObject().equals(FOAF.knows.asNode()) )
        		cntPredicateStmt1++;
        	else if ( t.getPredicate().equals(RDF.predicate.asNode()) && t.getObject().equals(DCTerms.created.asNode()) )
        		cntPredicateStmt2++;
        	else if ( t.getPredicate().equals(RDF.object.asNode()) )
        		cntObjectStmt++;
        	else if ( t.getPredicate().equals(DCTerms.source.asNode()) )
        		cntMetaStmt++;
        }

        assertEquals( 2, cntTypeStmt );
        assertEquals( 2, cntSubjectStmt );
        assertEquals( 1, cntPredicateStmt1 );
        assertEquals( 1, cntPredicateStmt2 );
        assertEquals( 2, cntObjectStmt );
        assertEquals( 1, cntMetaStmt );
	}

	@Test
	public void doubleNestedObject()
	{
		final String filename = "doubleNestedObject.ttls";
        final Graph g = convertAndLoadIntoGraph(filename);

        assertEquals( 9, g.size() );

        verifyNoNesting(g);

        int cntTypeStmt = 0;
        int cntSubjectStmt = 0;
        int cntPredicateStmt1 = 0;
        int cntPredicateStmt2 = 0;
        int cntObjectStmt = 0;
        int cntMetaStmt = 0;

        final Iterator<Triple> it = g.find();
        while ( it.hasNext() )
        {
        	final Triple t = it.next();
        	if ( t.getPredicate().equals(RDF.type.asNode()) && t.getObject().equals(RDF.Statement.asNode()) )
        		cntTypeStmt++;
        	else if ( t.getPredicate().equals(RDF.subject.asNode()) )
        		cntSubjectStmt++;
        	else if ( t.getPredicate().equals(RDF.predicate.asNode()) && t.getObject().equals(FOAF.knows.asNode()) )
        		cntPredicateStmt1++;
        	else if ( t.getPredicate().equals(RDF.predicate.asNode()) && t.getObject().equals(DCTerms.created.asNode()) )
        		cntPredicateStmt2++;
        	else if ( t.getPredicate().equals(RDF.object.asNode()) )
        		cntObjectStmt++;
        	else if ( t.getPredicate().equals(FOAF.knows.asNode()) )
        		cntMetaStmt++;
        }

        assertEquals( 2, cntTypeStmt );
        assertEquals( 2, cntSubjectStmt );
        assertEquals( 1, cntPredicateStmt1 );
        assertEquals( 1, cntPredicateStmt2 );
        assertEquals( 2, cntObjectStmt );
        assertEquals( 1, cntMetaStmt );
	}


	// ---- helpers ----

	protected Graph convertAndLoadIntoGraph( String filename )
	{
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
			assertFalse( t.getSubject()   instanceof Node_Triple );
			assertFalse( t.getPredicate() instanceof Node_Triple );
			assertFalse( t.getObject()    instanceof Node_Triple );
		}
	}

}
