package se.liu.ida.rdfstar.tools.parser.system;

import static org.junit.Assert.assertFalse;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RIOT;
import org.apache.jena.riot.system.ErrorHandlerFactory;
import org.apache.jena.riot.system.IRIResolver;
import org.apache.jena.riot.system.RiotLib;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.liu.ida.rdfstar.tools.parser.system.ParserProfileTurtleStarExtImpl;

/**
 * 
 * @author Olaf Hartig http://olafhartig.de/
 */
public class ParserProfileTurtleStarExtImplTest extends AbstractParserProfileTurtleStarTest
{
	public ParserProfileTurtleStarExtImplTest() {
		super( new ParserProfileTurtleStarExtImpl(RiotLib.factoryRDF(),
				 ErrorHandlerFactory.errorHandlerStd,
                 IRIResolver.create(),
                 getPrefixMap(),
                 RIOT.getContext().copy(),
                 false, // checking
                 false) );
	}

	@Before
	public void setup() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void noEmbeddedTriple() {
		final Node node = getNode( ":s" );

        assertFalse( node instanceof Node_Triple );
	}

	@Test
	public void embeddedTripleOK() {
		final Node node = getNode( "<<:s :p :o>>" );

		confirmTripleNode(node);
	}

	@Test
	public void nesting1() {
		final Node node = getNode( "<< <<:s :p :o>> :p2 :o2>>" );

		confirmTripleNode(node);

		final Triple triple = getTriple(node);
		confirmTripleNode(triple.getSubject());
	}

	@Test
	public void nesting2() {
		final Node node = getNode( "<< :s2 :p2 <<:s :p :o>> >>" );

		confirmTripleNode(node);

		final Triple triple = getTriple(node);
		confirmTripleNode(triple.getObject());
	}

	@Test
	public void nesting3() {
		final Node node = getNode( "<<<<:s :p :o>> :p2 <<:s :p :o>>>>" );

		confirmTripleNode(node);

		final Triple triple = getTriple(node);
		confirmTripleNode(triple.getSubject());
		confirmTripleNode(triple.getObject());
	}

}
