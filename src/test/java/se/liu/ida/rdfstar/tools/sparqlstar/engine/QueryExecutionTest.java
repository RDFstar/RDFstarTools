package se.liu.ida.rdfstar.tools.sparqlstar.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.jena.graph.Graph;
import org.apache.jena.query.ARQ;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.engine.main.StageBuilder;
import org.apache.jena.sparql.graph.GraphFactory;
import org.junit.Before;
import org.junit.Test;

import se.liu.ida.rdfstar.tools.graph.GraphWrapperStar;
import se.liu.ida.rdfstar.tools.graph.RDFStarUtils;
import se.liu.ida.rdfstar.tools.parser.lang.LangTurtleStar;
import se.liu.ida.rdfstar.tools.sparqlstar.core.DatasetGraphWrapperStar;
import se.liu.ida.rdfstar.tools.sparqlstar.engine.main.StageGeneratorSPARQLStar;
import se.liu.ida.rdfstar.tools.sparqlstar.lang.SPARQLStar;

/**
 * 
 * @author Olaf Hartig http://olafhartig.de/
 */
public class QueryExecutionTest
{
	static final public String prefixes = "PREFIX ex: <http://example.com/> "
                                        + "PREFIX dct: <http://purl.org/dc/terms/> "
	                                    + "PREFIX foaf: <http://xmlns.com/foaf/0.1/> ";

	@Before
	public void setup()
	{
		LangTurtleStar.init();
		StageBuilder.setGenerator(ARQ.getContext(), new StageGeneratorSPARQLStar());
	}

	@Test
	public void queryDataset()
	{
		final String filename = "doubleNestedObject.ttls";
		final String queryString = prefixes + "SELECT ?s WHERE { ?s ?p ex:book }";

        final Query query = QueryFactory.create(queryString, null, SPARQLStar.syntax);
        final QueryExecution qexec = QueryExecutionFactory.create( query, loadDataset(filename) );
        final ResultSet rs = qexec.execSelect();

        consume( rs, "s", "http://example.com/bob" );
	}

	@Test
	public void queryModel()
	{
		final String filename = "doubleNestedObject.ttls";
		final String queryString = prefixes + "SELECT ?s WHERE { ?s ?p ex:book }";

        final Query query = QueryFactory.create(queryString, null, SPARQLStar.syntax);
        final QueryExecution qexec = QueryExecutionFactory.create( query, loadModel(filename) );
        final ResultSet rs = qexec.execSelect();

        consume( rs, "s", "http://example.com/bob" );
	}

	@Test
	public void doubleNestedObject1()
	{
		final String filename = "doubleNestedObject.ttls";
		final String queryString = prefixes + "SELECT ?s WHERE { ?s foaf:knows ?p }";

        test( queryString, filename, "s", "http://example.com/alice", "http://example.com/bob" );
	}

	@Test
	public void doubleNestedObject2()
	{
		final String filename = "doubleNestedObject.ttls";
		final String queryString = prefixes + "SELECT ?s WHERE { ex:bob foaf:knows <<?s ?p ?o>> }";

        test( queryString, filename, "s", "http://example.com/alice" );
	}

	@Test
	public void doubleNestedObject3()
	{
		final String filename = "doubleNestedObject.ttls";
		final String queryString = prefixes + "SELECT ?s WHERE { ex:alice foaf:knows <<?s ?p ?o>> }";

        test( queryString, filename, "s", "http://example.com/bob" );
	}

	@Test
	public void doubleNestedObject4()
	{
		final String filename = "doubleNestedObject.ttls";
		final String queryString = prefixes + "SELECT ?o2 WHERE { ex:bob foaf:knows <<?s ?p <<?s2 ?p2 ?o2>>>> }";

        test( queryString, filename, "o2", "http://example.com/book" );
	}

	@Test
	public void doubleNestedObject5()
	{
		final String filename = "doubleNestedObject.ttls";
		final String queryString = prefixes + "SELECT ?s WHERE { ex:alice foaf:knows ?t . ?s ?p ?t }";

        test( queryString, filename, "s", "http://example.com/alice" );
	}

	@Test
	public void doubleNestedObject6()
	{
		final String filename = "doubleNestedObject.ttls";
		final String queryString = prefixes + "SELECT ?s2 WHERE { ex:bob foaf:knows <<?s ?p ?t>> . ?s2 ?p2 ?t }";

        test( queryString, filename, "s2", "http://example.com/alice" );
	}

	@Test
	public void nestedSubjectAndObject1()
	{
		final String filename = "nestedSubjectAndObject.ttls";
		final String queryString = prefixes + "SELECT ?o WHERE { ex:book ?p ?o }";

        test( queryString, filename, "o", "http://example.com/alice" );
	}

	@Test
	public void nestedSubjectAndObject2()
	{
		final String filename = "nestedSubjectAndObject.ttls";
		final String queryString = prefixes + "SELECT ?o2 WHERE { ex:book ?p [ ?p2 ?o2 ] }";

        test( queryString, filename, "o2", "http://example.com/book" );
	}

	@Test
	public void testWithTTLStarString()
	{
		final String ttlsString =
				"@prefix ex: <http://example.com/> ." +
				"<< ex:s ex:p ex:o >>  ex:m1  ex:x1 . " +
				"<< ex:s ex:p ex:o >>  ex:m2  ex:x2 . ";

		final String queryString = prefixes + "SELECT ?o WHERE { ?t ex:m1 ex:x1 . ?t ex:m2 ?o }";

		final Graph g = RDFStarUtils.createRedundancyAugmentedGraphFromTurtleStarSnippet(ttlsString);
		final Model m = ModelFactory.createModelForGraph(g);


		final Query query = QueryFactory.create( queryString, null, SPARQLStar.syntax );
        final ResultSet rs = QueryExecutionFactory.create(query, m).execSelect();

        consume( rs, "o", "http://example.com/x2" );
	}

	@Test
	public void testWithTTLStarString2()
	{
		final String ttlsString =
				"@prefix ex: <http://example.com/> ." +
				"<< ex:s ex:p ex:o >>  ex:m1  ex:x1 . ";

		final String queryString = prefixes + "SELECT ?o WHERE { ?t ex:m1 ex:x1 . ?t ?p ?o }";

		final Graph g = RDFStarUtils.createRedundancyAugmentedGraphFromTurtleStarSnippet(ttlsString);
		final Model m = ModelFactory.createModelForGraph(g);


		final Query query = QueryFactory.create( queryString, null, SPARQLStar.syntax );
        final ResultSet rs = QueryExecutionFactory.create(query, m).execSelect();

        consume( rs, "o", "http://example.com/x1" );
	}


	// ---- helpers ----

	protected void test( String queryString, String filename, String varName, String... uriStrings )
	{
		consume( execute(queryString, filename), varName, uriStrings );
	}

	protected ResultSet execute( String queryString, String filename )
	{
		final Query query = QueryFactory.create( queryString, null, SPARQLStar.syntax );
        return QueryExecutionFactory.create(query, loadModel(filename)).execSelect();
	}

	protected Model loadModel( String filename )
	{
		final String fullFilename = getClass().getResource("/TurtleStar/"+filename).getFile();

		final Graph g = new GraphWrapperStar( GraphFactory.createDefaultGraph() );
		final Model m = ModelFactory.createModelForGraph(g);
		RDFDataMgr.read(m, fullFilename);
		return m;
	}

	protected Dataset loadDataset( String filename )
	{
		final String fullFilename = getClass().getResource("/TurtleStar/"+filename).getFile();

		final DatasetGraph dsg = DatasetGraphFactory.createTxnMem();
    	final DatasetGraph dsgWrapped = new DatasetGraphWrapperStar(dsg);
    	final Dataset ds = DatasetFactory.wrap(dsgWrapped);
		RDFDataMgr.read(ds, fullFilename);
		return ds;
	}

	protected void consume( ResultSet rs, String varName, String... uriStrings )
	{
		final int l = uriStrings.length;
		for ( int i = 0; i < l; i++ )
		{
			assertTrue( "The result set contains only " + i + " elements instead of " + l + ".",
			            rs.hasNext() );

	        final QuerySolution s = rs.next();

	        assertTrue( s.contains(varName) );
	        assertTrue( s.get(varName).isURIResource() );
	        assertEquals( uriStrings[i], s.get(varName).asResource().getURI() );
		}

		// rs should be exhausted at this point
		int x = 0;
		while ( rs.hasNext() ) {
			rs.next();
			x++;
		}

		assertEquals( 0, x );
	}

}
