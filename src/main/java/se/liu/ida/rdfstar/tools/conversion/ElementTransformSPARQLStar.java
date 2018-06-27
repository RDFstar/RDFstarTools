package se.liu.ida.rdfstar.tools.conversion;

import java.util.HashMap;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementBind;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.syntaxtransform.ElementTransformCopyBase;
import org.apache.jena.vocabulary.RDF;

/**
 * 
 * @author Jesper Eriksson
 * @author Amir Hakim
 */
public class ElementTransformSPARQLStar extends ElementTransformCopyBase
{
	final HashMap<Triple,Node> doneNested = new HashMap<Triple,Node>();

	@Override
	public Element transform(ElementPathBlock el)
	{
		final ElementPathBlock epb = new ElementPathBlock() ;
		for (TriplePath tp : el.getPattern()) {
			unNestTriplePattern(tp,epb,false);
		}

		return epb;
	}
	
	@Override
	public Element transform(ElementBind eb, Var v, Expr expr2)
	{
		final ElementPathBlock epb = new ElementPathBlock() ;
		final NodeValue nv = (NodeValue) eb.getExpr();
		final Node_Triple nt = (Node_Triple) nv.getNode();
		final Triple tTemp = ((Node_Triple)nt).get();
		final TriplePath tp = new TriplePath(tTemp);
		unNestBindClause(tp,epb,false, v);
		return epb;
	}

	// --- helper method ---

	public Node unNestTriplePattern(TriplePath triple, ElementPathBlock epb, boolean hasParent)
	{	
		Node s = triple.getSubject();
		Node p = triple.getPredicate();
		Node o = triple.getObject();

		if( s instanceof Node_Triple)
		{
			final Triple tTemp = ((Node_Triple)s).get();
			final TriplePath subTriple = new TriplePath(tTemp);
			s = unNestTriplePattern(subTriple,epb,true);
		}
		
		if( o instanceof Node_Triple)
		{
			final Triple tTemp = ((Node_Triple)o).get();
			final TriplePath objTriple = new TriplePath(tTemp);
			o = unNestTriplePattern(objTriple, epb,true);
		}
		
		Node var = NodeFactory.createBlankNode(); 
		final Triple t = new Triple(s,p,o);

		if(doneNested.get(t) == null)
		{
			epb.addTriple(t);
		}
		else
			var = doneNested.get(t);
		
		if(hasParent)
		{	
			if(doneNested.get(t) == null)
			{
				Node type = RDF.Nodes.type;
				final Node statement = RDF.Nodes.Statement;
			
				Triple t1 = new Triple(var,type, statement);
				epb.addTriple(t1);
			
				type = RDF.Nodes.subject;
				t1 = new Triple(var,type,s);
				epb.addTriple(t1);
				
				type = RDF.Nodes.predicate;
				t1 = new Triple(var,type,p);
				epb.addTriple(t1);
				
				type = RDF.Nodes.object;
				t1 = new Triple(var,type,o);
				epb.addTriple(t1);
				doneNested.put(t, var);
			}
		}

		return var;
	}

	public Node unNestBindClause(TriplePath triple, ElementPathBlock epb, boolean hasParent, Var v)
	{
		Node s = triple.getSubject();
		Node p = triple.getPredicate();
		Node o = triple.getObject();
	
		if( s instanceof Node_Triple)
		{
			final Triple tTemp = ((Node_Triple)s).get();
			final TriplePath subTriple = new TriplePath(tTemp);
			s = unNestTriplePattern(subTriple,epb,true);
		}
		
		if( o instanceof Node_Triple)
		{
			final Triple tTemp = ((Node_Triple)o).get();
			final TriplePath objTriple = new TriplePath(tTemp);
			o = unNestTriplePattern(objTriple, epb,true);
		}
		
		Node var = null;
		if(!hasParent)
		{
			var = v; 
		}
		else
		{
			var = NodeFactory.createBlankNode();
		}
		
		final Triple t = new Triple(s,p,o);
		if(doneNested.get(t) == null)
		{
			epb.addTriple(t);
		}
		else
			var = doneNested.get(t);
		
		if(doneNested.get(t) == null)
		{
			Node type = RDF.Nodes.type;
			final Node statement = RDF.Nodes.Statement;
				
			Triple t1 = new Triple(var,type, statement);
			epb.addTriple(t1);
				
			type = RDF.Nodes.subject;
			t1 = new Triple(var,type,s);
			epb.addTriple(t1);
					
			type = RDF.Nodes.predicate;
			t1 = new Triple(var,type,p);
			epb.addTriple(t1);
					
			type = RDF.Nodes.object;
			t1 = new Triple(var,type,o);
			epb.addTriple(t1);
			doneNested.put(t, var);
		}
		return var;
	}

}