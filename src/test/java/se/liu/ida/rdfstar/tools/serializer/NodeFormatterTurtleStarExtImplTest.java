package se.liu.ida.rdfstar.tools.serializer;

import static org.junit.Assert.assertEquals;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Triple;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.liu.ida.rdfstar.tools.parser.system.AbstractParserProfileTurtleStarTest;

/**
 * 
 * @author Olaf Hartig http://olafhartig.de/
 */
public class NodeFormatterTurtleStarExtImplTest  extends AbstractNodeFormatterTurtleStarTest
{
	public NodeFormatterTurtleStarExtImplTest() {
		super( createNodeFormatterTurtleStar() );
	}

	static protected NodeFormatterTurtleStar createNodeFormatterTurtleStar() {
		return new NodeFormatterTurtleStarExtImpl(
				null, // baseIRI
				AbstractParserProfileTurtleStarTest.getPrefixMap());
	}

	@Before
	public void setup() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void noTriple() {
		final Node n = NodeFactory.createURI("http://example.com");
		final String s = serialize(n);
		assertEquals("<http://example.com>", s);
	}

	@Test
	public void triple() {
		final Node u = NodeFactory.createURI("http://example.com");
		final Node n = new Node_Triple(new Triple(u, u, u));
		final String s = serialize(n);
		assertEquals("<<<http://example.com> <http://example.com> <http://example.com>>>", s);
	}

	@Test
	public void nestedTripleS() {
		final Node u = NodeFactory.createURI("http://example.com");
		final Node n1 = new Node_Triple(new Triple(u, u, u));
		final Node n2 = new Node_Triple(new Triple(n1, u, u));
		final String s = serialize(n2);
		assertEquals("<<<<<http://example.com> <http://example.com> <http://example.com>>> <http://example.com> <http://example.com>>>", s);
	}

	@Test
	public void nestedTripleO() {
		final Node u = NodeFactory.createURI("http://example.com");
		final Node n1 = new Node_Triple(new Triple(u, u, u));
		final Node n2 = new Node_Triple(new Triple(u, u, n1));
		final String s = serialize(n2);
		assertEquals("<<<http://example.com> <http://example.com> <<<http://example.com> <http://example.com> <http://example.com>>>>>", s);
	}

}
