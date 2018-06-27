package se.liu.ida.rdfstar.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.jena.Jena;
import org.apache.jena.atlas.lib.Lib;
import org.apache.jena.query.ARQ;
import org.apache.jena.riot.RIOT;
import org.apache.jena.shared.JenaException;
import org.apache.jena.sparql.ARQInternalErrorException;

import arq.cmdline.ModLangParse;
import arq.cmdline.ModTime;
import jena.cmd.CmdException;
import jena.cmd.CmdGeneral;
import se.liu.ida.rdfstar.tools.conversion.RDF2RDFStar;

/**
 * 
 * @author Ebba Lindström
 * @author Olaf Hartig
 */
public class ConverterRDF2RDFStar extends CmdGeneral
{
    protected ModTime modTime                   = new ModTime();
    protected ModLangParse modLangParse         = new ModLangParse();

    protected String inputFilename;
    protected OutputStream outStream;

    public static void main(String... argv)
    {
        new ConverterRDF2RDFStar(argv).mainRun();
    }

    public ConverterRDF2RDFStar(String[] argv)
    {
        super(argv);

        modVersion.addClass(Jena.class);
        modVersion.addClass(ARQ.class);
        modVersion.addClass(RIOT.class);

        super.addModule(modTime);
        super.addModule(modLangParse);
    }

    static String usage = ConverterRDF2RDFStar.class.getName()+" [--time] [--check|--noCheck] [--sink] [--base=IRI] infile outfile" ;

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
        
        if ( getNumPositional() != 2 ) {
        	cmdError("Only one input file and one output file allowed");
        }

        inputFilename = getPositionalArg(0);

        // check whether the input file actually exists and is indeed a file
        final File inputFile = new File(inputFilename); 
        if ( ! inputFile.exists() ) {
        	cmdError("The given input file does not exist");
        } 
        if ( ! inputFile.isFile() ) {
        	cmdError("The given input file is not a file");
        }

        final String outputFilename = getPositionalArg(1);
        final File outputFile = new File( outputFilename );

        // verify that the output file does not yet exist
        if ( outputFile.exists() ) {
        	cmdError("The given output file already exists");
        }

        try {
        	outputFile.createNewFile();
        }
        catch ( IOException e ) {
        	cmdError("Creating the output file failed: " + e.getMessage() );
        }

        try {
        	outStream = new FileOutputStream(outputFile);
        }
        catch ( FileNotFoundException e ) {
        	cmdError("The created output file does not exists");
        }
    }

    @Override
    protected void exec()
    {
    	try {
    		final RDF2RDFStar converter = new RDF2RDFStar();
    		converter.convert(inputFilename, outStream);
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

}
