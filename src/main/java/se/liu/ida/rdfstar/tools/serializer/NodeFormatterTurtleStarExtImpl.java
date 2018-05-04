package se.liu.ida.rdfstar.tools.serializer;

import org.apache.jena.atlas.io.AWriter;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.riot.out.NodeFormatterTTL_MultiLine;
import org.apache.jena.riot.out.NodeToLabel;
import org.apache.jena.riot.system.PrefixMap;

/**
 * A {@link NodeFormatterTurtleStar} implemented as
 * an extension of {@link NodeFormatterTTL_MultiLine}.
 *   
 * @author Olaf Hartig http://olafhartig.de/
 */
public class NodeFormatterTurtleStarExtImpl extends NodeFormatterTTL_MultiLine implements NodeFormatterTurtleStar
{
    public NodeFormatterTurtleStarExtImpl(String baseIRI, PrefixMap prefixMap) {
        super(baseIRI, prefixMap) ;
    }

    public NodeFormatterTurtleStarExtImpl(String baseIRI, PrefixMap prefixMap, NodeToLabel nodeToLabel) {
        super(baseIRI, prefixMap, nodeToLabel);
    }

    @Override
    public void format(AWriter w, Node n) {
    	if ( n instanceof Node_Triple )
    		Helper.format(w, (Node_Triple) n, this);
    	else
    		super.format(w, n);
    }
}
