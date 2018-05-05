package se.liu.ida.rdfstar.tools.serializer;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.out.NodeToLabel;
import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.riot.system.PrefixMapFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Olaf Hartig http://olafhartig.de/
 */
public class SinkTripleStarOutputTest
{
	@Before
	public void setup() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void neitherBaseIRInotPrefixMap() {
		final Node u = NodeFactory.createURI("http://example.com/i");
		final Node n = new Node_Triple(new Triple(u, u, u));
		final String result = serialize( n, u, u, null, null );
		assertEquals("<<<http://example.com/i> <http://example.com/i> <http://example.com/i>>> <http://example.com/i> <http://example.com/i> .\n", result);
	}

	@Test
	public void withBaseIRI() {
		final Node u = NodeFactory.createURI("http://example.com/i");
		final Node n = new Node_Triple(new Triple(u, u, u));
		final String result = serialize( n, u, u, "http://example.com/", null );
		assertEquals("<<<i> <i> <i>>> <i> <i> .\n", result);
	}

	@Test
	public void withPrefixMap() {
		final Node u = NodeFactory.createURI("http://example.com/i");
		final Node n = new Node_Triple(new Triple(u, u, u));
		final String result = serialize( n, u, u, null, getPrefixMapForTests() );
		assertEquals("<<ex:i ex:i ex:i>> ex:i ex:i .\n", result);
	}

	@Test
	public void withBaseIRIandPrefixMap() {
		final Node u = NodeFactory.createURI("http://example.com/i");
		final Node n = new Node_Triple(new Triple(u, u, u));
		final String result = serialize( n, u, u, "http://example.com/", getPrefixMapForTests() );
		assertEquals("<<ex:i ex:i ex:i>> ex:i ex:i .\n", result);
	}


	// ---- helpers ----

	static public PrefixMap getPrefixMapForTests() {
		final PrefixMap pmap = PrefixMapFactory.createForOutput();
		pmap.add("ex", "http://example.com/");
		return pmap;
	}

	public String serialize( Node s, Node p, Node o, String base, PrefixMap pmap ) {
		return serialize( new Triple(s, p, o), base, pmap );
	}

	public String serialize( Triple t, String base, PrefixMap pmap ) {
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();

		final SinkTripleStarOutput out = new SinkTripleStarOutput(
				outstream,
				base,
				pmap,
				NodeToLabel.createScopeByDocument());

		out.send( t );
		out.close();
		try {
			outstream.close();
		}
		catch ( IOException e ) {
			throw new IllegalStateException("closing the output stream failed", e);
		}
		return outstream.toString();
	}

}
