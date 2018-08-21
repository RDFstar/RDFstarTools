package se.liu.ida.rdfstar.tools.conversion;

import org.apache.jena.query.Query;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.syntax.syntaxtransform.QueryTransformOps;

/**
 * Converts a SPARQL* query (given as a {@link Query} object)
 * into a SPARQL query (also given as a {@link Query} object).
 *
 * @author Olaf Hartig http://olafhartig.de/
 */
public class SPARQLStar2SPARQL
{
	public Query convert( Query query )
	{
		final ElementTransformSPARQLStar etss = new ElementTransformSPARQLStar();
		final Query convertedQuery = QueryTransformOps.transform(query, etss);
		convertedQuery.setSyntax(Syntax.syntaxSPARQL);
		return convertedQuery;
	}
}
