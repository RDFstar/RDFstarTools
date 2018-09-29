package se.liu.ida.rdfstar.tools.parser.system;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.riot.RiotException;
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

    @Override
    protected void checkTriple(Node subject, Node predicate, Node object, long line, long col) {
    	if ( subject == null
    	     || (!subject.isURI() && 
    	         !subject.isBlank() &&
    	         !(subject instanceof Node_Triple)) ) {
        	getErrorHandler().error("Subject is not a URI, blank node, or triple", line, col);
            throw new RiotException("Bad subject: " + subject);
        }
        if ( predicate == null || (!predicate.isURI()) ) {
        	getErrorHandler().error("Predicate not a URI", line, col);
            throw new RiotException("Bad predicate: " + predicate);
        }
        if ( object == null
             || (!object.isURI() &&
                 !object.isBlank() &&
                 !object.isLiteral() &&
                 !(object instanceof Node_Triple)) ) {
        	getErrorHandler().error("Object is not a URI, blank node, literal, or triple", line, col);
            throw new RiotException("Bad object: " + object);
        }
    }

}
