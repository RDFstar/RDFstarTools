package se.liu.ida.rdfstar.tools.conversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementBind;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.junit.Test;

import se.liu.ida.rdfstar.tools.sparqlstar.lang.SPARQLStar;

/**
 * 
 * @author Olaf Hartig
 */
public class ElementTransformSPARQLStarTest
{
	@Test
	public void transformElementPathBlock_NoNeedToConvert()
	{
		final String queryString = "SELECT * WHERE { <http://example.org/a> ?p _:b1 }";
		final String expected =    "SELECT * WHERE { <http://example.org/a> ?p _:b2 }";
		checkBgpAndBindOnlyQuery(queryString, expected);
	}

	@Test
	public void transformElementPathBlock_NestedSubject()
	{
		final String queryString = "SELECT * WHERE { << <http://ex.org/a> ?p ?o >> ?p2 ?o2 }";

		final String expected = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		        "SELECT * WHERE { " +
		        "  <http://ex.org/a> ?p ?o ." +
				"  _:b rdf:type rdf:Statement ." +
				"  _:b rdf:subject   <http://ex.org/a> ." +
				"  _:b rdf:predicate ?p ." +
				"  _:b rdf:object    ?o ." +
				"  _:b ?p2 ?o2 ." +
				"}";

		checkBgpAndBindOnlyQuery(queryString, expected);
	}

	@Test
	public void transformElementPathBlock_NestedObject()
	{
		final String queryString = "SELECT * WHERE { ?s2 ?p2 << <http://ex.org/a> ?p ?o >> }";

		final String expected = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		        "SELECT * WHERE { " +
		        "  <http://ex.org/a> ?p ?o ." +
				"  _:b rdf:type rdf:Statement ." +
				"  _:b rdf:subject   <http://ex.org/a> ." +
				"  _:b rdf:predicate ?p ." +
				"  _:b rdf:object    ?o ." +
				"  ?s2 ?p2 _:b ." +
				"}";

		checkBgpAndBindOnlyQuery(queryString, expected);
	}

	@Test
	public void transformElementPathBlock_NestedSubjectAndObject()
	{
		final String queryString = "SELECT * WHERE { << ?s ?p <http://ex.org/a> >> ?p2 << <http://ex.org/a> ?p ?o >> }";

		final String expected = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		        "SELECT * WHERE { " +
		        "  ?s ?p <http://ex.org/a> ." +
		        "  <http://ex.org/a> ?p ?o ." +
				"  _:bs rdf:type rdf:Statement ." +
				"  _:bs rdf:subject   ?s ." +
				"  _:bs rdf:predicate ?p ." +
				"  _:bs rdf:object    <http://ex.org/a> ." +
				"  _:bo rdf:type rdf:Statement ." +
				"  _:bo rdf:subject   <http://ex.org/a> ." +
				"  _:bo rdf:predicate ?p ." +
				"  _:bo rdf:object    ?o ." +
				"  _:bs ?p2 _:bo ." +
				"}";

		checkBgpAndBindOnlyQuery(queryString, expected);
	}

	@Test
	public void transformElementPathBlock_DoubleNestedSubject()
	{
		final String queryString = "SELECT * WHERE { << << <http://ex.org/a> ?p ?o >> ?p2 ?o2 >> ?p3 _:bnode }";

		final String expected = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		        "SELECT * WHERE { " +
		        "  <http://ex.org/a> ?p ?o ." +
		        "  _:bss ?p2 ?o2 ." +
				"  _:bss rdf:type rdf:Statement ." +
				"  _:bss rdf:subject   <http://ex.org/a> ." +
				"  _:bss rdf:predicate ?p ." +
				"  _:bss rdf:object    ?o ." +
				"  _:bs rdf:type rdf:Statement ." +
				"  _:bs rdf:subject   _:bss ." +
				"  _:bs rdf:predicate ?p2 ." +
				"  _:bs rdf:object    ?o2 ." +
				"  _:bs ?p3 _:bnode ." +
				"}";

		checkBgpAndBindOnlyQuery(queryString, expected);
	}

	@Test
	public void transformElementPathBlock_NestedSubjectTwice()
	{
		final String queryString = "SELECT * WHERE { << <http://ex.org/a> ?p ?o >> ?p2 ?o2 ; ?p3 ?o3 }";

		final String expected = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		        "SELECT * WHERE { " +
		        "  <http://ex.org/a> ?p ?o ." +
				"  _:b rdf:type rdf:Statement ." +
				"  _:b rdf:subject   <http://ex.org/a> ." +
				"  _:b rdf:predicate ?p ." +
				"  _:b rdf:object    ?o ." +
				"  _:b ?p2 ?o2 ." +
				"  _:b ?p3 ?o3 ." +
				"}";

		checkBgpAndBindOnlyQuery(queryString, expected);
	}

	@Test
	public void transformElementPathBlock_NestedObjectTwice()
	{
		final String queryString = "SELECT * WHERE { ?s2 ?p2 << <http://ex.org/a> ?p ?o >> . ?s3 ?p3 << <http://ex.org/a> ?p ?o >> }";

		final String expected = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		        "SELECT * WHERE { " +
		        "  <http://ex.org/a> ?p ?o ." +
				"  _:b rdf:type rdf:Statement ." +
				"  _:b rdf:subject   <http://ex.org/a> ." +
				"  _:b rdf:predicate ?p ." +
				"  _:b rdf:object    ?o ." +
				"  ?s2 ?p2 _:b ." +
				"  ?s3 ?p3 _:b ." +
				"}";

		checkBgpAndBindOnlyQuery(queryString, expected);
	}

	@Test
	public void transformElementPathBlock_NestedSubjectAsNestedObject()
	{
		final String queryString = "SELECT * WHERE { << <http://ex.org/a> ?p ?o >> ?p2 ?o2 . ?s3 ?p3 << <http://ex.org/a> ?p ?o >> }";

		final String expected = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		        "SELECT * WHERE { " +
		        "  <http://ex.org/a> ?p ?o ." +
				"  _:b rdf:type rdf:Statement ." +
				"  _:b rdf:subject   <http://ex.org/a> ." +
				"  _:b rdf:predicate ?p ." +
				"  _:b rdf:object    ?o ." +
				"  _:b ?p2 ?o2 ." +
				"  ?s3 ?p3 _:b ." +
				"}";

		checkBgpAndBindOnlyQuery(queryString, expected);
	}

	@Test
	public void transformElementBind_NoNeedToConvert()
	{
		final String queryString = "SELECT * WHERE { BIND ( <http://example.org/a> AS ?t ) }";
		final String expected =    "SELECT * WHERE { BIND ( <http://example.org/a> AS ?t ) }";
		checkBgpAndBindOnlyQuery(queryString, expected);
	}

	@Test
	public void transformElementBind_Simple()
	{
		final String queryString = "SELECT * WHERE { BIND( << <http://ex.org/a> ?p ?o >> AS ?t ) }";

		final String expected = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		        "SELECT * WHERE { " +
		        "  <http://ex.org/a> ?p ?o ." +
				"  ?t rdf:type rdf:Statement ." +
				"  ?t rdf:subject   <http://ex.org/a> ." +
				"  ?t rdf:predicate ?p ." +
				"  ?t rdf:object    ?o ." +
				"}";

		checkBgpAndBindOnlyQuery(queryString, expected);
	}

	@Test
	public void transformElementBind_NestedSubject()
	{
		final String queryString = "SELECT * WHERE { BIND( << << <http://ex.org/a> ?p ?o >> ?p2 ?o2 >> AS ?t ) }";

		final String expected = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		        "SELECT * WHERE { " +
		        "  <http://ex.org/a> ?p ?o ." +
		        "  _:bs ?p2 ?o2 ." +
				"  _:bs rdf:type rdf:Statement ." +
				"  _:bs rdf:subject   <http://ex.org/a> ." +
				"  _:bs rdf:predicate ?p ." +
				"  _:bs rdf:object    ?o ." +
				"  ?t rdf:type rdf:Statement ." +
				"  ?t rdf:subject   _:bs ." +
				"  ?t rdf:predicate ?p2 ." +
				"  ?t rdf:object    ?o2 ." +
				"}";

		checkBgpAndBindOnlyQuery(queryString, expected);
	}

	@Test
	public void transformElementBind_NestedObject()
	{
		final String queryString = "SELECT * WHERE { BIND( << ?s2 ?p2 << <http://ex.org/a> ?p ?o >> >> AS ?t ) }";

		final String expected = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		        "SELECT * WHERE { " +
		        "  <http://ex.org/a> ?p ?o ." +
		        "  ?s2 ?p2 _:bo ." +
				"  _:bo rdf:type rdf:Statement ." +
				"  _:bo rdf:subject   <http://ex.org/a> ." +
				"  _:bo rdf:predicate ?p ." +
				"  _:bo rdf:object    ?o ." +
				"  ?t rdf:type rdf:Statement ." +
				"  ?t rdf:subject   ?s2 ." +
				"  ?t rdf:predicate ?p2 ." +
				"  ?t rdf:object    _:bo ." +
				"}";

		checkBgpAndBindOnlyQuery(queryString, expected);
	}

	@Test
	public void transformElementBind_NestedSubjectAndObject()
	{
		final String queryString = "SELECT * WHERE { BIND( << << <http://ex.org/a> ?p _:bnode >> ?p2 << <http://ex.org/a> ?p ?o >> >> AS ?t ) }";

		final String expected = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		        "SELECT * WHERE { " +
		        "  <http://ex.org/a> ?p _:bnode ." +
		        "  <http://ex.org/a> ?p ?o ." +
		        "  _:bs ?p2 _:bo ." +
				"  _:bs rdf:type rdf:Statement ." +
				"  _:bs rdf:subject   <http://ex.org/a> ." +
				"  _:bs rdf:predicate ?p ." +
				"  _:bs rdf:object    _:bnode ." +
				"  _:bo rdf:type rdf:Statement ." +
				"  _:bo rdf:subject   <http://ex.org/a> ." +
				"  _:bo rdf:predicate ?p ." +
				"  _:bo rdf:object    ?o ." +
				"  ?t rdf:type rdf:Statement ." +
				"  ?t rdf:subject   _:bs ." +
				"  ?t rdf:predicate ?p2 ." +
				"  ?t rdf:object    _:bo ." +
				"}";

		checkBgpAndBindOnlyQuery(queryString, expected);
	}

	@Test
	public void transform_BindVarUse()
	{
		final String queryString = "SELECT * WHERE { BIND( << <http://ex.org/a> ?p ?o >> AS ?t ) . ?t ?p2 ?o2 }";

		final String expected = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		        "SELECT * WHERE { " +
		        "  <http://ex.org/a> ?p ?o ." +
				"  ?t rdf:type rdf:Statement ." +
				"  ?t rdf:subject   <http://ex.org/a> ." +
				"  ?t rdf:predicate ?p ." +
				"  ?t rdf:object    ?o ." +
				"  ?t ?p2 ?o2 ." +
				"}";

		checkBgpAndBindOnlyQuery(queryString, expected);
	}

	@Test
	public void transform_BindSameAsNested()
	{
		final String queryString = "SELECT * WHERE { BIND( << <http://ex.org/a> ?p ?o >> AS ?t ) . << <http://ex.org/a> ?p ?o >> ?p2 ?o2 }";

		final String expected = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		        "SELECT * WHERE { " +
		        "  <http://ex.org/a> ?p ?o ." +
				"  ?t rdf:type rdf:Statement ." +
				"  ?t rdf:subject   <http://ex.org/a> ." +
				"  ?t rdf:predicate ?p ." +
				"  ?t rdf:object    ?o ." +
				"  ?t ?p2 ?o2 ." +
				"}";

		checkBgpAndBindOnlyQuery(queryString, expected);
	}


	// ---- helpers ----

	protected Query convert( String sparqlstarQueryString )
	{
		final String baseIRI = null;

		final Query sparqlstarQuery = QueryFactory.create(sparqlstarQueryString, baseIRI, SPARQLStar.syntax);

		final Query sparqlQuery = new SPARQLStar2SPARQL().convert(sparqlstarQuery);

		assertEquals( Syntax.syntaxSPARQL, sparqlQuery.getSyntax() );

		return sparqlQuery;
	}

	protected void checkBgpAndBindOnlyQuery( String sparqlstarQueryString, String expectedResultQuery )
	{
		final String baseIRI = null;
		final Query expectedQuery = QueryFactory.create(expectedResultQuery, baseIRI, Syntax.syntaxSPARQL);
		final ElementGroup expectedEG = (ElementGroup) expectedQuery.getQueryPattern();

		final Query convertedQuery = convert( sparqlstarQueryString );
		final ElementGroup convertedEG = (ElementGroup) convertedQuery.getQueryPattern();
		
		checkForEquivalence( expectedEG, mergeElementPathBlocks(convertedEG) );
	}

	protected void checkForEquivalence( ElementGroup expectedEG, ElementGroup testEG )
	{
		assertEquals( expectedEG.size(), testEG.size() );
		assertEquals( expectedEG.get(0).getClass(), testEG.get(0).getClass() );

		if ( expectedEG.get(0) instanceof ElementPathBlock ) {
			final ElementPathBlock expectedEPB = (ElementPathBlock) expectedEG.get(0);
			final ElementPathBlock convertedEPB = (ElementPathBlock) testEG.get(0);
			checkForEquivalence( expectedEPB, convertedEPB );
		}
		else if ( expectedEG.get(0) instanceof ElementBind ) {
			final ElementBind expectedEB = (ElementBind) expectedEG.get(0);
			final ElementBind convertedEB = (ElementBind) testEG.get(0);
			checkForEquivalence( expectedEB, convertedEB );
		}
		else
			fail();
	}

	protected void checkForEquivalence( ElementPathBlock expectedEPB, ElementPathBlock testEPB )
	{
		final IsomorphismMap isoMap = new IsomorphismMap();

		final Iterator<TriplePath> eit = expectedEPB.getPattern().iterator();
		while ( eit.hasNext() )
		{
			final Triple expcTP = eit.next().asTriple();

			boolean found = false;
			final Iterator<TriplePath> rit = testEPB.getPattern().iterator();
			while ( rit.hasNext() )
			{
				final Triple testTP = rit.next().asTriple();

				if (    isoMap.canBeIsomorphic(expcTP.getSubject(),   testTP.getSubject())
				     && isoMap.canBeIsomorphic(expcTP.getPredicate(), testTP.getPredicate())
				     && isoMap.canBeIsomorphic(expcTP.getObject(),    testTP.getObject()) )
					found = true;
			}

			if ( ! found )
				System.err.println( "Expected triple pattern not found (" + expcTP.toString() + ")" );

			assertTrue(found);
		}
	}

	protected void checkForEquivalence( ElementBind expectedEB, ElementBind testEB )
	{
		assertTrue( expectedEB.equalTo(testEB,null) );
	}

	protected ElementGroup mergeElementPathBlocks( ElementGroup eg )
	{
		if ( eg.size() == 1 )
			return eg;

		final ElementGroup result = new ElementGroup();

		final Iterator<Element> it = eg.getElements().iterator();
		Element prevEl = it.next();

		while ( it.hasNext() )
		{
			final Element currEl = it.next();
			if (    (currEl instanceof ElementPathBlock)
			     && (prevEl instanceof ElementPathBlock) )
			{
				final ElementPathBlock currPB = (ElementPathBlock) currEl;
				final ElementPathBlock prevPB = (ElementPathBlock) prevEl;
				prevPB.getPattern().addAll( currPB.getPattern() );
			}
			else {
				result.addElement(prevEl);
				prevEl = currEl;
			}
		}

		result.addElement(prevEl);

		return result;
	}

	protected class IsomorphismMap
	{
	    final protected Map<Node, Node> map = new HashMap<Node, Node>();
	    final protected Set<Node> valueSet = new HashSet<Node>();

	    public boolean canBeIsomorphic(Node n1, Node n2) {
	        if (    (n1.isBlank() || Var.isBlankNodeVar(n1))
	             && (n2.isBlank() || Var.isBlankNodeVar(n2)) ) {
	            final Node other = map.get(n1);
	            if ( other == null ) {
	            	if ( valueSet.contains(n2) )
	            		return false;

	                map.put(n1, n2);
	                valueSet.add(n2);
	                return true;
	            }
	            return other.equals(n2);
	        }
	        return n1.equals(n2);
	    }
	} // end of class IsomorphismMap

}
