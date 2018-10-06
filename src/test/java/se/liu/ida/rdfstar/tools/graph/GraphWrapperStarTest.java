package se.liu.ida.rdfstar.tools.graph;

import static org.junit.Assert.assertEquals;

import org.apache.jena.graph.Graph;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.liu.ida.rdfstar.tools.graph.RDFStarUtils;

/**
 * 
 * @author Olaf Hartig http://olafhartig.de/
 */
public class GraphWrapperStarTest {
	
	@Before
	public void setup() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void stringParse1_RedundancyAugmented() {
        final String x = "<<<s> <p> <o>>> <p2> <o2> .";
        final Graph g = RDFStarUtils.createRedundancyAugmentedGraphFromTurtleStarSnippet(x);

        assertEquals( 2, g.size() );
	}

	@Test
	public void stringParse2_RedundancyAugmented() {
        final String x = "<s2> <p2> <<<s> <p> <o>>> .";
        final Graph g = RDFStarUtils.createRedundancyAugmentedGraphFromTurtleStarSnippet(x);

        assertEquals( 2, g.size() );
	}

	@Test
	public void stringParse3_RedundancyAugmented() {
        final String x = "<<<s> <p> <o>>> <p2> <<<s> <p> <o>>> .";
        final Graph g = RDFStarUtils.createRedundancyAugmentedGraphFromTurtleStarSnippet(x);

        assertEquals( 2, g.size() );
	}

	@Test
	public void stringParse4_RedundancyAugmented() {
        final String x = "<< <s> <p> <<<s> <p> <o>>> >> <p2> <o2> .";
        final Graph g = RDFStarUtils.createRedundancyAugmentedGraphFromTurtleStarSnippet(x);

        assertEquals( 3, g.size() );
	}

	@Test
	public void stringParse5_RedundancyAugmented() {
        final String x = "<<<s> <p> <o>>> <p2> <o2> , <o3> .";
        final Graph g = RDFStarUtils.createRedundancyAugmentedGraphFromTurtleStarSnippet(x);

        assertEquals( 3, g.size() );
	}

}
