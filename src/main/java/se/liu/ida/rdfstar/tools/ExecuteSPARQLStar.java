package se.liu.ida.rdfstar.tools;

import org.apache.jena.query.ARQ;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.engine.main.StageBuilder;

import se.liu.ida.rdfstar.tools.parser.lang.LangTurtleStar;
import se.liu.ida.rdfstar.tools.sparqlstar.engine.main.StageGeneratorSPARQLStar;
import se.liu.ida.rdfstar.tools.sparqlstar.lang.SPARQLStar;
import se.liu.ida.rdfstar.tools.sparqlstar.resultset.ResultSetWritersSPARQLStar;

import arq.query;

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

}
