package se.liu.ida.rdfstar.tools.graph;

import java.io.StringReader;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFLib;

import org.apache.jena.sparql.core.DatasetGraph;
import se.liu.ida.rdfstar.tools.parser.lang.LangTrigStar;
import se.liu.ida.rdfstar.tools.parser.lang.LangTurtleStar;

/**
 * Some helper functions for dealing with Jena objects that are used for RDF* data.
 *   
 * @author Olaf Hartig
 */
public class RDFStarUtils
{
	/**
	 * Return true if the given triple is a nested triple, that is,
	 * it has another triple in its subject or its object position. 
	 */
	static public boolean isNested( Triple t )
	{
		return ( t.getSubject() instanceof Node_Triple ) && ( t.getObject() instanceof Node_Triple );
	}

	/**
	 * If the given triple is a nested triple that has another triple in its
	 * subject position, return that subject triple; otherwise return null.
	 */
	static public Triple getSubjectTriple( Triple t )
	{
		final Node s = t.getSubject();
		if ( s instanceof Node_Triple )
			return ( (Node_Triple) s ).get();
		else
			return null;
	}

	/**
	 * If the given triple is a nested triple that has another triple in its
	 * object position, return that object triple; otherwise return null.
	 */
	static public Triple getObjectTriple( Triple t )
	{
		final Node o = t.getObject();
		if ( o instanceof Node_Triple )
			return ( (Node_Triple) o ).get();
		else
			return null;
	}

	/**
	 * Returns a {@link Graph} populated with the RDF* data
	 * in the given Turtle* serialization.
	 */
	static public Graph createGraphFromTurtleStarSnippet( String snippet )
	{
	    final Graph g = ModelFactory.createDefaultModel().getGraph();
	    populateGraphFromTurtleStarSnippet(g, snippet);
		return g;
	}

	/**
	 * Returns a redundancy-augmented {@link Graph} populated
	 * with the RDF* data in the given Turtle* serialization.
	 * 
	 * @see GraphWrapperStar
	 */
	static public Graph createRedundancyAugmentedGraphFromTurtleStarSnippet( String snippet )
	{
	    final Graph g = ModelFactory.createDefaultModel().getGraph();
	    final Graph gstar = new GraphWrapperStar(g);
	    populateGraphFromTurtleStarSnippet(gstar, snippet);
		return gstar;
	}

	/**
	 * Adds the RDF* data from the given Turtle* serialization
	 * to the given {@link Graph}. 
	 */
	static public void populateGraphFromTurtleStarSnippet( Graph graph, String snippet )
	{
		final StringReader reader = new StringReader(snippet);
	    final StreamRDF dest = StreamRDFLib.graph(graph);
	
	    RDFParser.create()
	             .source(reader)
	             .lang(LangTurtleStar.TURTLESTAR)
	             .parse(dest);
	}

	/**
	 * Returns a {@link DatasetGraph} populated with the RDF* data
	 * in the given Trig* serialization.
	 */
	static public DatasetGraph createDatasetGraphFromTrigStarSnippet(String snippet )
	{
		final DatasetGraph ds = DatasetFactory.create().asDatasetGraph();
		populateDatasetGraphFromTrigStarSnippet(ds, snippet);
		return ds;
	}

	/**
	 * Returns a redundancy-augmented {@link DatasetGraph} populated
	 * with the RDF* data in the given Trig* serialization.
	 *
	 * @see GraphWrapperStar
	 */
	static public DatasetGraph createRedundancyAugmentedDatasetGraphFromTrigStarSnippet(String snippet )
	{
		final DatasetGraph ds = DatasetFactory.create().asDatasetGraph();
		final DatasetGraph dsstar = new DatasetGraphWrapperStar(ds);
		populateDatasetGraphFromTrigStarSnippet(dsstar, snippet);
		return dsstar;
	}

	/**
	 * Adds the RDF* data from the given Trig* serialization
	 * to the given {@link DatasetGraph}.
	 */
	static public void populateDatasetGraphFromTrigStarSnippet(DatasetGraph datasetGraph, String snippet )
	{
		final StringReader reader = new StringReader(snippet);
		final StreamRDF dest = StreamRDFLib.dataset(datasetGraph);

		RDFParser.create()
				.source(reader)
				.lang(LangTrigStar.TRIGSTAR)
				.parse(dest);
	}

}
