package se.liu.ida.rdfstar.tools.parser.system;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.riot.RiotException;
import org.apache.jena.riot.system.*;
import org.apache.jena.riot.tokens.Token;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.util.Context;
import org.apache.jena.sparql.util.FmtUtils;
import se.liu.ida.rdfstar.tools.parser.tokens.EmbeddedTripleTokenUtils;

/**
 * A {@link ParserProfileTrigStar} implemented as an extension of {@link ParserProfileStd}.
 *   
 * @author Robin Keskisärkkä
 */
public class ParserProfileTrigStarExtImpl extends ParserProfileStd implements ParserProfileTrigStar {
    public ParserProfileTrigStarExtImpl(FactoryRDF factory, ErrorHandler errorHandler,
                                        IRIResolver resolver, PrefixMap prefixMap,
                                        Context context, boolean checking, boolean strictMode) {
        super(factory, errorHandler, resolver, prefixMap, context, checking, strictMode);
    }

    @Override
    public Node createNodeFromToken(Node scope, Token token, long line, long col) {
        if (EmbeddedTripleTokenUtils.isEmbeddedTripleToken(token))
            return Helper.createTripleNodeFromEmbeddedTripleToken(this, scope, token, line, col);
        else
            return super.createNodeFromToken(scope, token, line, col);
    }

    @Override
    protected void checkTriple(Node subject, Node predicate, Node object, long line, long col) {
        if (subject == null
                || (!subject.isURI() &&
                !subject.isBlank() &&
                !(subject instanceof Node_Triple))) {
            getErrorHandler().error("Subject is not a URI, blank node, or triple", line, col);
            throw new RiotException("Bad subject: " + subject);
        }
        if (predicate == null || (!predicate.isURI())) {
            getErrorHandler().error("Predicate not a URI", line, col);
            throw new RiotException("Bad predicate: " + predicate);
        }
        if (object == null
                || (!object.isURI() &&
                !object.isBlank() &&
                !object.isLiteral() &&
                !(object instanceof Node_Triple))) {
            getErrorHandler().error("Object is not a URI, blank node, literal, or triple", line, col);
            throw new RiotException("Bad object: " + object);
        }
    }

    @Override
    protected void checkQuad(Node graph, Node subject, Node predicate, Node object, long line, long col) {
        // Allow blank nodes - syntax may restrict more.
        if (graph != null && !graph.isURI() && !graph.isBlank()) {
            throw new RiotException("Bad graph name: " + graph);
        }
        checkTriple(subject, predicate, object, line, col);
    }
}