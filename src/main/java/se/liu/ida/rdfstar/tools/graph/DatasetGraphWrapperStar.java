package se.liu.ida.rdfstar.tools.graph;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.shared.AddDeniedException;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphWrapper;
import org.apache.jena.sparql.core.Quad;

/**
 * An implementation of {@link DatasetGraph} that wraps another {@link DatasetGraph} and
 * applies redundancy augmentation when nested triples are added. Redundancy
 * augmentation means that triples that are contained in a nested triple are
 * also explicitly added to the graph.
 *   
 * @author Robin Keskisärkkä
 */
public class DatasetGraphWrapperStar extends DatasetGraphWrapper
{
	public DatasetGraphWrapperStar(DatasetGraph ds )
	{
		super( checkIsEmpty(ds) );
	}

	static public DatasetGraph checkIsEmpty( DatasetGraph ds ) throws IllegalArgumentException
	{
		if ( ! ds.isEmpty() )
			throw new IllegalArgumentException("The dataset to be wrapped is not empty.");

		return ds;
	}

	@Override
    public void add( Quad q ) throws AddDeniedException
    {
		super.add(q);

		final Triple subjectTriple = RDFStarUtils.getSubjectTriple(q.asTriple());
		if ( subjectTriple != null ) {
			add(new Quad(q.getGraph(), subjectTriple));
		}

		final Triple objectTriple = RDFStarUtils.getObjectTriple(q.asTriple());
		if ( objectTriple != null ) {
			add(new Quad(q.getGraph(), objectTriple));
		}
    }

	@Override
	public void add(Node g, Node s, Node p, Node o) {
		add(new Quad(g, s, p, o));
	}


}
