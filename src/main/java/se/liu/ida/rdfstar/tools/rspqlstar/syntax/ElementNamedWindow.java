package se.liu.ida.rdfstar.tools.rspqlstar.syntax;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementNamedGraph;

/**
 * The ElementNamedWindow is created as an extension of ElementNamedGraph.
 */
public class ElementNamedWindow extends ElementNamedGraph {

    public ElementNamedWindow(Node windowName, Element el){
        super(windowName, el);
    }

    public Node getWindowName(){
        return getGraphNameNode();
    }
}


