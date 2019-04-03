package se.liu.ida.rdfstar.tools.rspqlstar.serializer;

import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.serializer.FormatterElement;
import org.apache.jena.sparql.serializer.SerializationContext;
import org.apache.jena.sparql.syntax.*;
import se.liu.ida.rdfstar.tools.rspqlstar.syntax.ElementNamedWindow;
import se.liu.ida.rdfstar.tools.rspqlstar.util.MyFmtUtils;

/**
 * Wrapper to expose the protected serialization context variable and slotToString method.
 */

public class MyFormatterElement extends FormatterElement  {
    public MyFormatterElement(IndentedWriter out, SerializationContext context) {
        super(out, context);
    }

    public SerializationContext getContext(){
        return context;
    }

    protected String slotToString(Node n){
        return MyFmtUtils.stringForNode(n, context) ;
    }

    public void visit(ElementNamedWindow el) {
        visitNodePattern("WINDOW", el.getWindowName(), el.getElement());
    }

    // This has been modified to handle both window clauses and graph clauses
    // The alternative is to extend ElementVisitor and also to install an extension
    // of the element walker.
    @Override
    public void visit(ElementNamedGraph el) {
        if(el instanceof ElementNamedWindow){
            visit(((ElementNamedWindow) el));
        } else {
            visitNodePattern("GRAPH", el.getGraphNameNode(), el.getElement());
        }
    }


    private void visitNodePattern(String label, Node node, Element subElement) {
        int len = label.length();
        out.print(label);
        out.print(" ");
        String nodeStr = (node == null) ? "*" : slotToString(node);
        out.print(nodeStr);
        len += nodeStr.length();
        if ( GRAPH_FIXED_INDENT ) {
            out.incIndent(INDENT);
            out.newline(); // NB and newline
        } else {
            out.print(" ");
            len++;
            out.incIndent(len);
        }
        visitAsGroup(subElement);

        if ( GRAPH_FIXED_INDENT )
            out.decIndent(INDENT);
        else
            out.decIndent(len);
    }

    public void visitAsGroup(Element el) {
        boolean needBraces = !((el instanceof ElementGroup) || (el instanceof ElementSubQuery));

        if ( needBraces ) {
            out.print("{ ");
            out.incIndent(INDENT);
        }
        el.visit(this);

        if ( needBraces ) {
            out.decIndent(INDENT);
            out.print("}");
        }
    }
}
