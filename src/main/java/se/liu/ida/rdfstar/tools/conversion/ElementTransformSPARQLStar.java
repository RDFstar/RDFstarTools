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

public class ElementTransformSPARQLStar extends ElementTransformCopyBase
{
	HashMap<Triple,Node> doneNested = new HashMap<Triple,Node>();

	//called for elementpathblock
	public Node unNest(TriplePath triple, ElementPathBlock epb, boolean hasParent)
	{	
		Node s = triple.getSubject();
		Node p = triple.getPredicate();
		Node o = triple.getObject();

		if( s instanceof Node_Triple)
		{
			Triple tTemp = ((Node_Triple)s).get();
			TriplePath subTriple = new TriplePath(tTemp);
			s = unNest(subTriple,epb,true);
		}
		
		if( o instanceof Node_Triple)
		{
			Triple tTemp = ((Node_Triple)o).get();
			TriplePath objTriple = new TriplePath(tTemp);
			o = unNest(objTriple, epb,true);
		}
		
		Node var = NodeFactory.createBlankNode(); 
		Triple t = new Triple(s,p,o);

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
				Node statement = RDF.Nodes.Statement;
			
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
	
	//Called for elementbind
	public Node unNest(TriplePath triple, ElementPathBlock epb, boolean hasParent, Var v)
	{
		Node s = triple.getSubject();
		Node p = triple.getPredicate();
		Node o = triple.getObject();
	
		if( s instanceof Node_Triple)
		{
			Triple tTemp = ((Node_Triple)s).get();
			TriplePath subTriple = new TriplePath(tTemp);
			s = unNest(subTriple,epb,true);
		}
		
		if( o instanceof Node_Triple)
		{
			Triple tTemp = ((Node_Triple)o).get();
			TriplePath objTriple = new TriplePath(tTemp);
			o = unNest(objTriple, epb,true);
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
		
		Triple t = new Triple(s,p,o);
		if(doneNested.get(t) == null)
		{
			epb.addTriple(t);
		}
		else
			var = doneNested.get(t);
		
		if(doneNested.get(t) == null)
		{
			Node type = RDF.Nodes.type;
			Node statement = RDF.Nodes.Statement;
				
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

	@Override
	public Element transform(ElementPathBlock el)
	{
		ElementPathBlock epb = new ElementPathBlock() ;
		for (TriplePath tp : el.getPattern()) {
			unNest(tp,epb,false);
		}

		return epb;
	}
	
	@Override
	public Element transform(ElementBind eb, Var v, Expr expr2)
	{
		ElementPathBlock epb = new ElementPathBlock() ;
		NodeValue nv = (NodeValue) eb.getExpr();
		Node_Triple nt = (Node_Triple) nv.getNode();
		Triple tTemp = ((Node_Triple)nt).get();
		TriplePath tp = new TriplePath(tTemp);
		unNest(tp,epb,false, v);
		return epb;
	}

}
