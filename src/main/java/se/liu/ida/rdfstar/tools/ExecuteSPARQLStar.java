package se.liu.ida.rdfstar.tools;

import org.apache.jena.query.ARQ;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.engine.main.StageBuilder;

import se.liu.ida.rdfstar.tools.parser.lang.LangTurtleStar;
import se.liu.ida.rdfstar.tools.sparqlstar.core.DatasetGraphWrapperStar;
import se.liu.ida.rdfstar.tools.sparqlstar.engine.main.StageGeneratorSPARQLStar;
import se.liu.ida.rdfstar.tools.sparqlstar.lang.SPARQLStar;
import se.liu.ida.rdfstar.tools.sparqlstar.resultset.ResultSetWritersSPARQLStar;

import arq.query;
import arq.cmdline.ModDataset;
import arq.cmdline.ModDatasetGeneral;

public class ExecuteSPARQLStar extends query
{
    public static void main( String... argv )
    {
    	LangTurtleStar.init();
    	ResultSetWritersSPARQLStar.init();

        new ExecuteSPARQLStar(argv).mainRun();
    }

    public ExecuteSPARQLStar( String[] argv )
    {
    	super(argv);
    }

    @Override
    protected Syntax getDefaultSyntax() { return SPARQLStar.syntax; }

    @Override
    protected ModDataset setModDataset()
    {
    	return new ModDatasetRedundancyAugmentation();
    }

    @Override
    protected void processModulesAndArgs()
    {
    	super.processModulesAndArgs();
    }

    @Override
    protected void exec()
    {
    	StageBuilder.setGenerator(ARQ.getContext(), new StageGeneratorSPARQLStar());

    	super.exec();
    } 


    static protected class ModDatasetRedundancyAugmentation extends ModDatasetGeneral
    {
        @Override
        public Dataset createDataset()
        {
        	final DatasetGraph dsg = DatasetGraphFactory.createTxnMem();
        	final DatasetGraph dsgWrapped = new DatasetGraphWrapperStar(dsg);
        	final Dataset ds = DatasetFactory.wrap(dsgWrapped);
        	addGraphs(ds);
        	dataset = ds;
        	return dataset;
        }

    } // end of class ModDatasetRedundancyAugmentation

}
