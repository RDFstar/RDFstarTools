package se.liu.ida.rdfstar.tools.rspqlstar.lang;

import org.apache.jena.atlas.logging.Log;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryException;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.shared.JenaException;
import org.apache.jena.sparql.lang.SPARQLParser;
import se.liu.ida.rdfstar.tools.rspqlstar.lang.rspqlstar.ParseException;
import se.liu.ida.rdfstar.tools.rspqlstar.lang.rspqlstar.RSPQLStarParser11;
import se.liu.ida.rdfstar.tools.rspqlstar.lang.rspqlstar.TokenMgrError;

import java.io.Reader;
import java.io.StringReader;

/**
 * Parser for SPARQL*.
 * 
 * The parser may be used either by simply calling its method
 * {@link #parse(Query, String)} or by registering it in the
 * Jena query parser framework, which can be done by calling
 * {@link RSPQLStar#init()}.
 *  
 * @author Robin Keskisärkkä
 */
public class ParserRSPQLStar extends SPARQLParser
{
    @Override
    protected Query parse$(final Query query, String queryString)
    {
        query.setSyntax(RSPQLStar.syntax);

        final Reader in = new StringReader(queryString);
        final RSPQLStarParser11 parser = new RSPQLStarParser11(in);

        try {
            query.setStrict(true);
            parser.setQuery(new RSPQLStarQuery(query));

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
        	Log.warn(ParserRSPQLStar.class, "Unexpected throwable: ", t);
            throw new QueryException(t.getMessage(), t);
        }

        return parser.getQuery();
    }

}
