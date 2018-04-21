package se.liu.ida.rdfstar.tools.parser.tokens;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.jena.atlas.io.PeekReader;
import org.apache.jena.riot.RiotParseException;
import org.apache.jena.riot.tokens.Token;
import org.apache.jena.riot.tokens.TokenType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.liu.ida.rdfstar.tools.parser.tokens.EmbeddedTripleTokenUtils;
import se.liu.ida.rdfstar.tools.parser.tokens.TokenizerText;

/**
 * 
 * @author Olaf Hartig http://olafhartig.de/
 */
public class TokenizerTextTest {
	
	@Before
	public void setup() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void failTooFewElements1() {
		try {
			Token token = getToken( "<<:s :p >>" );
			fail("Expected exception missing");

			if ( token == null ) token = null; // avoid compiler warning
		}
		catch ( RiotParseException e ) {
			// this is expected
		}
	}

	@Test
	public void failTooFewElements2() {
		try {
			Token token = getToken( "<<:s >>" );
			fail("Expected exception missing");

			if ( token == null ) token = null; // avoid compiler warning
		}
		catch ( RiotParseException e ) {
			// this is expected
		}
	}

	@Test
	public void failTooFewElements3() {
		try {
			Token token = getToken( "<<>>" );
			fail("Expected exception missing");

			if ( token == null ) token = null; // avoid compiler warning
		}
		catch ( RiotParseException e ) {
			// this is expected
		}
	}

	@Test
	public void failTooManyElements() {
		try {
			Token token = getToken( "<<:s :p :o :o>>" );
			fail("Expected exception missing");

			if ( token == null ) token = null; // avoid compiler warning
		}
		catch ( RiotParseException e ) {
			// this is expected
		}
	}	

	@Test
	public void embeddedTripleOK() {
		final Token token = getToken( "<<:s :p :o>>" );

		confirmEmbeddedTripleToken(token);

		final Token tokenS = EmbeddedTripleTokenUtils.getSubjectSubToken(token);
		final Token tokenP = EmbeddedTripleTokenUtils.getPredicateSubToken(token);
		final Token tokenO = EmbeddedTripleTokenUtils.getObjectSubToken(token);

		assertEquals(TokenType.PREFIXED_NAME, tokenS.getType());
		assertEquals(TokenType.PREFIXED_NAME, tokenP.getType());
		assertEquals(TokenType.PREFIXED_NAME, tokenO.getType());
	}

	@Test
	public void nesting1() {
		final Token token = getToken( "<< <<:s :p :o>> :p2 :o2>>" );

		confirmEmbeddedTripleToken(token);

		final Token tokenS = EmbeddedTripleTokenUtils.getSubjectSubToken(token);
		final Token tokenP = EmbeddedTripleTokenUtils.getPredicateSubToken(token);
		final Token tokenO = EmbeddedTripleTokenUtils.getObjectSubToken(token);

		confirmEmbeddedTripleToken(tokenS);
		assertEquals(TokenType.PREFIXED_NAME, tokenP.getType());
		assertEquals(TokenType.PREFIXED_NAME, tokenO.getType());
	}

	@Test
	public void nesting2() {
		final Token token = getToken( "<<<<:s :p :o>> :p2 :o2>>" );

		confirmEmbeddedTripleToken(token);

		final Token tokenS = EmbeddedTripleTokenUtils.getSubjectSubToken(token);
		final Token tokenP = EmbeddedTripleTokenUtils.getPredicateSubToken(token);
		final Token tokenO = EmbeddedTripleTokenUtils.getObjectSubToken(token);

		confirmEmbeddedTripleToken(tokenS);
		assertEquals(TokenType.PREFIXED_NAME, tokenP.getType());
		assertEquals(TokenType.PREFIXED_NAME, tokenO.getType());
	}

	@Test
	public void nesting3() {
		final Token token = getToken( "<< :s2 :p2 <<:s :p :o>> >>" );

		confirmEmbeddedTripleToken(token);

		final Token tokenS = EmbeddedTripleTokenUtils.getSubjectSubToken(token);
		final Token tokenP = EmbeddedTripleTokenUtils.getPredicateSubToken(token);
		final Token tokenO = EmbeddedTripleTokenUtils.getObjectSubToken(token);

		assertEquals(TokenType.PREFIXED_NAME, tokenS.getType());
		assertEquals(TokenType.PREFIXED_NAME, tokenP.getType());
		confirmEmbeddedTripleToken(tokenO);
	}

	@Test
	public void nesting4() {
		final Token token = getToken( "<< :s2 :p2 <<:s :p :o>>>>" );

		confirmEmbeddedTripleToken(token);

		final Token tokenS = EmbeddedTripleTokenUtils.getSubjectSubToken(token);
		final Token tokenP = EmbeddedTripleTokenUtils.getPredicateSubToken(token);
		final Token tokenO = EmbeddedTripleTokenUtils.getObjectSubToken(token);

		assertEquals(TokenType.PREFIXED_NAME, tokenS.getType());
		assertEquals(TokenType.PREFIXED_NAME, tokenP.getType());
		confirmEmbeddedTripleToken(tokenO);
	}

	@Test
	public void nesting5() {
		final Token token = getToken( "<<<<:s :p :o>> :p2 <<:s :p :o>>>>" );

		confirmEmbeddedTripleToken(token);

		final Token tokenS = EmbeddedTripleTokenUtils.getSubjectSubToken(token);
		final Token tokenP = EmbeddedTripleTokenUtils.getPredicateSubToken(token);
		final Token tokenO = EmbeddedTripleTokenUtils.getObjectSubToken(token);

		confirmEmbeddedTripleToken(tokenS);
		assertEquals(TokenType.PREFIXED_NAME, tokenP.getType());
		confirmEmbeddedTripleToken(tokenO);
	}


	// -------- helpers -------------

	protected Token getToken( final String string ) {
		final PeekReader reader = PeekReader.readString(string);
		final TokenizerText tokenizer = new TokenizerText(reader);
		final Token token = tokenizer.next();
		tokenizer.close();

		return token;
	}

	protected void confirmEmbeddedTripleToken( final Token token ) {

		assertEquals(EmbeddedTripleTokenUtils.typeOfEmbeddedTripleTokens, token.getType());
		assertEquals(EmbeddedTripleTokenUtils.image1OfEmbeddedTripleTokens, token.getImage());
		assertEquals(EmbeddedTripleTokenUtils.image2OfEmbeddedTripleTokens, token.getImage2());

		assertNotNull( token.getSubToken1() );
		assertNotNull( token.getSubToken2() );
		assertNotNull( token.getSubToken2().getSubToken1() );
		assertNotNull( token.getSubToken2().getSubToken2() );

		assertTrue( EmbeddedTripleTokenUtils.isEmbeddedTripleToken(token) );
	}

}
