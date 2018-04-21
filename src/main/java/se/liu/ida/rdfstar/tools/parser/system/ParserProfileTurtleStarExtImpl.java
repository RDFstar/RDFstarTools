package se.liu.ida.rdfstar.tools.parser.system;

import org.apache.jena.graph.Node;
import org.apache.jena.riot.system.ErrorHandler;
import org.apache.jena.riot.system.FactoryRDF;
import org.apache.jena.riot.system.IRIResolver;
import org.apache.jena.riot.system.ParserProfileStd;
import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.riot.tokens.Token;
import org.apache.jena.sparql.util.Context;

import se.liu.ida.rdfstar.tools.parser.tokens.EmbeddedTripleTokenUtils;

/**
 * A {@link ParserProfileTurtleStar} implemented
 * as an extension of {@link ParserProfileStd}.
 *   
 * @author Olaf Hartig http://olafhartig.de/
 */
public class ParserProfileTurtleStarExtImpl extends ParserProfileStd implements ParserProfileTurtleStar
{
    public ParserProfileTurtleStarExtImpl(FactoryRDF factory, ErrorHandler errorHandler, 
            IRIResolver resolver, PrefixMap prefixMap,
            Context context, boolean checking, boolean strictMode) {
    	super(factory,errorHandler,resolver,prefixMap,context, checking, strictMode );
    }

    @Override
    public Node createNodeFromToken(Node scope, Token token, long line, long col) {
    	if ( EmbeddedTripleTokenUtils.isEmbeddedTripleToken(token) )
    		return ParserProfileTurtleStar.Helper.createTripleNodeFromEmbeddedTripleToken(this, scope, token, line, col);
    	else
    		return super.createNodeFromToken(scope, token, line, col);
    }

}
