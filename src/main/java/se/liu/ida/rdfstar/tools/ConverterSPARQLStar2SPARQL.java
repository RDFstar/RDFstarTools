package se.liu.ida.rdfstar.tools;

import jena.cmd.CmdException;
import jena.cmd.CmdGeneral;

import org.apache.jena.Jena;
import org.apache.jena.atlas.lib.Lib;
import org.apache.jena.query.ARQ;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryException;
import org.apache.jena.riot.RIOT;
import org.apache.jena.shared.JenaException;
import org.apache.jena.sparql.ARQInternalErrorException;
import org.apache.jena.sparql.resultset.ResultSetException;

import se.liu.ida.rdfstar.tools.conversion.SPARQLStar2SPARQL;
import se.liu.ida.rdfstar.tools.sparqlstar.lang.SPARQLStar;
import arq.cmdline.ModQueryIn;
import arq.cmdline.ModQueryOut;

/**
 * Command line tool to convert SPARQL* queries into SPARQL queries.
 *   
 * @author Olaf Hartig http://olafhartig.de/
 */
public class ConverterSPARQLStar2SPARQL extends CmdGeneral
{
	protected ModQueryIn    modQuery        = new MyModQueryIn();
    protected ModQueryOut   modOutput       = new ModQueryOut();

    public static void main(String... argv)
    {
        new ConverterSPARQLStar2SPARQL(argv).mainRun();
    }

    public ConverterSPARQLStar2SPARQL(String[] argv)
    {
        super(argv);

        modVersion.addClass(Jena.class);
        modVersion.addClass(ARQ.class);
        modVersion.addClass(RIOT.class);

        super.addModule(modQuery);
        super.addModule(modOutput);
    }

    static String usage = ConverterSPARQLStar2SPARQL.class.getName()+" [--out syntax]  \"query\" | --query <file>";

    @Override
    protected String getSummary()
    {
        return usage;
    }

    @Override
    protected String getCommandName() { return Lib.className(this) ; }

    @Override
    protected void processModulesAndArgs()
    {
        super.processModulesAndArgs();
    }

    @Override
    protected void exec()
    {
    	try {
    		final Query querySPARQLStar = modQuery.getQuery();
    		final Query querySPARQL = new SPARQLStar2SPARQL().convert(querySPARQLStar);
    		modOutput.output(querySPARQL);
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
            //System.err.println(qEx.getMessage()) ;
            throw new CmdException("Query Exeception", qEx) ;
        }
        catch (JenaException ex)
    	{ 
            ex.printStackTrace();
            throw ex;
        } 
        catch (CmdException ex) { throw ex ; } 
        catch (Exception ex)
        {
            throw new CmdException("Exception", ex) ;
        }
    }

    protected class MyModQueryIn extends ModQueryIn
    {
    	public MyModQueryIn() { super(SPARQLStar.syntax); }

    	/** do not register 'querySyntaxDecl' as done in the super class */
        @Override
        public void registerWith(CmdGeneral cmdLine) {
            cmdLine.getUsage().startCategory("Query") ;
            cmdLine.add(queryFileDecl,   "--query, --file",  "File containing a query") ;
            cmdLine.add(queryBaseDecl,   "--base",           "Base URI for the query") ;
        }
    }

}
