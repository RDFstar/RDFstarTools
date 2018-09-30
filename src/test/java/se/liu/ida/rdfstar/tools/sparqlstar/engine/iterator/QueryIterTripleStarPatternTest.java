package se.liu.ida.rdfstar.tools.sparqlstar.engine.iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.ARQ;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.binding.BindingFactory;
import org.apache.jena.sparql.engine.iterator.QueryIterNullIterator;
import org.apache.jena.sparql.engine.iterator.QueryIterSingleton;
import org.apache.jena.sparql.engine.iterator.QueryIterTriplePattern;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.jena.sparql.util.Context;
import org.junit.Test;

import se.liu.ida.rdfstar.tools.parser.lang.LangTurtleStarTest;

/**
 * 
 * @author Olaf Hartig http://olafhartig.de/
 */
public class QueryIterTripleStarPatternTest
{
	@Test
	public void createWithoutNestedTP()
	{
		final Node u = NodeFactory.createURI("http://example.com/i");
		final Triple tp = new Triple( u, u, u );
		final QueryIterator input = new QueryIterNullIterator(null);

		final QueryIterator it = QueryIterTripleStarPattern.create(input, tp, null);
		assertTrue( it instanceof QueryIterTriplePattern );
		it.close();
	}

	@Test
	public void createWithNestedTP1()
	{
		final Node u = NodeFactory.createURI("http://example.com/i");
		final Node n1 = new Node_Triple( new Triple(u,u,u) );
		final Triple tp = new Triple(n1, u, u);
		final QueryIterator input = new QueryIterNullIterator(null);

		final QueryIterator it = QueryIterTripleStarPattern.create(input, tp, null);
		assertTrue( it instanceof QueryIterTripleStarPattern );
		it.close();
	}

	@Test
	public void createWithNestedTP2()
	{
		final Node u = NodeFactory.createURI("http://example.com/i");
		final Node n1 = new Node_Triple( new Triple(u,u,u) );
		final Triple tp = new Triple(n1, u, u);
		final QueryIterator input = new QueryIterNullIterator(null);

		final QueryIterator it = QueryIterTripleStarPattern.create(input, tp, null);
		assertTrue( it instanceof QueryIterTripleStarPattern );
		it.close();
	}

	@Test
	public void matchSubjectTriple()
	{
		final ExecutionContext execCxt = createTestExecCxt();
		final Triple tp = new Triple( $V1(), $m1(), $x1() );
		final Binding inputBinding = BindingFactory.binding();
		final QueryIterator input = QueryIterSingleton.create(inputBinding, execCxt);

		final QueryIterator it = QueryIterTripleStarPattern.create( input, tp, execCxt );
		assertTrue( it.hasNext() );

		final Binding outputBinding = it.nextBinding();
		assertEquals( 1, outputBinding.size() );
		assertEquals( $V1(), outputBinding.vars().next() );

		final Node outputValue = outputBinding.get( $V1() );  
		assertTrue( outputValue instanceof Node_Triple );

		final Triple outputTriple = ( (Node_Triple) outputValue ).get();
		assertEquals( $s(), outputTriple.getSubject() );
		assertEquals( $p(), outputTriple.getPredicate() );
		assertEquals( $o(), outputTriple.getObject() );

		assertFalse( it.hasNext() ); 
		it.close();
	}


	// --- helpers ---

	static protected Var $V1()  { return Var.alloc("V1"); }
	static protected Var $V2()  { return Var.alloc("V2"); }

	static protected Node $s()  { return NodeFactory.createURI("http://example.com/s"); }
	static protected Node $p()  { return NodeFactory.createURI("http://example.com/p"); }
	static protected Node $o()  { return NodeFactory.createURI("http://example.com/o"); }
	static protected Node $m1() { return NodeFactory.createURI("http://example.com/m1"); }
	static protected Node $m2() { return NodeFactory.createURI("http://example.com/m2"); }
	static protected Node $x1() { return NodeFactory.createURI("http://example.com/x1"); }
	static protected Node $x2() { return NodeFactory.createURI("http://example.com/x2"); }

	protected Graph createTestGraph()
	{
		final String ttlsString =
				"@prefix ex: <http://example.com/> ." +
				"<< ex:s ex:p ex:o >>  ex:m1  ex:x1 . " +
				"<< ex:s ex:p ex:o >>  ex:m1  ex:x2 . ";

		return LangTurtleStarTest.createGraphFromTurtleStarSnippet(ttlsString);
	}

	protected ExecutionContext createTestExecCxt()
	{
		final DatasetGraph dsg = DatasetGraphFactory.create( createTestGraph() );
		final Context context = ARQ.getContext();
		return new ExecutionContext( context, dsg.getDefaultGraph(), dsg, QC.getFactory(context) );
	}

}
