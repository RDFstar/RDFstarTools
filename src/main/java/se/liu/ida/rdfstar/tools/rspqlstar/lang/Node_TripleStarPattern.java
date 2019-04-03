package se.liu.ida.rdfstar.tools.rspqlstar.lang;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Triple;

/**
 * Extends {@link Node_Triple} to correctly implement {@link Node#isConcrete()},
 * namely, by returning true if and only if each of the three components of the
 * triple wrapped by this {@link Node_Triple} is concrete.  
 * 
 * @author Olaf Hartig
 */
public class Node_TripleStarPattern extends Node_Triple
{
    static public Node_TripleStarPattern asNode_TripleStarPattern(Node_Triple n )
    {
    	if ( n instanceof Node_TripleStarPattern)
    		return (Node_TripleStarPattern) n;

    	return new Node_TripleStarPattern( n.get() );
    }

	static public Triple asTripleWithNode_TripleStarPatterns( Triple t )
	{
		final Node s = t.getSubject();
		final Node s2;
		if ( s instanceof Node_Triple )
			s2 = asNode_TripleStarPattern( (Node_Triple) s );
		else
			s2 = s;

		final Node o = t.getObject();
		final Node o2;
		if ( o instanceof Node_Triple )
			o2 = asNode_TripleStarPattern( (Node_Triple) o );
		else
			o2 = o;

		if ( s == s2 && o == o2 )
			return t;
		else
			return new Triple(s2, t.getPredicate(), o2);
	}


	final public boolean isConcrete;

	public Node_TripleStarPattern(Triple t )
	{
		super( asTripleWithNode_TripleStarPatterns(t) );

		isConcrete = t.getSubject().isConcrete() && t.getPredicate().isConcrete() && t.getObject().isConcrete();
	}

	@Override
	public boolean isConcrete() {
		return isConcrete;
	}

}
