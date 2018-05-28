package se.liu.ida.rdfstar.tools.sparqlstar.resultset;

import static org.junit.Assert.assertEquals;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.shared.impl.PrefixMappingImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Olaf Hartig http://olafhartig.de/
 */
public class TextOutputStarTest
{
	@Before
	public void setup() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void withoutPrefixMapping() {
		final MyTextOutput o = new MyTextOutput( new PrefixMappingImpl() );
		assertEquals("<http://example.com/i> <http://example.com/i> <http://example.com/i> ", getResult(o));
	}

	@Test
	public void withPrefixMapping() {
		final MyTextOutput o = new MyTextOutput( getPrefixMappingForTests() );
		assertEquals("ex:i ex:i ex:i ", getResult(o));
	}

	@Test
	public void nested1() {
		final MyTextOutput o = new MyTextOutput( getPrefixMappingForTests() );
		
		final Node u = NodeFactory.createURI("http://example.com/i");
		final Node n1 = new Node_Triple(new Triple(u, u, u));
		final Node n2 = new Node_Triple(new Triple(n1, u, u));
		final QuerySolution s = createQuerySolution( "?t", n2 );		
		final String result = o.get(s, "?t");

		assertEquals("<< ex:i ex:i ex:i >> ex:i ex:i ", result);
	}

	@Test
	public void nested2() {
		final MyTextOutput o = new MyTextOutput( getPrefixMappingForTests() );
		
		final Node u = NodeFactory.createURI("http://example.com/i");
		final Node n1 = new Node_Triple(new Triple(u, u, u));
		final Node n2 = new Node_Triple(new Triple(u, u, n1));
		final QuerySolution s = createQuerySolution( "?t", n2 );		
		final String result = o.get(s, "?t");

		assertEquals("ex:i ex:i << ex:i ex:i ex:i >> ", result);
	}



	// ---- helpers ----

	static public PrefixMapping getPrefixMappingForTests() {
		return new PrefixMappingImpl().setNsPrefix("ex", "http://example.com/");
	}

	static class MyTextOutput extends TextOutputStar {
		public MyTextOutput( PrefixMapping pm ) { super(pm); }
		public String get(QuerySolution s, String varName) { return getVarValueAsString(s, varName); }
	}

	public String getResult( MyTextOutput o ) {
		final Node u = NodeFactory.createURI("http://example.com/i");
		final Node n = new Node_Triple(new Triple(u, u, u));
		final QuerySolution s = createQuerySolution( "?t", n );		
		return o.get(s, "?t");
	}

	public QuerySolution createQuerySolution( String varName, Node n ) {
		final RDFNode nn = ModelFactory.createDefaultModel().asRDFNode(n);
		final QuerySolutionMap s = new QuerySolutionMap();
		s.add(varName, nn);
		return s;
	}
}
