package se.liu.ida.rdfstar.tools;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.jena.atlas.lib.Lib;
import org.apache.jena.shared.JenaException;
import org.apache.jena.sparql.ARQInternalErrorException;

import arq.cmdline.ModLangParse;
import arq.cmdline.ModTime;
import jena.cmd.ArgDecl;
import jena.cmd.CmdException;
import jena.cmd.CmdGeneral;
import se.liu.ida.rdfstar.tools.conversion.RDFStar2RDF;


/**
 * 
 * @author Ebba Lindström
 * @author Olaf Hartig
 */

public class ConverterRDFStar2RDF extends CmdGeneral {

	protected ModTime modTime                   = new ModTime();
    protected ModLangParse modLangParse         = new ModLangParse();
    //what is this below used for?
    protected ArgDecl argOutputFile    = new ArgDecl(ArgDecl.HasValue, "out", "output", "outfile", "outputfile");

    protected String inputFilename;
    protected OutputStream outStream;
    protected boolean outStreamOpened = false;

	
	public static void main(String... argv) {
	        new ConverterRDFStar2RDF(argv).mainRun();
	    }
	
	protected ConverterRDFStar2RDF(String[] argv) {
		super(argv);
		
        super.addModule(modTime);
        super.addModule(modLangParse);
        
        //Add more things here?

	}
	
	static String usage = ConverterRDFStar2RDF.class.getName()+" [--time] [--check|--noCheck] [--sink] [--base=IRI] [--out=file] infile" ;

	@Override
	protected String getSummary() {
		return usage;
	}
	
    @Override
    protected void processModulesAndArgs()
    {
        super.processModulesAndArgs();

        if ( getNumPositional() == 0 ) {
        	cmdError("No input file specified");
        }
        else if ( getNumPositional() > 1 ) {
        	cmdError("Only one input file allowed");
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

        // initialize the output stream
        final String outFileName = getValue(argOutputFile);
        if ( outFileName == null )
        {
        	outStream = System.out; // no output file specified, write to stdout instead
        }
        else
        {
        	final File outputFile = new File( outFileName );

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
            	outStreamOpened = true;
            }
            catch ( FileNotFoundException e ) {
            	cmdError("The created output file does not exists");
            }
        }        
    }
	
	@Override
	protected void exec() {
    	try {
    		final RDFStar2RDF converter = new RDFStar2RDF();
    		converter.convert(inputFilename, outStream);
    	}
        catch (ARQInternalErrorException intEx)
        {
            System.err.println(intEx.getMessage()) ;
            if ( intEx.getCause() != null )
            {
                System.err.println("Cause:");
                intEx.getCause().printStackTrace(System.err);
                System.err.println();
            }
            intEx.printStackTrace(System.err);
        }
        catch (JenaException ex)
    	{ 
            ex.printStackTrace();
            throw ex;
        } 
        catch (CmdException ex) { throw ex; } 
        catch (Exception ex)
        {
            throw new CmdException("Exception", ex);
        }
    	finally
    	{
    		if ( outStreamOpened ) {
    			try {
    				outStream.close();
    			}
    			catch ( IOException e ) {
    				throw new CmdException("Closing the output stream failed: " + e.getMessage(), e );
    			}
    		}
    	}
		
	}

	@Override
	protected String getCommandName() {
		return Lib.className(this) ;
	}

	
}
