package se.liu.ida.rdfstar.tools.parser.tokens;

import org.apache.jena.riot.tokens.Token;
import org.apache.jena.riot.tokens.TokenType;

public class EmbeddedTripleTokenUtils {

	static public TokenType typeOfEmbeddedTripleTokens = TokenType.KEYWORD;
	static public String image1OfEmbeddedTripleTokens = Token.ImageANY;
	static public String image2OfEmbeddedTripleTokens = "EmbeddedTripleToken";

	static public boolean isEmbeddedTripleToken( final Token token ) {
		return (token.getType() == typeOfEmbeddedTripleTokens)
				&& token.getImage().equals(image1OfEmbeddedTripleTokens)
				&& token.getImage2().equals(image2OfEmbeddedTripleTokens);
	}

	static public Token createEmbeddedTripleToken( final Token s, final Token p, final Token o ) {
		final Token token = new Token(typeOfEmbeddedTripleTokens, image1OfEmbeddedTripleTokens, image2OfEmbeddedTripleTokens);
    	setSubTokens(token, s, p, o);
		return token;
	}

	static public void initializeEmbeddedTripleToken( final Token t, final Token s, final Token p, final Token o ) {
    	t.setType(typeOfEmbeddedTripleTokens);
    	t.setImage(image1OfEmbeddedTripleTokens);
    	t.setImage2(image2OfEmbeddedTripleTokens);
    	setSubTokens(t, s, p, o);
	}

	static protected void setSubTokens( final Token t, final Token s, final Token p, final Token o ) {
    	final Token po = new Token(p.getLine(), p.getColumn());
    	po.setSubToken1(p);
    	po.setSubToken2(o);

    	t.setSubToken1(s);
    	t.setSubToken2(po);
	}

	static public Token getSubjectSubToken( final Token token ) {
		return token.getSubToken1();
	}

	static public Token getPredicateSubToken( final Token token ) {
		return token.getSubToken2().getSubToken1();
	}

	static public Token getObjectSubToken( final Token token ) {
		return token.getSubToken2().getSubToken2();
	}

}
