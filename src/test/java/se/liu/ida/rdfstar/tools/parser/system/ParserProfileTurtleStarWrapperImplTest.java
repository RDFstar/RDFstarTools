package se.liu.ida.rdfstar.tools.parser.system;

import static org.junit.Assert.assertFalse;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RIOT;
import org.apache.jena.riot.system.ErrorHandlerFactory;
import org.apache.jena.riot.system.IRIResolver;
import org.apache.jena.riot.system.ParserProfileStd;
import org.apache.jena.riot.system.RiotLib;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.liu.ida.rdfstar.tools.parser.system.ParserProfileTurtleStar;
import se.liu.ida.rdfstar.tools.parser.system.ParserProfileTurtleStarWrapperImpl;

public class ParserProfileTurtleStarWrapperImplTest extends AbstractParserProfileTurtleStarTest
{
	public ParserProfileTurtleStarWrapperImplTest() {
		super( createParserProfileTurtleStar() );
	}

	static protected ParserProfileTurtleStar createParserProfileTurtleStar() {
		ParserProfileStd wrappedProfile = new ParserProfileStd(RiotLib.factoryRDF(), 
                ErrorHandlerFactory.errorHandlerStd,
                IRIResolver.create(),
                getPrefixMap(),
                RIOT.getContext().copy(),
                true, // checking
                false);

		return new ParserProfileTurtleStarWrapperImpl(wrappedProfile);		
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
