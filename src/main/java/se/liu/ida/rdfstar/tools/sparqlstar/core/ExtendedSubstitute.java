package se.liu.ida.rdfstar.tools.sparqlstar.core;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.Substitute;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.engine.binding.Binding;

import se.liu.ida.rdfstar.tools.sparqlstar.lang.Node_TripleStarPattern;

/**
 * A SPARQL*-aware extension of {@link Substitute}.
 * 
 * @author Olaf Hartig
 */
public class ExtendedSubstitute extends Substitute
{
    public static BasicPattern substitute( BasicPattern bgp, Binding binding )
    {
        if ( binding == null || binding.isEmpty() )
            return bgp;

        final BasicPattern bgp2 = new BasicPattern();
        for ( final Triple triple : bgp ) {
            final Triple t = substitute(triple, binding);
            bgp2.add(t);
        }
        return bgp2;
    }

    public static Triple substitute( Triple triple, Binding binding )
    {
    	if ( binding == null || binding.isEmpty() )
            return triple;

    	final Node s = triple.getSubject();
    	final Node p = triple.getPredicate();
    	final Node o = triple.getObject();

    	final Node s1 = substitute(s, binding);
    	final Node p1 = substitute(p, binding);
    	final Node o1 = substitute(o, binding);

        Triple t = triple;
        if ( s1 != s || p1 != p || o1 != o )
            t = new Triple(s1, p1, o1);
        return t;
    }

    public static TriplePath substitute( TriplePath triplePath, Binding binding )
    {
        if ( triplePath.isTriple() ) {
        	final Triple t1 = triplePath.asTriple();
        	final Triple t2 = substitute(t1, binding);

        	if ( t1 == t2 )
        		return triplePath;
        	else
        		return new TriplePath(t2);
        }

        final Node s = triplePath.getSubject();
        final Node o = triplePath.getObject();
        final Node s1 = substitute(s, binding);
        final Node o1 = substitute(o, binding);

        TriplePath tp = triplePath;
        if ( s1 != s || o1 != o )
            tp = new TriplePath(s1, triplePath.getPath(), o1);
        return tp;
    }

    public static Node substitute( Node n, Binding b )
    {
    	if ( n instanceof Node_Triple )
    	{
    		final Triple t1 = ((Node_Triple) n).get();
    		final Triple t2 = substitute(t1, b);

    		if ( t1 == t2 )
    			return n;
    		else
    			return new Node_TripleStarPattern(t2);
    	}
    	else
    		return Substitute.substitute(n, b);
    }

}
