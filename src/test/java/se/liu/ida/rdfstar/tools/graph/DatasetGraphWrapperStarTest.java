package se.liu.ida.rdfstar.tools.graph;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.DatasetGraph;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import se.liu.ida.rdfstar.tools.graph.RDFStarUtils;

import static org.junit.Assert.assertEquals;

/**
 * 
 * @author Robin Keskisärkkä
 */
public class DatasetGraphWrapperStarTest {
	
	@Before
	public void setup() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void stringParse1_RedundancyAugmented() {
        final String x = "<g> { <<<s> <p> <o>>> <p2> <o2> . }";
        final DatasetGraph ds = RDFStarUtils.createRedundancyAugmentedDatasetGraphFromTrigStarSnippet(x);

        final Node n = ds.listGraphNodes().next();
        assertEquals( 2, ds.getGraph(n).size() );
	}

	@Test
	public void stringParse2_RedundancyAugmented() {
        final String x = "<g> { <s2> <p2> <<<s> <p> <o>>> . }";
		final DatasetGraph ds = RDFStarUtils.createRedundancyAugmentedDatasetGraphFromTrigStarSnippet(x);

		final Node n = ds.listGraphNodes().next();
		assertEquals( 2, ds.getGraph(n).size() );
	}

	@Test
	public void stringParse3_RedundancyAugmented() {
        final String x = "<g> { <<<s> <p> <o>>> <p2> <<<s> <p> <o>>> . }";
		final DatasetGraph ds = RDFStarUtils.createRedundancyAugmentedDatasetGraphFromTrigStarSnippet(x);

		final Node n = ds.listGraphNodes().next();
		assertEquals( 2, ds.getGraph(n).size() );
	}

	@Test
	public void stringParse4_RedundancyAugmented() {
        final String x = "<g> { << <s> <p> <<<s> <p> <o>>> >> <p2> <o2> . }";
		final DatasetGraph ds = RDFStarUtils.createRedundancyAugmentedDatasetGraphFromTrigStarSnippet(x);

		final Node n = ds.listGraphNodes().next();
		assertEquals( 3, ds.getGraph(n).size() );
	}

	@Test
	public void stringParse5_RedundancyAugmented() {
        final String x = "<g> { <<<s> <p> <o>>> <p2> <o2> , <o3> . }";
		final DatasetGraph ds = RDFStarUtils.createRedundancyAugmentedDatasetGraphFromTrigStarSnippet(x);

		final Node n = ds.listGraphNodes().next();
		assertEquals( 3, ds.getGraph(n).size() );
	}

	@Test
	public void stringParse6_RedundancyAugmented() {
		final String x = "{ <<<s> <p> <o>>> <p2> <o2> . }";
		final DatasetGraph ds = RDFStarUtils.createRedundancyAugmentedDatasetGraphFromTrigStarSnippet(x);

		assertEquals( 2, ds.getDefaultGraph().size() );
	}

	@Test
	public void stringParse7_RedundancyAugmented() {
		final String x = "<s2> <p2> <<<s> <p> <o>>> .";
		final DatasetGraph ds = RDFStarUtils.createRedundancyAugmentedDatasetGraphFromTrigStarSnippet(x);

		assertEquals( 2, ds.getDefaultGraph().size() );
	}

	@Test
	public void stringParse8_RedundancyAugmented() {
		final String x = "<<<s> <p> <o>>> <p2> <<<s> <p> <o>>> .";
		final DatasetGraph ds = RDFStarUtils.createRedundancyAugmentedDatasetGraphFromTrigStarSnippet(x);

		assertEquals( 2, ds.getDefaultGraph().size() );
	}

	@Test
	public void stringParse9_RedundancyAugmented() {
		final String x = "<< <s> <p> <<<s> <p> <o>>> >> <p2> <o2> .";
		final DatasetGraph ds = RDFStarUtils.createRedundancyAugmentedDatasetGraphFromTrigStarSnippet(x);

		assertEquals( 3, ds.getDefaultGraph().size() );
	}

	@Test
	public void stringParse10_RedundancyAugmented() {
		final String x = "<<<s> <p> <o>>> <p2> <o2> , <o3> .";
		final DatasetGraph ds = RDFStarUtils.createRedundancyAugmentedDatasetGraphFromTrigStarSnippet(x);

		assertEquals( 3, ds.getDefaultGraph().size() );
	}

}
