package se.liu.ida.rdfstar.tools.serializer;

import org.apache.jena.atlas.io.AWriter;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.riot.out.NodeFormatter;

/**
 * A {@link NodeFormatterTurtleStar} implemented as
 * a wrapper of some other {@link NodeFormatter}.
 *   
 * @author Olaf Hartig http://olafhartig.de/
 */

public class NodeFormatterTurtleStarWrapperImpl implements NodeFormatterTurtleStar
{
	final protected NodeFormatter wrappedFormatter;

	public NodeFormatterTurtleStarWrapperImpl(NodeFormatter wrappedFormatter) {
		this.wrappedFormatter = wrappedFormatter;
	}

	@Override
    public void format(AWriter w, Node n) {
    	if ( n instanceof Node_Triple )
    		Helper.format(w, (Node_Triple) n, this);
    	else
    		wrappedFormatter.format(w, n);
	}

	@Override
    public void formatURI(AWriter w, Node n) {
		wrappedFormatter.formatURI(w, n);
	}

	@Override
    public void formatURI(AWriter w, String uriStr) {
		wrappedFormatter.formatURI(w, uriStr);
	}

	@Override
    public void formatVar(AWriter w, Node n) {
		wrappedFormatter.formatVar(w, n);
	}

	@Override
    public void formatVar(AWriter w, String name) {
		wrappedFormatter.formatVar(w, name);
	}

	@Override
    public void formatBNode(AWriter w, Node n) {
		wrappedFormatter.formatBNode(w, n);
	}

	@Override
    public void formatBNode(AWriter w, String label) {
		wrappedFormatter.formatBNode(w, label);
	}

	@Override
    public void formatLiteral(AWriter w, Node n) {
		wrappedFormatter.formatLiteral(w, n);
	}

	@Override
    public void formatLitString(AWriter w, String lex) {
		wrappedFormatter.formatLitString(w, lex);
	}

	@Override
    public void formatLitLang(AWriter w, String lex, String langTag) {
		wrappedFormatter.formatLitLang(w, lex, langTag);
	}

	@Override
    public void formatLitDT(AWriter w, String lex, String datatypeURI) {
		wrappedFormatter.formatLitDT(w, lex, datatypeURI);
	}
}
