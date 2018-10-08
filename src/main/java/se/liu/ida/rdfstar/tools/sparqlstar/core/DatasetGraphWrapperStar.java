package se.liu.ida.rdfstar.tools.sparqlstar.core;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphWrapper;
import org.apache.jena.sparql.core.Quad;

import se.liu.ida.rdfstar.tools.graph.GraphWrapperStar;

/**
 * An implementation of {@link DatasetGraph} that wraps
 * another {@link DatasetGraph} for which it makes sure
 * that every {@link Graph} in this dataset is turned
 * into a {@link GraphWrapperStar}.
 *   
 * @author Olaf Hartig
 */
public class DatasetGraphWrapperStar extends DatasetGraphWrapper
{
	public DatasetGraphWrapperStar( DatasetGraph dsg )
	{
		super( checkIsEmpty(dsg) );
	}

	static public DatasetGraph checkIsEmpty( DatasetGraph dsg ) throws IllegalArgumentException
	{
		if ( ! dsg.isEmpty() )
			throw new IllegalArgumentException("The dataset to be wrapped is not empty.");

		return dsg;
	}

    @Override
    public void addGraph( Node graphName, Graph graph )
    {
    	if ( !(graph instanceof GraphWrapperStar) )
			throw new IllegalArgumentException( "The named graph to be added to this dataset is not of type GraphWrapperStar (it is of type " + graph.getClass() + "instead)." );

    	super.addGraph( graphName, graph );
/*
    	if ( graph instanceof GraphWrapperStar )
    		super.addGraph( graphName, graph );
    	else
    		super.addGraph( graphName, new GraphWrapperStar(graph) );
*/    		
    }

    @Override
    public void setDefaultGraph( Graph g )
    {
    	if ( !(g instanceof GraphWrapperStar) )
			throw new IllegalArgumentException( "The graph to be set as default graph is not of type GraphWrapperStar (it is of type " + g.getClass() + "instead)." );

    	super.setDefaultGraph( g );
/*
    	if ( g instanceof GraphWrapperStar )
    		super.setDefaultGraph( g );
    	else
    		super.setDefaultGraph( new GraphWrapperStar(g) );
*/
    }

    @Override
    public void add( Node g, Node s, Node p, Node o )
    {
    	super.add(g, s, p, o);

    	if ( s instanceof Node_Triple ) {
    		final Triple t = ( (Node_Triple) s ).get();
    		add( g, t.getSubject(), t.getPredicate(), t.getObject() );
    	}

    	if ( o instanceof Node_Triple ) {
    		final Triple t = ( (Node_Triple) o ).get();
    		add( g, t.getSubject(), t.getPredicate(), t.getObject() );
    	}
    }

    @Override
    public void add( Quad quad )
    {
    	super.add( quad );

    	if ( quad.getSubject() instanceof Node_Triple ) {
    		final Triple t = ( (Node_Triple) quad.getSubject() ).get();
    		add( quad.getGraph(), t.getSubject(), t.getPredicate(), t.getObject() );
    	}

    	if ( quad.getObject() instanceof Node_Triple ) {
    		final Triple t = ( (Node_Triple) quad.getObject() ).get();
    		add( quad.getGraph(), t.getSubject(), t.getPredicate(), t.getObject() );
    	}
    }

}
