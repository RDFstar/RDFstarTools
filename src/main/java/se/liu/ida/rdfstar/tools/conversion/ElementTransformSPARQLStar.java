package se.liu.ida.rdfstar.tools.conversion;

import java.util.HashMap;
import java.util.Random;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.ARQConstants;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementBind;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.syntaxtransform.ElementTransform;
import org.apache.jena.sparql.syntax.syntaxtransform.ElementTransformCopyBase;
import org.apache.jena.vocabulary.RDF;

/**
 * The {@link ElementTransform} used by {@link SPARQLStar2SPARQL}.
 * 
 * @author Jesper Eriksson
 * @author Amir Hakim
 * @author Olaf Hartig
 */
public class ElementTransformSPARQLStar extends ElementTransformCopyBase
{
	final protected HashMap<Triple,Node> doneNested = new HashMap<Triple,Node>();
	final protected int randomVarID = new Random().nextInt();
	protected int anonVarCounter = 0;

	@Override
	public Element transform( ElementPathBlock el )
	{
		final ElementPathBlock epb = new ElementPathBlock();

		for ( TriplePath tp : el.getPattern() ) {
			unNestTriplePattern(tp.asTriple(), epb, false);
		}

		return epb;
	}
	
	@Override
	public Element transform( ElementBind eb, Var v, Expr expr2 )
	{
		if (    ( eb.getExpr() instanceof NodeValue )
		     && ( ((NodeValue) eb.getExpr()).getNode() instanceof Node_Triple ) )
		{
			final NodeValue nv = (NodeValue) eb.getExpr();
			final Node_Triple nt = (Node_Triple) nv.getNode();
			final Triple tp = ( (Node_Triple) nt ).get();

			final ElementPathBlock epb = new ElementPathBlock();
			unNestBindClause(tp, epb, v);
			return epb;
		}
		else
			return super.transform(eb, v, expr2);
	}


	// --- helper methods ---

	protected Node unNestTriplePattern( Triple tp, ElementPathBlock epb, boolean hasParent )
	{	
		Node s = tp.getSubject();
		Node p = tp.getPredicate();
		Node o = tp.getObject();

		if ( s instanceof Node_Triple )
		{
			final Triple sTP = ( (Node_Triple) s ).get();
			s = unNestTriplePattern(sTP, epb, true);
		}

		if ( o instanceof Node_Triple )
		{
			final Triple oTP = ( (Node_Triple) o ).get();
			o = unNestTriplePattern(oTP, epb, true);
		}

		final Triple nonnestedTP = new Triple(s, p, o);
		final boolean seenBefore = doneNested.containsKey(nonnestedTP);

		final Node var;
		if ( seenBefore ) {
			var = doneNested.get(nonnestedTP);
		}
		else
		{
			var = createFreshAnonVarForReifiedTriple();
			epb.addTriple(nonnestedTP);

			if ( hasParent ) {
				epb.addTriple( new Triple(var, RDF.Nodes.type,      RDF.Nodes.Statement) );
				epb.addTriple( new Triple(var, RDF.Nodes.subject,   s) );
				epb.addTriple( new Triple(var, RDF.Nodes.predicate, p) );
				epb.addTriple( new Triple(var, RDF.Nodes.object,    o) );
				doneNested.put(nonnestedTP, var);
			}
		}

		return var;
	}

	protected void unNestBindClause( Triple tp, ElementPathBlock epb, Var var )
	{
		Node s = tp.getSubject();
		Node p = tp.getPredicate();
		Node o = tp.getObject();

		if ( s instanceof Node_Triple )
		{
			final Triple sTP = ( (Node_Triple) s ).get();
			s = unNestTriplePattern(sTP, epb, true);
		}

		if ( o instanceof Node_Triple )
		{
			final Triple oTP = ( (Node_Triple) o ).get();
			o = unNestTriplePattern(oTP, epb, true);
		}

		final Triple nonnestedTP = new Triple(s, p, o);

		if ( ! doneNested.containsKey(nonnestedTP) )
		{
			epb.addTriple(nonnestedTP);
			epb.addTriple( new Triple(var, RDF.Nodes.type,      RDF.Nodes.Statement) );
			epb.addTriple( new Triple(var, RDF.Nodes.subject,   s) );
			epb.addTriple( new Triple(var, RDF.Nodes.predicate, p) );
			epb.addTriple( new Triple(var, RDF.Nodes.object,    o) );
			doneNested.put(nonnestedTP, var);
		}
	}

	protected Node createFreshAnonVarForReifiedTriple()
	{
		final String varName = ARQConstants.allocVarAnonMarker + randomVarID + anonVarCounter++;
		return NodeFactory.createVariable( varName );
	}

}
