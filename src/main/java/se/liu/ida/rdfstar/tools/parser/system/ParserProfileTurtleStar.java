package se.liu.ida.rdfstar.tools.parser.system;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.system.ParserProfile;
import org.apache.jena.riot.tokens.Token;

import se.liu.ida.rdfstar.tools.parser.tokens.EmbeddedTripleTokenUtils;

/**
 * An extension of {@link ParserProfile} to indicate
 * parser profiles developed to support Turtle*.
 *  
 * @author Olaf Hartig http://olafhartig.de/
 */
public interface ParserProfileTurtleStar extends ParserProfile
{
	static class Helper {
		static public Node createTripleNodeFromEmbeddedTripleToken(
				final ParserProfile profile, final Node scope,
				final Token token, final long line, final long col) 
		{
			final Token tokenS = EmbeddedTripleTokenUtils.getSubjectSubToken(token);
			final Token tokenP = EmbeddedTripleTokenUtils.getPredicateSubToken(token);
			final Token tokenO = EmbeddedTripleTokenUtils.getObjectSubToken(token);

			final Node s = profile.create(scope, tokenS);
			final Node p = profile.create(scope, tokenP);
			final Node o = profile.create(scope, tokenO);

			final Triple t = profile.createTriple(s, p, o, token.getLine(), token.getColumn());
			return new Node_Triple(t);
		}
	}
}