package se.liu.ida.rdfstar.tools.rspqlstar.util;

import org.apache.jena.atlas.logging.Log;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Literal;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.serializer.SerializationContext;
import org.apache.jena.sparql.util.FmtUtils;

/**
 * Extension of FmtUtil for formatting Node_Triple using the SPARQL* syntax.
 */

public class MyFmtUtils extends FmtUtils {

    public static void stringForNode(StringBuilder result, Node n, SerializationContext context) {

        if ( n == null ) {
            result.append("<<null>>") ;
            return ;
        }

        // mappable?
        if ( context != null && context.getBNodeMap() != null ) {
            String str = context.getBNodeMap().asString(n) ;
            if ( str != null ) {
                result.append(str) ;
                return ;
            }
        }

        if ( n.isBlank() ) {
            result.append("_:").append(n.getBlankNodeLabel()) ;
        } else if ( n.isLiteral() ) {
            stringForLiteral(result, (Node_Literal)n, context) ;
        } else if ( n.isURI() ) {
            String uri = n.getURI() ;
            stringForURI(result, uri, context) ;
        } else if ( n.isVariable() ) {
            result.append("?").append(n.getName()) ;
        } else if ( n.equals(Node.ANY) ) {
            result.append("ANY") ;
        } else if ( n instanceof Node_Triple ) {
            Triple t = ((Node_Triple) n).get();
            result.append("<<") ;
            stringForNode(result, t.getSubject(), context) ;
            result.append(" ") ;
            stringForNode(result, t.getPredicate(), context) ;
            result.append(" ") ;
            stringForNode(result, t.getObject(), context) ;
            result.append(">>");
        } else {
            Log.warn(FmtUtils.class, "Failed to turn a node into a string: " + n) ;
            result.append(n.toString()) ;
        }
    }
    public static String stringForNode(Node n, SerializationContext context)
    {
        StringBuilder sb = new StringBuilder(  );
        stringForNode( sb, n, context );
        return sb.toString();
    }
}
