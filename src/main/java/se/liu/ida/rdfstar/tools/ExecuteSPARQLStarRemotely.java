package se.liu.ida.rdfstar.tools;

import java.io.OutputStream;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryException;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.shared.JenaException;
import org.apache.jena.sparql.ARQInternalErrorException;
import org.apache.jena.sparql.engine.http.HttpQuery;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.apache.jena.sparql.resultset.ResultSetException;
import org.apache.jena.sparql.util.QueryExecUtils;

import arq.cmdline.CmdARQ;
import arq.cmdline.ModQueryIn;
import arq.cmdline.ModRemote;
import arq.cmdline.ModResultsOut;
import jena.cmd.CmdException;
import jena.cmd.CmdGeneral;
import se.liu.ida.rdfstar.tools.conversion.SPARQLStar2SPARQL;
import se.liu.ida.rdfstar.tools.sparqlstar.lang.ParserSPARQLStar;

/*
 * Takes SPARQL* query and remote endpoint as input, and returns the queried RDF-data as output.
 * 
 */

public class ExecuteSPARQLStarRemotely extends CmdARQ{
	
	//from rsparql
    protected ModQueryIn    modQuery =      new MyModQueryIn() ;
    protected ModRemote     modRemote =     new ModRemote() ;
    protected ModResultsOut modResults =    new ModResultsOut() ;
    
    protected Query querySPARQL;
    
    protected String inputFilename;
    protected OutputStream outStream;
    protected boolean outStreamOpened = false;

	protected ExecuteSPARQLStarRemotely(String[] argv) {
		super(argv);
		
		//from rsparql
		super.addModule(modRemote) ;
        super.addModule(modQuery) ;
        super.addModule(modResults) ;

	}
	
    public static void main(String... argv)
    {
        new ExecuteSPARQLStarRemotely(argv).mainRun();
    }
    
    //TODO: not sure if this is correct written, check with Olaf
    static String usage = ExecuteSPARQLStarRemotely.class.getName() + " --service endpointURL --query <file>";
    
	@Override
	protected String getSummary() {
		return usage;
	}
	
	
    @Override
    protected void processModulesAndArgs()
    {
        super.processModulesAndArgs();
        
        //TODO: maybe change here later so that it is possible to write the result to a file, atm just System.out
    	outStream = System.out;
    	
    	//from rsparql, checks the remote endpoint
    	if ( modRemote.getServiceURL() == null )
             throw new CmdException("No SPARQL endpoint specificied") ;
    	
    }
	

	@Override
	protected void exec() {
		
		try {
    		final Query querySPARQLStar = modQuery.getQuery();
    		querySPARQL = new SPARQLStar2SPARQL().convert(querySPARQLStar);
    	}
        catch (ARQInternalErrorException intEx)
        {
            System.err.println(intEx.getMessage()) ;
            if ( intEx.getCause() != null )
            {
                System.err.println("Cause:") ;
                intEx.getCause().printStackTrace(System.err) ;
                System.err.println() ;
            }
            intEx.printStackTrace(System.err) ;
        }
        catch (ResultSetException ex)
        {
            System.err.println(ex.getMessage()) ;
            ex.printStackTrace(System.err) ;
        }
        catch (QueryException qEx)
        {
            throw new CmdException("Query Exeception", qEx) ;
        }
        catch (JenaException ex)
    	{ 
            ex.printStackTrace();
            throw ex;
        } 
        catch (CmdException ex) { throw ex ; } 
		
		try {
            String serviceURL = modRemote.getServiceURL() ;
            QueryExecution qe = QueryExecutionFactory.sparqlService(serviceURL, querySPARQL) ;
            if ( modRemote.usePost() )
                HttpQuery.urlLimit = 0 ;

            QueryExecUtils.executeQuery(querySPARQL, qe, modResults.getResultsFormat()) ;
        } catch (QueryExceptionHTTP ex)
        {
            throw new CmdException("HTTP Exeception", ex) ;
        }
        catch (Exception ex)
        {
            System.out.flush() ;
            ex.printStackTrace(System.err) ;
        }

	}

	 protected class MyModQueryIn extends ModQueryIn
	    {
	    	public MyModQueryIn() { super(ParserSPARQLStar.syntaxSPARQLstar); }

	    	/** do not register 'querySyntaxDecl' as done in the super class */
	        @Override
	        public void registerWith(CmdGeneral cmdLine) {
	            cmdLine.getUsage().startCategory("Query") ;
	            cmdLine.add(queryFileDecl,   "--query, --file",  "File containing a query") ;
	            cmdLine.add(queryBaseDecl,   "--base",           "Base URI for the query") ;
	        }
	    }
	
}
