package se.liu.ida.rdfstar.tools.cmdutils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import jena.cmd.ArgDecl;
import jena.cmd.CmdGeneral;

/**
 * Extend {@link CmdGeneral} with options to remove or to ignore arguments.
 *  
 * @author Olaf Hartig
 */
abstract public class ExtendedCmdGeneral extends CmdGeneral
{
	final protected Set<String> argsToBeIgnored = new HashSet<>();

    public ExtendedCmdGeneral( String[] argv )
    {
        super(argv);
    }

    public void removeArg( String argName )
    {
    	final ArgDecl arg = argMap.get(argName);
    	if ( arg != null )
    		remove(arg);
    }

    /** Attention: does not remove from usage string. */
    public void remove( ArgDecl arg )
    {
    	for ( final Iterator<String> it = arg.names(); it.hasNext(); )
            argMap.remove( it.next() );
    }

	public void registerArgumentToBeIgnored( String name )
	{
		argsToBeIgnored.add(name);
	}

	public void unregisterArgumentToBeIgnored( String name )
	{
		argsToBeIgnored.remove(name);
	}

	@Override
	public void add( ArgDecl argDecl, String argName, String msg )
	{
		// argsToBeIgnored may not have been created when this method
		// is called from the constructor of the superclass 
		if ( argsToBeIgnored == null ) {
			super.add(argDecl, argName, msg);
			return;
		}

		// Check whether any of the names of the given argument
		// has been registered to be ignored. If that's the case,
		// ignore the given argument.
    	for ( final Iterator<String> it = argDecl.names(); it.hasNext(); )
    	{
    		final String name = it.next();
    		if ( argsToBeIgnored.contains(name) )
    			return;  // here we ignore
    	}

    	// do not ignore
    	super.add(argDecl, argName, msg);
	}

}
