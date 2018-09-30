package se.liu.ida.rdfstar.tools.sparqlstar.engine.iterator;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.ARQInternalErrorException;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.binding.BindingFactory;
import org.apache.jena.sparql.engine.binding.BindingMap;
import org.apache.jena.sparql.engine.iterator.QueryIter;
import org.apache.jena.sparql.engine.iterator.QueryIterRepeatApply;
import org.apache.jena.sparql.engine.iterator.QueryIterTriplePattern;
import org.apache.jena.util.iterator.ClosableIterator;
import org.apache.jena.util.iterator.NiceIterator;

import se.liu.ida.rdfstar.tools.sparqlstar.core.ExtendedSubstitute;

/**
 * A SPARQL*-aware version of {@link QueryIterTriplePattern}.
 * 
 * @author Olaf Hartig
 */
public class QueryIterTripleStarPattern extends QueryIterRepeatApply
{
	static public QueryIterator create( QueryIterator input,
                                        Triple tp,
                                        ExecutionContext cxt )
	{
		if ( tp.getSubject() instanceof Node_Triple || tp.getObject() instanceof Node_Triple )
			return new QueryIterTripleStarPattern(input, tp, cxt);
		else
			return new QueryIterTriplePattern(input, tp, cxt);
	}

    final protected Triple tp;
    
    protected QueryIterTripleStarPattern( QueryIterator input,
                                          Triple tp,
                                          ExecutionContext cxt )
    {
        super(input, cxt);
        this.tp = tp;
    }

    @Override
    protected QueryIterator nextStage( Binding binding )
    {
        return new TripleMapper( binding, tp, getExecContext() );
    }

    static protected class TripleMapper extends QueryIter
    {
    	final protected Node s;
    	final protected Node p;
    	final protected Node o;

    	final protected boolean sIsTripleWithVars;
    	final protected boolean oIsTripleWithVars;
    	
    	final protected Binding binding;
    	protected ClosableIterator<Triple> graphIter;
    	protected Binding slot = null;
    	protected boolean finished = false;
    	protected volatile boolean cancelled = false;

        public TripleMapper( Binding binding, Triple tp, ExecutionContext cxt )
        {
            super(cxt) ;
            this.s = ExtendedSubstitute.substitute( tp.getSubject(),   binding );
            this.p = ExtendedSubstitute.substitute( tp.getPredicate(), binding );
            this.o = ExtendedSubstitute.substitute( tp.getObject(),    binding );
            this.sIsTripleWithVars = isTripleWithVars(s);
            this.oIsTripleWithVars = isTripleWithVars(o);
            this.binding = binding;

            final Node s2 = sIsTripleWithVars ? Node.ANY : tripleNode(s);
            final Node p2 = tripleNode(p);
            final Node o2 = oIsTripleWithVars ? Node.ANY : tripleNode(o);

            final Graph graph = cxt.getActiveGraph();
            this.graphIter = graph.find(s2, p2, o2);
        }

        static protected Node tripleNode( Node node )
        {
            if ( node.isVariable() )
                return Node.ANY;
            return node;
        }

        static protected boolean isTripleWithVars( Node node )
        {
        	if ( !(node instanceof Node_Triple) )
        		return false;

        	final Triple t = ( (Node_Triple) node ).get();

        	if ( Var.isVar(t.getSubject()) || Var.isVar(t.getPredicate()) || Var.isVar(t.getObject()) )
        		return true;

        	if ( isTripleWithVars(t.getSubject()) || isTripleWithVars(t.getObject()) )
        		return true;

        	return false;
        }

        protected Binding mapper(Triple r)
        {
            final BindingMap results = BindingFactory.create(binding);

            if ( ! insert(r, s, p, o, results, sIsTripleWithVars, oIsTripleWithVars) )
                return null;

            return results;
        }

        static protected boolean insert( Triple r,
                                         Node s, Node p, Node o,
                                         BindingMap results,
                                         boolean sIsTripleWithVars,
                                         boolean oIsTripleWithVars )
        {
            if ( ! insert(s, r.getSubject(), results, sIsTripleWithVars) )
                return false;
            if ( ! insert(p, r.getPredicate(), results, false) )
                return false;
            if ( ! insert(o, r.getObject(), results, oIsTripleWithVars) )
                return false;

            return true;
        }

        static protected boolean insert( Triple r,
                                         Node s, Node p, Node o,
                                         BindingMap results )
        {
        	return insert( r, s,  p,  o, results, isTripleWithVars(s), isTripleWithVars(o) );
        }

        static protected boolean insert( Node inputNode,
                                         Node outputNode,
                                         BindingMap results,
                                         boolean inputNodeIsTripleWithVars )
        {
        	if ( inputNodeIsTripleWithVars )
            {
        		if ( !(outputNode instanceof Node_Triple) )
        			return false;

        		final Triple outputTriple = ( (Node_Triple) outputNode ).get();
        		final Triple inputTriple  = ( (Node_Triple) inputNode  ).get();

        		return insert(outputTriple,
        		              inputTriple.getSubject(),
        		              inputTriple.getPredicate(),
        		              inputTriple.getObject(),
        		              results );
            }
            else if ( Var.isVar(inputNode) )
            {
            	final Var v = Var.alloc(inputNode);
                final Node x = results.get(v);
                if ( x != null )
                    return outputNode.equals(x);

                results.add(v, outputNode);
                return true;
            }
            else
            	return true;
        }
        
        @Override
        protected boolean hasNextBinding()
        {
            if ( finished ) return false;
            if ( slot != null ) return true;
            if ( cancelled ) {
                graphIter.close();
                finished = true;
                return false;
            }

            while( graphIter.hasNext() && slot == null )
            {
                final Triple t = graphIter.next();
                slot = mapper(t);
            }

            if ( slot == null )
                finished = true;

            return slot != null;
        }

        @Override
        protected Binding moveToNextBinding()
        {
            if ( ! hasNextBinding() ) 
                throw new ARQInternalErrorException();

            final Binding r = slot;
            slot = null;
            return r;
        }

        @Override
        protected void closeIterator()
        {
            if ( graphIter != null )
                NiceIterator.close(graphIter);
            graphIter = null;
        }

        @Override
        protected void requestCancel() { cancelled = true; }

    } // end of class TripleMapper

}
