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
import se.liu.ida.rdfstar.tools.sparqlstar.lang.Node_TripleStarPattern;

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
		final Node n1 = new Node_TripleStarPattern( new Triple(u,u,u) );
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
		final Node n1 = new Node_TripleStarPattern( new Triple(u,u,u) );
		final Triple tp = new Triple(n1, u, u);
		final QueryIterator input = new QueryIterNullIterator(null);

		final QueryIterator it = QueryIterTripleStarPattern.create(input, tp, null);
		assertTrue( it instanceof QueryIterTripleStarPattern );
		it.close();
	}

	@Test
	public void matchWholeSubjectTriple()
	{
		// ?V1 ex:m1 ex:x1
		final Triple tp = new Triple( $V1(), $m1(), $x1() );

		final ExecutionContext execCxt = createTestExecCxt();
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

	@Test
	public void matchWholeObjectTriple()
	{
		// ex:x1 ex:m1 ?V1
		final Triple tp = new Triple( $x1(), $m1(), $V1() );

		final ExecutionContext execCxt = createTestExecCxt();
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

	@Test
	public void matchSubjectInSubjectTriple()
	{
		// << ?V1 ex:p ex:o >> ex:m1 ex:x1
		final Triple tp1 = new Triple( $V1(), $p(), $o() );
		final Node nTP1 = new Node_TripleStarPattern( tp1 );
		final Triple tp2 = new Triple( nTP1, $m1(), $x1() );

		final Binding inputBinding = BindingFactory.binding();
		final ExecutionContext execCxt = createTestExecCxt();
		final QueryIterator input = QueryIterSingleton.create(inputBinding, execCxt);

		final QueryIterator it = QueryIterTripleStarPattern.create( input, tp2, execCxt );
		assertTrue( it.hasNext() );

		final Binding outputBinding = it.nextBinding();
		assertEquals( 1, outputBinding.size() );
		assertEquals( $V1(), outputBinding.vars().next() );

		assertEquals( $s(), outputBinding.get($V1()) );

		assertFalse( it.hasNext() ); 
		it.close();
	}

	@Test
	public void matchMetaTriplesBasedOnWholeSubjectTriple()
	{
		//  << ex:s ex:p ex:o >> ?V1 ?V2
		final Triple tp1 = new Triple( $s(), $p(), $o() );
		final Node nTP1 = new Node_TripleStarPattern( tp1 );
		final Triple tp2 = new Triple( nTP1, $V1(), $V2() );

		final Binding inputBinding = BindingFactory.binding();
		final ExecutionContext execCxt = createTestExecCxt();
		final QueryIterator input = QueryIterSingleton.create(inputBinding, execCxt);

		final QueryIterator it = QueryIterTripleStarPattern.create( input, tp2, execCxt );
		assertTrue( it.hasNext() );

		Binding outputBinding = null;

		outputBinding = it.nextBinding();
		assertEquals( 2, outputBinding.size() );

		assertEquals( $m1(), outputBinding.get($V1()) );
		assertEquals( $x2(), outputBinding.get($V2()) );

		outputBinding = it.nextBinding();
		assertEquals( 2, outputBinding.size() );

		assertEquals( $m1(), outputBinding.get($V1()) );
		assertEquals( $x1(), outputBinding.get($V2()) );

		assertFalse( it.hasNext() ); 
		it.close();
	}

	@Test
	public void matchSubjectInSubjectTripleAndObjectInMetaTriple()
	{
		// << ?V1 ex:p ex:o >> ex:m1 ?V2
		final Triple tp1 = new Triple( $V1(), $p(), $o() );
		final Node nTP1 = new Node_TripleStarPattern( tp1 );
		final Triple tp2 = new Triple( nTP1, $m1(), $V2() );

		final Binding inputBinding = BindingFactory.binding();
		final ExecutionContext execCxt = createTestExecCxt();
		final QueryIterator input = QueryIterSingleton.create(inputBinding, execCxt);

		final QueryIterator it = QueryIterTripleStarPattern.create( input, tp2, execCxt );
		assertTrue( it.hasNext() );

		Binding outputBinding = it.nextBinding();
		assertEquals( 2, outputBinding.size() );

		assertEquals( $s(), outputBinding.get($V1()) );
		assertEquals( $x2(), outputBinding.get($V2()) );

		outputBinding = it.nextBinding();
		assertEquals( 2, outputBinding.size() );

		assertEquals( $s(), outputBinding.get($V1()) );
		assertEquals( $x1(), outputBinding.get($V2()) );

		assertFalse( it.hasNext() ); 
		it.close();
	}

	@Test
	public void matchSubjectInSubjectTripleWithGivenObjectInMetaTriple()
	{
		// << ?V1 ex:p ex:o >> ex:m1 ?V2
		final Triple tp1 = new Triple( $V1(), $p(), $o() );
		final Node nTP1 = new Node_TripleStarPattern( tp1 );
		final Triple tp2 = new Triple( nTP1, $m1(), $V2() );

		// ?V2 --> ex:x1
		final Binding inputBinding = BindingFactory.binding( $V2(), $x1() );
		final ExecutionContext execCxt = createTestExecCxt();
		final QueryIterator input = QueryIterSingleton.create(inputBinding, execCxt);

		final QueryIterator it = QueryIterTripleStarPattern.create( input, tp2, execCxt );
		assertTrue( it.hasNext() );

		final Binding outputBinding = it.nextBinding();
		assertEquals( 2, outputBinding.size() );

		assertEquals( $s(), outputBinding.get($V1()) );
		assertEquals( $x1(), outputBinding.get($V2()) );

		assertFalse( it.hasNext() ); 
		it.close();
	}

	@Test
	public void matchSubjectInMetaTripleWithGivenObjectInObjectTriple()
	{
		// ?V1 ?V2 << ex:s ex:p ?V3 >>
		final Triple tp1 = new Triple( $s(), $p(), $V3() );
		final Node nTP1 = new Node_TripleStarPattern( tp1 );
		final Triple tp2 = new Triple( $V1(), $V2(), nTP1 );

		// ?V3 --> ex:o
		final Binding inputBinding = BindingFactory.binding( $V3(), $o() );
		final ExecutionContext execCxt = createTestExecCxt();
		final QueryIterator input = QueryIterSingleton.create(inputBinding, execCxt);

		final QueryIterator it = QueryIterTripleStarPattern.create( input, tp2, execCxt );
		assertTrue( it.hasNext() );

		Binding outputBinding = it.nextBinding();
		assertEquals( 3, outputBinding.size() );

		assertEquals( $x2(), outputBinding.get($V1()) );
		assertEquals( $m2(), outputBinding.get($V2()) );
		assertEquals( $o(), outputBinding.get($V3()) );

		outputBinding = it.nextBinding();
		assertEquals( 3, outputBinding.size() );

		assertEquals( $x1(), outputBinding.get($V1()) );
		assertEquals( $m1(), outputBinding.get($V2()) );
		assertEquals( $o(), outputBinding.get($V3()) );

		assertFalse( it.hasNext() ); 
		it.close();
	}

	@Test
	public void matchSubjectInMetaTripleWithGivenSubjectInObjectTriple()
	{
		// ?V1 ?V2 << ?V3 ex:p ex:o >>
		final Triple tp1 = new Triple( $s(), $p(), $V3() );
		final Node nTP1 = new Node_TripleStarPattern( tp1 );
		final Triple tp2 = new Triple( $V1(), $V2(), nTP1 );

		// ?V2 --> ex:m1, ?V3 --> ex:s
		final Binding inputBindingX = BindingFactory.binding( $V2(), $m1() );
		final Binding inputBinding = BindingFactory.binding( inputBindingX, $V3(), $o() );
		final ExecutionContext execCxt = createTestExecCxt();
		final QueryIterator input = QueryIterSingleton.create(inputBinding, execCxt);

		final QueryIterator it = QueryIterTripleStarPattern.create( input, tp2, execCxt );
		assertTrue( it.hasNext() );

		final Binding outputBinding = it.nextBinding();
		assertEquals( 3, outputBinding.size() );

		assertEquals( $x1(), outputBinding.get($V1()) );
		assertEquals( $m1(), outputBinding.get($V2()) );
		assertEquals( $o(), outputBinding.get($V3()) );

		assertFalse( it.hasNext() ); 
		it.close();
	}

	@Test
	public void matchNothingWithGivenObjectInObjectTriple()
	{
		// ?V1 ?V2 << ex:s ex:p ?V3 >>
		final Triple tp1 = new Triple( $s(), $p(), $V3() );
		final Node nTP1 = new Node_TripleStarPattern( tp1 );
		final Triple tp2 = new Triple( $V1(), $V2(), nTP1 );

		// ?V3 --> ex:x1
		final Binding inputBinding = BindingFactory.binding( $V3(), $x1() );
		final ExecutionContext execCxt = createTestExecCxt();
		final QueryIterator input = QueryIterSingleton.create(inputBinding, execCxt);

		final QueryIterator it = QueryIterTripleStarPattern.create( input, tp2, execCxt );
		assertFalse( it.hasNext() ); 
		it.close();
	}


	// --- helpers ---

	static protected Var $V1()  { return Var.alloc("V1"); }
	static protected Var $V2()  { return Var.alloc("V2"); }
	static protected Var $V3()  { return Var.alloc("V3"); }

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
				"<< ex:s ex:p ex:o >>  ex:m1  ex:x2 . " +
				"ex:x1  ex:m1  << ex:s ex:p ex:o >> . " +
				"ex:x2  ex:m2  << ex:s ex:p ex:o >> . ";

		return LangTurtleStarTest.createGraphFromTurtleStarSnippet(ttlsString);
	}

	protected ExecutionContext createTestExecCxt()
	{
		final DatasetGraph dsg = DatasetGraphFactory.create( createTestGraph() );
		final Context context = ARQ.getContext();
		return new ExecutionContext( context, dsg.getDefaultGraph(), dsg, QC.getFactory(context) );
	}

}
