package se.liu.ida.rdfstar.tools.sparqlstar.lang;

import java.io.Reader;
import java.io.StringReader;

import org.apache.jena.atlas.logging.Log;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryException;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.query.Syntax;
import org.apache.jena.shared.JenaException;
import org.apache.jena.sparql.lang.SPARQLParser;
import org.apache.jena.sparql.lang.SPARQLParserFactory;
import org.apache.jena.sparql.lang.SPARQLParserRegistry;

import se.liu.ida.rdfstar.tools.sparqlstar.lang.sparqlstar.ParseException;
import se.liu.ida.rdfstar.tools.sparqlstar.lang.sparqlstar.SPARQLStarParser11;
import se.liu.ida.rdfstar.tools.sparqlstar.lang.sparqlstar.TokenMgrError;

/**
 * Parser for SPARQL*.
 * 
 * The parser may be used either by simply calling its method
 * {@link #parse(Query, String)} or by registering it in the
 * Jena query parser framework, which can be done by calling
 * {@link #init()}.
 *  
 * @author Olaf Hartig http://olafhartig.de/
 */
public class ParserSPARQLStar extends SPARQLParser
{
    public static void init() {}
    static {
    	syntaxSPARQLstar = new MySyntax("http://ida.liu.se/rdfstar/SPARQLstar_11");
    	init$();
    }
    
    private static synchronized void init$() {
    	final SPARQLParserFactory myFactory = new SPARQLParserFactory() {
    		@Override
            public boolean accept( Syntax syntax ) { return syntaxSPARQLstar.equals(syntax); } 
            @Override
            public SPARQLParser create( Syntax syntax ) { return new ParserSPARQLStar(); }
        };

    	SPARQLParserRegistry.addFactory(syntaxSPARQLstar, myFactory);
    }

	static public final Syntax syntaxSPARQLstar;

	static public class MySyntax extends Syntax {
		protected MySyntax(String s) { super(s); }
	}

    @Override
    protected Query parse$(final Query query, String queryString)
    {
        query.setSyntax(syntaxSPARQLstar);

        final Reader in = new StringReader(queryString);
        final SPARQLStarParser11 parser = new SPARQLStarParser11(in);

        try {
            query.setStrict(true);
            parser.setQuery(query);
            parser.QueryUnit();
        }
        catch (ParseException e) {
        	throw new QueryParseException(e.getMessage(),
                                          e.currentToken.beginLine,
                                          e.currentToken.beginColumn);
        }
        catch (TokenMgrError e) {
            throw new QueryParseException(e.getMessage(),
            		                      parser.token.endLine,
            		                      parser.token.endColumn);
        }
        catch (QueryException e) {
        	throw e;
        }
        catch (JenaException e) {
        	throw new QueryException(e.getMessage(), e);
        }
        catch (Error e) {
        	System.err.println(e.getMessage());
            throw new QueryParseException(e.getMessage(), e, -1, -1);
        }
        catch (Throwable t) {
        	Log.warn(ParserSPARQLStar.class, "Unexpected throwable: ", t);
            throw new QueryException(t.getMessage(), t);
        }

        return query ;
    }
}
