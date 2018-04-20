package se.liu.ida.rdfstar.tools.parser.system;

import static org.junit.Assert.assertTrue;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.riot.system.PrefixMapFactory;
import org.apache.jena.riot.tokens.Token;
import org.apache.jena.riot.tokens.Tokenizer;

import se.liu.ida.rdfstar.tools.parser.system.ParserProfileTurtleStar;
import se.liu.ida.rdfstar.tools.parser.tokens.TokenizerFactory;

public abstract class AbstractParserProfileTurtleStarTest
{
	final protected ParserProfileTurtleStar parserProfile;

	protected AbstractParserProfileTurtleStarTest( final ParserProfileTurtleStar parserProfile ) {
		this.parserProfile = parserProfile;
	}

	static public PrefixMap getPrefixMap() {
		final PrefixMap pmap = PrefixMapFactory.createForInput();
        pmap.add("" , "http://example/");
        return pmap;
	}

	protected Node getNode( final String string ) {
		final Tokenizer tokenizer = TokenizerFactory.makeTokenizerString(string);
		final Token token = tokenizer.next();
		tokenizer.close();

        return parserProfile.create(null, token);
	}

	protected void confirmTripleNode( final Node node ) {
		assertTrue( node instanceof Node_Triple );
		assertTrue( ((Node_Triple) node).get() instanceof Triple );
	}

	protected Triple getTriple( final Node node ) {
		return ((Node_Triple) node).get();
	}
	
}
