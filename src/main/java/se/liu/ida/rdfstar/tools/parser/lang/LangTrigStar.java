package se.liu.ida.rdfstar.tools.parser.lang;

import org.apache.jena.atlas.web.ContentType;
import org.apache.jena.riot.*;
import org.apache.jena.riot.lang.LangRIOT;
import org.apache.jena.riot.lang.LangTriG;
import org.apache.jena.riot.lang.RiotParsers;
import org.apache.jena.riot.system.ParserProfile;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.tokens.Tokenizer;
import org.apache.jena.sparql.util.Context;
import se.liu.ida.rdfstar.tools.parser.system.ParserProfileTrigStar;
import se.liu.ida.rdfstar.tools.parser.system.ParserProfileTrigStarWrapperImpl;
import se.liu.ida.rdfstar.tools.parser.tokens.TokenizerFactory;
import se.liu.ida.rdfstar.tools.parser.tokens.TokenizerText;

import java.io.InputStream;
import java.io.Reader;

/**
 * This class represents the Trig* language and performs the
 * plumbing of  registering Trig* and its parser extensions
 * with Jena's RIOT parser framework.
 *   
 * @author Robin Keskisärkkä
 */
public class LangTrigStar extends LangTriGExt
{
    /** Display name of Turtle*. */
    public static final String strLangTrigStar = "Trig*";

    /** Content type of Turtle*. */
    public static final String contentTypeTrigStar = "text/x-trigstar";

    /** Turtle* */
    public static final Lang TRIGSTAR = LangBuilder.create(strLangTrigStar, contentTypeTrigStar)
                                                .addAltNames("TRIGS")
                                                .addFileExtensions("trigs")
                                                .build();

    /** Alternative constant for {@link #TRIGSTAR} */
    public static final Lang TRIGS = TRIGSTAR;

    public static void init() {}
    static { init$() ; }

    private static synchronized void init$() {
    	RDFLanguages.register(TRIGSTAR);
    	RDFParserRegistry.registerLangTriples( TRIGSTAR, new TrigStarReaderFactory() );
    }

    public LangTrigStar(Tokenizer tokens, ParserProfile profile, StreamRDF dest) {
        super(tokens, wrapParserProfileIfNeeded(profile), dest);

        if ( ! (tokens instanceof TokenizerText) )
        	throw new IllegalArgumentException( "The given tokenizer is of an unexpected type (" + tokens.getClass().getName() + ")" );

        setCurrentGraph(null) ;
    }

    static protected ParserProfile wrapParserProfileIfNeeded( ParserProfile profile ) {
        if ( profile instanceof ParserProfileTrigStar)
        	return profile;
        else
        	return new ParserProfileTrigStarWrapperImpl(profile);
    }

    @Override
    public Lang getLang() {
        return TRIGSTAR;
    }


    static class TrigStarReaderFactory implements ReaderRIOTFactory
    {
        @Override
        public ReaderRIOT create(Lang lang, ParserProfile profile) {
            return new TrigStarReaderRIOT(lang, profile);
        }
    }

    static class TrigStarReaderRIOT implements ReaderRIOT
    {
        private final Lang lang;
        private ParserProfile parserProfile = null;

        TrigStarReaderRIOT(Lang lang, ParserProfile parserProfile) {
            this.lang = lang;
            this.parserProfile = parserProfile;
        }

        @Override
        public void read(InputStream in, String baseURI, ContentType ct, StreamRDF output, Context context) {
        	final LangRIOT parser;
        	if ( lang == TRIGSTAR ) {
        		final Tokenizer tokenizer = TokenizerFactory.makeTokenizerUTF8(in);
                parser = new LangTrigStar(tokenizer, parserProfile, output);
        	}
        	else {
        		parser = RiotParsers.createParser(in, lang, output, parserProfile);
        	}

            parser.parse() ;
        }

        @Override
        public void read(Reader in, String baseURI, ContentType ct, StreamRDF output, Context context) {
        	final LangRIOT parser;
        	if ( lang == TRIGSTAR ) {
        		@SuppressWarnings("deprecation")
                final Tokenizer tokenizer = TokenizerFactory.makeTokenizer(in);
                parser = new LangTrigStar(tokenizer, parserProfile, output);
        	}
        	else {
        		parser = RiotParsers.createParser(in, lang, output, parserProfile);
        	}

            parser.parse() ;
        }
    }
}
