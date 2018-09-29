package se.liu.ida.rdfstar.tools.parser.system;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Triple;
import org.apache.jena.iri.IRI;
import org.apache.jena.riot.system.ErrorHandler;
import org.apache.jena.riot.system.FactoryRDF;
import org.apache.jena.riot.system.IRIResolver;
import org.apache.jena.riot.system.ParserProfile;
import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.riot.tokens.Token;
import org.apache.jena.sparql.core.Quad;

import se.liu.ida.rdfstar.tools.parser.tokens.EmbeddedTripleTokenUtils;

/**
 * A {@link ParserProfileTurtleStar} implemented as
 * a wrapper of some other {@link ParserProfile}.
 * 
 * @author Olaf Hartig http://olafhartig.de/
 */
public class ParserProfileTurtleStarWrapperImpl implements ParserProfileTurtleStar
{
	final protected ParserProfile profile;

	public ParserProfileTurtleStarWrapperImpl( final ParserProfile profile ) {
		this.profile = profile;
	}

    @Override
    public ErrorHandler getErrorHandler() {
        return profile.getErrorHandler();
    }

    @Override
    public boolean isStrictMode() {
        return profile.isStrictMode();
    }

    @Override
    public String resolveIRI(String uriStr, long line, long col) {
        return profile.resolveIRI(uriStr, line, col);
    }

    @Override
    public void setIRIResolver(IRIResolver resolver) {
    	profile.setIRIResolver(resolver); 
    }

    @Override
    public IRI makeIRI(String uriStr, long line, long col) {
        return profile.makeIRI(uriStr, line, col);
    }

    @Override
    public Triple createTriple(Node subject, Node predicate, Node object, long line, long col) {
    	final boolean sIsTriple = ( subject != null && subject instanceof Node_Triple );
    	final boolean oIsTriple = ( object  != null && object  instanceof Node_Triple );

    	if ( ! sIsTriple && ! oIsTriple )
    		return profile.createTriple(subject, predicate, object, line, col);
    	else
    		return getFactorRDF().createTriple(subject, predicate, object);
    }

    @Override
    public Quad createQuad(Node graph, Node subject, Node predicate, Node object, long line, long col) {
    	final boolean sIsTriple = ( subject != null && subject instanceof Node_Triple );
    	final boolean oIsTriple = ( object  != null && object  instanceof Node_Triple );

    	if ( ! sIsTriple && ! oIsTriple )
    		return profile.createQuad(graph, subject, predicate, object, line, col);
    	else
    		return getFactorRDF().createQuad(graph, subject, predicate, object);
    }

    @Override
    public Node createURI(String x, long line, long col) {
    	return profile.createURI(x, line, col);
    }

    @Override
    public Node createTypedLiteral(String lexical, RDFDatatype datatype, long line, long col) {
        return profile.createTypedLiteral(lexical, datatype, line, col);
    }

    @Override
    public Node createLangLiteral(String lexical, String langTag, long line, long col) {
        return profile.createLangLiteral(lexical, langTag, line, col);
    }

    @Override
    public Node createStringLiteral(String lexical, long line, long col) {
        return profile.createStringLiteral(lexical, line, col);
    }

    @Override
    public Node createNodeFromToken(Node scope, Token token, long line, long col) {
    	if ( EmbeddedTripleTokenUtils.isEmbeddedTripleToken(token) )
    		return ParserProfileTurtleStar.Helper.createTripleNodeFromEmbeddedTripleToken(this, scope, token, line, col);
    	else
    		return profile.createNodeFromToken(scope, token, line, col);
    }

    @Override
    public Node createBlankNode(Node scope, String label, long line, long col) {
        return profile.createBlankNode(scope, label, line, col);
    }

    @Override
    public Node createBlankNode(Node scope, long line, long col) {
        return profile.createBlankNode(scope, line, col);
    }

    @Override
    public Node create(Node currentGraph, Token token) {
    	if ( EmbeddedTripleTokenUtils.isEmbeddedTripleToken(token) )
    		return ParserProfileTurtleStar.Helper.createTripleNodeFromEmbeddedTripleToken(
    				      this, currentGraph, token, token.getLine(), token.getColumn());
    	else
    		return profile.create(currentGraph, token);
    }

    @Override
    public PrefixMap getPrefixMap() {
        return profile.getPrefixMap();
    }

	@Override
	public FactoryRDF getFactorRDF() {
		return profile.getFactorRDF();
	}

}
