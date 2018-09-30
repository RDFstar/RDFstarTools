package se.liu.ida.rdfstar.tools.sparqlstar.engine.main;

import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.iterator.QueryIterPeek;
import org.apache.jena.sparql.engine.main.StageGenerator;
import org.apache.jena.sparql.engine.main.StageGeneratorGeneric;
import org.apache.jena.sparql.engine.optimizer.reorder.ReorderProc;
import org.apache.jena.sparql.engine.optimizer.reorder.ReorderTransformation;
import org.apache.jena.sparql.mgt.Explain;

import se.liu.ida.rdfstar.tools.sparqlstar.core.ExtendedSubstitute;
import se.liu.ida.rdfstar.tools.sparqlstar.engine.iterator.QueryIterTripleStarPattern;

/**
 * 
 * @author Olaf Hartig
 */
public class StageGeneratorSPARQLStar extends StageGeneratorGeneric
{
	@Override
	protected QueryIterator execute(BasicPattern pattern,
	                                ReorderTransformation reorder,
	                                StageGenerator execution,
	                                QueryIterator input,
	                                ExecutionContext execCxt)
	{
		// The implementation of this method is a copy of the
		// superclass method that is modified to use SPARQL*-aware
		// versions of Substitute and QueryIterBlockTriples.

        Explain.explain(pattern, execCxt.getContext());

        if ( ! input.hasNext() )
            return input;

        if ( reorder != null && pattern.size() >= 2 ) {
            // If pattern size is 0 or 1, nothing to do.
            BasicPattern bgp2 = pattern;

            // Try to ground the pattern
            if ( ! input.isJoinIdentity() ) {
                QueryIterPeek peek = QueryIterPeek.create(input, execCxt);
                // And now use this one
                input = peek;
                Binding b = peek.peek();
                bgp2 = ExtendedSubstitute.substitute(pattern, b);
            }
            ReorderProc reorderProc = reorder.reorderIndexes(bgp2);
            pattern = reorderProc.reorder(pattern);
        }
        Explain.explain("Reorder/generic", pattern, execCxt.getContext());

        QueryIterator chain = input;
        for ( final Triple triple : pattern )
            chain = QueryIterTripleStarPattern.create(chain, triple, execCxt);
        return chain;
	}

}
