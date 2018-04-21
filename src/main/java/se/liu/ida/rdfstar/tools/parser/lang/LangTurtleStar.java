package se.liu.ida.rdfstar.tools.parser.lang;

import java.io.InputStream;
import java.io.Reader;

import org.apache.jena.atlas.web.ContentType;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.LangBuilder;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RDFParserRegistry;
import org.apache.jena.riot.ReaderRIOT;
import org.apache.jena.riot.ReaderRIOTFactory;
import org.apache.jena.riot.lang.LangRIOT;
import org.apache.jena.riot.lang.LangTurtle;
import org.apache.jena.riot.lang.RiotParsers;
import org.apache.jena.riot.system.ParserProfile;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.tokens.Tokenizer;
import org.apache.jena.sparql.util.Context;

import se.liu.ida.rdfstar.tools.parser.system.ParserProfileTurtleStar;
import se.liu.ida.rdfstar.tools.parser.system.ParserProfileTurtleStarWrapperImpl;
import se.liu.ida.rdfstar.tools.parser.tokens.TokenizerFactory;
import se.liu.ida.rdfstar.tools.parser.tokens.TokenizerText;

/**
 * This class represents the Turtle* language and performs the
 * plumbing of  registering Turtle* and its parser extensions
 * with Jena's RIOT parser framework.
 *   
 * @author Olaf Hartig http://olafhartig.de/
 */
public class LangTurtleStar extends LangTurtle
{
    /** Display name of Turtle*. */
    public static final String strLangTurtleStar = "Turtle*";

    /** Content type of Turtle*. */
    public static final String contentTypeTurtleStar = "text/x-turtlestar";

    /** Turtle* */
    public static final Lang TURTLESTAR = LangBuilder.create(strLangTurtleStar, contentTypeTurtleStar)
                                                .addAltNames("TTLS")
                                                .addFileExtensions("ttls")
                                                .build();

    /** Alternative constant for {@link #TURTLESTAR} */
    public static final Lang TTLS = TURTLESTAR;

    public static void init() {}
    static { init$() ; }
    
    private static synchronized void init$() {
    	RDFLanguages.register(TURTLESTAR);
    	RDFParserRegistry.registerLangTriples( TURTLESTAR, new TurtleStarReaderFactory() );
    }

    public LangTurtleStar(Tokenizer tokens, ParserProfile profile, StreamRDF dest) {
        super(tokens, wrapParserProfileIfNeeded(profile), dest);

        if ( ! (tokens instanceof TokenizerText) )
        	throw new IllegalArgumentException( "The given tokenizer is of an unexpected type (" + tokens.getClass().getName() + ")" );

        setCurrentGraph(null) ;
    }

    static protected ParserProfile wrapParserProfileIfNeeded( ParserProfile profile ) {
        if ( profile instanceof ParserProfileTurtleStar )
        	return profile;
        else
        	return new ParserProfileTurtleStarWrapperImpl(profile);
    }

    @Override
    public Lang getLang() {
        return TURTLESTAR;
    }


    static class TurtleStarReaderFactory implements ReaderRIOTFactory
    {
        @Override
        public ReaderRIOT create(Lang lang, ParserProfile profile) {
            return new TurtleStarReaderRIOT(lang, profile);
        }
    }

    static class TurtleStarReaderRIOT implements ReaderRIOT
    {
        private final Lang lang;
        private ParserProfile parserProfile = null;

        TurtleStarReaderRIOT(Lang lang, ParserProfile parserProfile) {
            this.lang = lang;
            this.parserProfile = parserProfile;
        }

        @Override
        public void read(InputStream in, String baseURI, ContentType ct, StreamRDF output, Context context) {
        	final LangRIOT parser;
        	if ( lang == TURTLESTAR ) {
        		final Tokenizer tokenizer = TokenizerFactory.makeTokenizerUTF8(in);
                parser = new LangTurtleStar(tokenizer, parserProfile, output);
        	}
        	else {
        		parser = RiotParsers.createParser(in, lang, output, parserProfile);
        	}

            parser.parse() ;
        }

        @Override
        public void read(Reader in, String baseURI, ContentType ct, StreamRDF output, Context context) {
        	final LangRIOT parser;
        	if ( lang == TURTLESTAR ) {
        		@SuppressWarnings("deprecation")
                final Tokenizer tokenizer = TokenizerFactory.makeTokenizer(in);
                parser = new LangTurtleStar(tokenizer, parserProfile, output);
        	}
        	else {
        		parser = RiotParsers.createParser(in, lang, output, parserProfile);
        	}

            parser.parse() ;
        }
    }

}
