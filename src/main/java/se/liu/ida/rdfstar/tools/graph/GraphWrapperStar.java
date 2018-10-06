package se.liu.ida.rdfstar.tools.graph;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Triple;
import org.apache.jena.shared.AddDeniedException;
import org.apache.jena.sparql.graph.GraphWrapper;

/**
 * An implementation of {@link Graph} that wraps another {@link Graph} and
 * applies redundancy augmentation when nested triples are added. Redundancy
 * augmentation means that triples that are contained in a nested triple are
 * also explicitly added to the graph.
 *   
 * @author Olaf Hartig
 */
public class GraphWrapperStar extends GraphWrapper
{
	public GraphWrapperStar( Graph g )
	{
		super( checkIsEmpty(g) );
	}

	static public Graph checkIsEmpty( Graph g ) throws IllegalArgumentException
	{
		if ( ! g.isEmpty() )
			throw new IllegalArgumentException("The graph to be wrapped is not empty.");

		return g;
	}

	@Override
    public void add( Triple t ) throws AddDeniedException
    {
		super.add(t);

		final Triple subjectTriple = RDFStarUtils.getSubjectTriple(t);
		if ( subjectTriple != null )
			add(subjectTriple);

		final Triple objectTriple = RDFStarUtils.getObjectTriple(t);
		if ( objectTriple != null )
			add(objectTriple);
    }

}
