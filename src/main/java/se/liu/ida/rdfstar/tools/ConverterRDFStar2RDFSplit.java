package se.liu.ida.rdfstar.tools;


import arq.cmdline.ModLangParse;
import arq.cmdline.ModTime;
import jena.cmd.ArgDecl;
import jena.cmd.CmdException;
import jena.cmd.CmdGeneral;
import org.apache.jena.atlas.lib.Lib;
import org.apache.jena.shared.JenaException;
import org.apache.jena.sparql.ARQInternalErrorException;
import se.liu.ida.rdfstar.tools.conversion.RDFStar2RDF;
import se.liu.ida.rdfstar.tools.conversion.RDFStar2RDFSplit;

import java.io.*;


/**
 * @author Ebba Lindstr�m
 * @author Olaf Hartig
 */

public class ConverterRDFStar2RDFSplit extends CmdGeneral {

    protected ModTime modTime = new ModTime();
    protected ModLangParse modLangParse = new ModLangParse();

    protected ArgDecl argPrefixFile = new ArgDecl(ArgDecl.HasValue, "prefix");
    protected ArgDecl argMetaFile = new ArgDecl(ArgDecl.HasValue, "meta");
    protected ArgDecl argReifFile = new ArgDecl(ArgDecl.HasValue, "reif");

    protected String inputFilename;
    protected OutputStream prefixStream;
    protected OutputStream metaStream;
    protected OutputStream reifStream;

    public static void main(String... argv) {
        argv = new String[]{
                "--time",
                "--prefix", "prefix.txt",
                "--meta", "meta.txt",
                "--reif", "reif.txt",
                "test.ttls"
        };
        new ConverterRDFStar2RDFSplit(argv).mainRun();
    }

    protected ConverterRDFStar2RDFSplit(String[] argv) {
        super(argv);
        super.addModule(modTime);
        super.addModule(modLangParse);
        super.add(argPrefixFile, "--prefix", "Prefix output file");
        super.add(argMetaFile, "--meta", "Meta data output file");
        super.add(argReifFile, "--reif", "Reification data output file");
    }

    static String usage = ConverterRDFStar2RDF.class.getName() + " [--time] [--check|--noCheck] [--sink] [--split] [--base=IRI] [--out=file] infile";

    @Override
    protected String getSummary() {
        return usage;
    }

    @Override
    protected void processModulesAndArgs() {
        // initialize the output stream
        final String prefixFile = getValue(argPrefixFile);
        final String metaFile = getValue(argMetaFile);
        final String reifFile = getValue(argReifFile);

        try {
            prefixStream = new FileOutputStream(new File(prefixFile));
            metaStream = new FileOutputStream(new File(metaFile));
            reifStream = new FileOutputStream(new File(reifFile));
        } catch (FileNotFoundException e) {
            cmdError(e.getMessage());
        }
        inputFilename = getPositionalArg(0);
    }

    @Override
    protected void exec() {
        if (modTime.timingEnabled()) {
            modTime.startTimer();
        }

        final RDFStar2RDFSplit converter = new RDFStar2RDFSplit();
        try {
            converter.convert(inputFilename, prefixStream, metaStream, reifStream, modLangParse.getBaseIRI());
        } catch (IOException e) {
            cmdError(e.getMessage());
        }

        if (modTime.timingEnabled()) {
            modTime.endTimer();
            System.out.printf("Processed in %s sec\n", modTime.getTimeInterval() / 1000f);
        }
    }

    @Override
    protected String getCommandName() {
        return Lib.className(this);
    }


}
