package se.liu.ida.rdfstar.tools.sparqlstar.resultset;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.core.Prologue;
import org.apache.jena.sparql.resultset.TextOutput;
import org.apache.jena.sparql.serializer.SerializationContext;
import org.apache.jena.sparql.util.FmtUtils;

/**
 * A version of {@link TextOutput} that can deal with
 * {@link Node} objects of type {@link Node_Triple}.
 * 
 * @author Olaf Hartig http://olafhartig.de/
 */
public class TextOutputStar extends TextOutput
{
    public TextOutputStar(Prologue prologue) { super(prologue); }

    public TextOutputStar(PrefixMapping pMap) { super(pMap); }

    public TextOutputStar(SerializationContext cxt) { super(cxt); }

    @Override
    protected String getVarValueAsString(QuerySolution rBind, String varName) {
        final RDFNode obj = rBind.get(varName);

        if ( obj == null ) {
            return super.getVarValueAsString(rBind, varName);
        }
        else if ( obj.asNode() instanceof Node_Triple ) {
        	final Triple t = ( (Node_Triple) obj.asNode() ).get();
        	return getAsString(t);
        }
        else {
        	return FmtUtils.stringForRDFNode(obj, context);
        }
    }

    protected String getAsString( Triple t ) {
    	//String result = "<< ";
    	String result = "";

    	final Node s = t.getSubject();
    	if ( s instanceof Node_Triple ) {
        	final Triple st = ( (Node_Triple) s ).get();
    		//result += getAsString(st) + " ";
    		result += "<< " + getAsString(st) + ">> ";
    	}
    	else
    		result += FmtUtils.stringForNode(s, context) + " ";

    	result += FmtUtils.stringForNode(t.getPredicate(), context) + " ";

    	final Node o = t.getObject();
    	if ( o instanceof Node_Triple ) {
        	final Triple ot = ( (Node_Triple) o ).get();
    		//result += getAsString(ot) + " ";
    		result += "<< " + getAsString(ot) + ">> ";
    	}
    	else
    		result += FmtUtils.stringForNode(o, context) + " ";

    	//result += ">>";
    	return result;
    }

}
