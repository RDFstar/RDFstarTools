package se.liu.ida.rdfstar.tools.conversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.junit.Test;

import se.liu.ida.rdfstar.tools.sparqlstar.lang.SPARQLStar;

/**
 * 
 * @author Olaf Hartig
 */
public class ElementTransformSPARQLStarTest
{
	@Test
	public void transformElementPathBlock1()
	{
		final String queryString = "SELECT * WHERE { <a> ?p ?o }";
		final Query resultQuery = convert( queryString );
		// TODO
	}

	// TODO ...


	// ---- helpers ----

	protected Query convert( String sparqlstarQueryString )
	{
		final String baseIRI = null;

		final Query sparqlstarQuery = QueryFactory.create(sparqlstarQueryString, baseIRI, SPARQLStar.syntax );

		final Query sparqlQuery = new SPARQLStar2SPARQL().convert(sparqlstarQuery);
		sparqlQuery.serialize( System.out );
		return sparqlQuery;
	}

}
