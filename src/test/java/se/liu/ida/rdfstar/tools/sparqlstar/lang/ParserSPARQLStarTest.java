package se.liu.ida.rdfstar.tools.sparqlstar.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.lang.SPARQLParser;
import org.apache.jena.sparql.lang.SPARQLParserRegistry;
import org.apache.jena.sparql.syntax.ElementBind;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Olaf Hartig http://olafhartig.de/
 */
public class ParserSPARQLStarTest {
	
	@Before
	public void setup() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void registrationOK() {
		assertTrue( SPARQLParserRegistry.containsParserFactory(SPARQLStar.syntax) );
		assertTrue( SPARQLParserRegistry.parser(SPARQLStar.syntax) instanceof ParserSPARQLStar );

		final String queryString = "SELECT * WHERE { <<?s ?p ?o>> ?p2 ?o2 }";
		QueryFactory.create(queryString, SPARQLStar.syntax);

		try {
			QueryFactory.create(queryString); // This should fail with the
		}                                     // default SPARQL parser.
		catch ( QueryParseException e ) {  // Hence, this exception 
			return;                        // is expected.
		}
		fail( "Expected exception not thrown." );
	}

	@Test
	public void parseNestedTP1() {
		final String queryString = "SELECT * WHERE { <<?s ?p ?o>> ?p2 ?o2 }";
		//final String queryString = "SELECT * WHERE { BIND( <<<s> <p> <o>>> AS ?t) }";

		final Triple t = getTriplePattern(queryString);
		assertTrue(  t.getSubject()   instanceof  Node_Triple );
		assertFalse( t.getPredicate() instanceof  Node_Triple );
		assertFalse( t.getObject()    instanceof  Node_Triple );
	}

	@Test
	public void parseNestedTP2() {
		final String queryString = "SELECT * WHERE { <<<s> <p> <o>>> ?p ?o }";

		final Triple t = getTriplePattern(queryString);
		assertTrue(  t.getSubject()   instanceof  Node_Triple );
		assertFalse( t.getPredicate() instanceof  Node_Triple );
		assertFalse( t.getObject()    instanceof  Node_Triple );
	}

	@Test
	public void parseNestedTP3() {
		final String queryString = "SELECT * WHERE { ?s2 ?p2 <<?s ?p ?o>> }";

		final Triple t = getTriplePattern(queryString);
		assertFalse( t.getSubject()   instanceof  Node_Triple );
		assertFalse( t.getPredicate() instanceof  Node_Triple );
		assertTrue(  t.getObject()    instanceof  Node_Triple );
	}

	@Test
	public void parseNestedTP4() {
		final String queryString = "SELECT * WHERE { ?s ?p <<<s> <p> <o>>> }";

		final Triple t = getTriplePattern(queryString);
		assertFalse( t.getSubject()   instanceof  Node_Triple );
		assertFalse( t.getPredicate() instanceof  Node_Triple );
		assertTrue(  t.getObject()    instanceof  Node_Triple );
	}

	@Test
	public void parseNestedTPsSameSubject1() {
		final String queryString = "SELECT * WHERE { <<?s ?p ?o>> ?p2 ?o2 ; ?p3 ?o3 }";

		final ElementPathBlock epb = getElementPathBlock(queryString);
		assertEquals( 2, epb.getPattern().size() );
		assertTrue( epb.getPattern().get(0) instanceof TriplePath );
		assertTrue( epb.getPattern().get(1) instanceof TriplePath );

		final TriplePath tp1 = epb.getPattern().get(0);
		assertTrue( tp1.isTriple() );
		final Triple t1 = tp1.asTriple();

		final TriplePath tp2 = epb.getPattern().get(1);
		assertTrue( tp2.isTriple() );
		final Triple t2 = tp2.asTriple();

		assertTrue(  t1.getSubject()   instanceof  Node_Triple );
		assertFalse( t1.getPredicate() instanceof  Node_Triple );
		assertFalse( t1.getObject()    instanceof  Node_Triple );

		assertTrue(  t2.getSubject()   instanceof  Node_Triple );
		assertFalse( t2.getPredicate() instanceof  Node_Triple );
		assertFalse( t2.getObject()    instanceof  Node_Triple );

		assertTrue(  t1.getSubject().equals(t2.getSubject()) );
		assertFalse( t1.getPredicate().equals(t2.getPredicate()) );
		assertFalse( t1.getObject().equals(t2.getObject()) );
	}

	@Test
	public void parseNestedTPsSameSubject2() {
		final String queryString = "SELECT * WHERE { <<?s ?p ?o>> ?p2 ?o2 , ?o3 }";

		final ElementPathBlock epb = getElementPathBlock(queryString);
		assertEquals( 2, epb.getPattern().size() );
		assertTrue( epb.getPattern().get(0) instanceof TriplePath );
		assertTrue( epb.getPattern().get(1) instanceof TriplePath );

		final TriplePath tp1 = epb.getPattern().get(0);
		assertTrue( tp1.isTriple() );
		final Triple t1 = tp1.asTriple();

		final TriplePath tp2 = epb.getPattern().get(1);
		assertTrue( tp2.isTriple() );
		final Triple t2 = tp2.asTriple();

		assertTrue(  t1.getSubject()   instanceof  Node_Triple );
		assertFalse( t1.getPredicate() instanceof  Node_Triple );
		assertFalse( t1.getObject()    instanceof  Node_Triple );

		assertTrue(  t2.getSubject()   instanceof  Node_Triple );
		assertFalse( t2.getPredicate() instanceof  Node_Triple );
		assertFalse( t2.getObject()    instanceof  Node_Triple );

		assertTrue(  t1.getSubject().equals(t2.getSubject()) );
		assertTrue(  t1.getPredicate().equals(t2.getPredicate()) );
		assertFalse( t1.getObject().equals(t2.getObject()) );
	}

	@Test
	public void parseNestedTPsOneOfTwoObjects1() {
		final String queryString = "SELECT * WHERE { ?s1 ?p1 ?o1 ; ?p2 <<?s ?p ?o>> }";

		final ElementPathBlock epb = getElementPathBlock(queryString);
		assertEquals( 2, epb.getPattern().size() );
		assertTrue( epb.getPattern().get(0) instanceof TriplePath );
		assertTrue( epb.getPattern().get(1) instanceof TriplePath );

		final TriplePath tp1 = epb.getPattern().get(0);
		assertTrue( tp1.isTriple() );
		final Triple t1 = tp1.asTriple();

		final TriplePath tp2 = epb.getPattern().get(1);
		assertTrue( tp2.isTriple() );
		final Triple t2 = tp2.asTriple();

		assertFalse( t1.getSubject()   instanceof  Node_Triple );
		assertFalse( t1.getPredicate() instanceof  Node_Triple );
		assertFalse( t1.getObject()    instanceof  Node_Triple );

		assertFalse( t2.getSubject()   instanceof  Node_Triple );
		assertFalse( t2.getPredicate() instanceof  Node_Triple );
		assertTrue(  t2.getObject()    instanceof  Node_Triple );

		assertTrue(  t1.getSubject().equals(t2.getSubject()) );
		assertFalse( t1.getPredicate().equals(t2.getPredicate()) );
		assertFalse( t1.getObject().equals(t2.getObject()) );
	}

	@Test
	public void parseNestedTPsOneOfTwoObjects2() {
		final String queryString = "SELECT * WHERE { ?s1 ?p1 ?o1 , <<?s ?p ?o>> }";

		final ElementPathBlock epb = getElementPathBlock(queryString);
		assertEquals( 2, epb.getPattern().size() );
		assertTrue( epb.getPattern().get(0) instanceof TriplePath );
		assertTrue( epb.getPattern().get(1) instanceof TriplePath );

		final TriplePath tp1 = epb.getPattern().get(0);
		assertTrue( tp1.isTriple() );
		final Triple t1 = tp1.asTriple();

		final TriplePath tp2 = epb.getPattern().get(1);
		assertTrue( tp2.isTriple() );
		final Triple t2 = tp2.asTriple();

		assertFalse( t1.getSubject()   instanceof  Node_Triple );
		assertFalse( t1.getPredicate() instanceof  Node_Triple );
		assertFalse( t1.getObject()    instanceof  Node_Triple );

		assertFalse( t2.getSubject()   instanceof  Node_Triple );
		assertFalse( t2.getPredicate() instanceof  Node_Triple );
		assertTrue(  t2.getObject()    instanceof  Node_Triple );

		assertTrue(  t1.getSubject().equals(t2.getSubject()) );
		assertTrue(  t1.getPredicate().equals(t2.getPredicate()) );
		assertFalse( t1.getObject().equals(t2.getObject()) );
	}

	@Test
	public void parseNestedNestedTP1() {
		final String queryString = "SELECT * WHERE { << <<?s ?p ?o>> ?p2 ?o2>> ?p3 ?o3 }";

		final Triple t = getTriplePattern(queryString);
		assertTrue(  t.getSubject()   instanceof  Node_Triple );
		assertFalse( t.getPredicate() instanceof  Node_Triple );
		assertFalse( t.getObject()    instanceof  Node_Triple );

		final Triple t2 = ( (Node_Triple) t.getSubject() ).get();
		assertTrue(  t2.getSubject()   instanceof  Node_Triple );
		assertFalse( t2.getPredicate() instanceof  Node_Triple );
		assertFalse( t2.getObject()    instanceof  Node_Triple );
	}

	@Test
	public void parseNestedNestedTP2() {
		final String queryString = "SELECT * WHERE { <<<<?s ?p ?o>> ?p2 ?o2>> ?p3 ?o3 }";

		final Triple t = getTriplePattern(queryString);
		assertTrue(  t.getSubject()   instanceof  Node_Triple );
		assertFalse( t.getPredicate() instanceof  Node_Triple );
		assertFalse( t.getObject()    instanceof  Node_Triple );

		final Triple t2 = ( (Node_Triple) t.getSubject() ).get();
		assertTrue(  t2.getSubject()   instanceof  Node_Triple );
		assertFalse( t2.getPredicate() instanceof  Node_Triple );
		assertFalse( t2.getObject()    instanceof  Node_Triple );
	}

	@Test
	public void parseNestedNestedTP3() {
		final String queryString = "SELECT * WHERE { <<?s2 ?p2 <<?s ?p ?o>>>> ?p3 ?o3 }";

		final Triple t = getTriplePattern(queryString);
		assertTrue(  t.getSubject()   instanceof  Node_Triple );
		assertFalse( t.getPredicate() instanceof  Node_Triple );
		assertFalse( t.getObject()    instanceof  Node_Triple );

		final Triple t2 = ( (Node_Triple) t.getSubject() ).get();
		assertFalse( t2.getSubject()   instanceof  Node_Triple );
		assertFalse( t2.getPredicate() instanceof  Node_Triple );
		assertTrue(  t2.getObject()    instanceof  Node_Triple );
	}

	@Test
	public void parseNestedNestedTP4() {
		final String queryString = "SELECT * WHERE { <s> <p> <<?s2 ?p2 <<?s ?p ?o>>>> }";

		final Triple t = getTriplePattern(queryString);
		assertFalse( t.getSubject()   instanceof  Node_Triple );
		assertFalse( t.getPredicate() instanceof  Node_Triple );
		assertTrue(  t.getObject()    instanceof  Node_Triple );

		final Triple t2 = ( (Node_Triple) t.getObject() ).get();
		assertFalse( t2.getSubject()   instanceof  Node_Triple );
		assertFalse( t2.getPredicate() instanceof  Node_Triple );
		assertTrue(  t2.getObject()    instanceof  Node_Triple );
	}

	@Test
	public void parseBind1() {
		final String queryString = "SELECT * WHERE { BIND( <<<s> <p> <o>>> AS ?t) }";

		final Triple t = getBindTriplePattern(queryString);
		assertFalse( t.getSubject()   instanceof  Node_Triple );
		assertFalse( t.getPredicate() instanceof  Node_Triple );
		assertFalse( t.getObject()    instanceof  Node_Triple );
	}

	@Test
	public void parseBind2() {
		final String queryString = "SELECT * WHERE { BIND( <<<<<s> ?p ?o>> <p> <o>>> AS ?t) }";

		final Triple t = getBindTriplePattern(queryString);
		assertTrue(  t.getSubject()   instanceof  Node_Triple );
		assertFalse( t.getPredicate() instanceof  Node_Triple );
		assertFalse( t.getObject()    instanceof  Node_Triple );
	}


	// ---- helpers -------

	protected ElementGroup  getElementGroup( String queryString ) {
		final SPARQLParser parser = new ParserSPARQLStar();
		final Query query = parser.parse(new Query(), queryString);

		assertTrue( query.isSelectType() );
		assertTrue( "unexpected type (" + query.getQueryPattern().getClass() + ")", 
			        query.getQueryPattern() instanceof ElementGroup );

		return (ElementGroup) query.getQueryPattern();
	}

	protected ElementPathBlock getElementPathBlock( String queryString ) {
		final ElementGroup eg = getElementGroup(queryString);
		assertEquals( 1, eg.size() );
		assertTrue( "unexpected type (" + eg.get(0).getClass() + ")", 
				    eg.get(0) instanceof ElementPathBlock );

		return (ElementPathBlock) eg.get(0);
	}

	protected Triple getTriplePattern( String queryString ) {
		final ElementPathBlock epb = getElementPathBlock(queryString);
		assertEquals( 1, epb.getPattern().size() );
		assertTrue( "unexpected type (" + epb.getPattern().get(0).getClass() + ")", 
			        epb.getPattern().get(0) instanceof TriplePath );

		final TriplePath tp = epb.getPattern().get(0);
		assertTrue( tp.isTriple() );

		return tp.asTriple();
	}

	protected Triple getBindTriplePattern( String queryString ) {
		final ElementGroup eg = getElementGroup(queryString);
		assertEquals( 1, eg.size() );
		assertTrue( "unexpected type (" + eg.get(0).getClass() + ")", 
				    eg.get(0) instanceof ElementBind );

		final ElementBind eb = (ElementBind) eg.get(0);
		assertTrue( "unexpected type (" + eb.getExpr().getClass() + ")", 
			        eb.getExpr() instanceof NodeValue );

		final Node n = ( (NodeValue) eb.getExpr() ).getNode();
		assertTrue( "unexpected type (" + n.getClass() + ")", 
			        n instanceof Node_Triple );

		return ( (Node_Triple) n ).get();
	}

}
