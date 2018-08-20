package se.liu.ida.rdfstar.tools.sparqlstar.lang;

import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.lang.SPARQLParser;
import org.apache.jena.sparql.lang.SPARQLParserFactory;
import org.apache.jena.sparql.lang.SPARQLParserRegistry;

/**
 * Initializes everything needed to use SPARQL* within the Jena framework.
 * 
 * Registers the SPARQL* parser.
 *  
 * @author Olaf Hartig http://olafhartig.de/
 */
public class SPARQLStar
{
    public static void init() {}
    static {
    	syntax = new MySyntax("http://ida.liu.se/rdfstar/SPARQLstar_11");
    	init$();
    }
    
    private static synchronized void init$()
    {
    	// create and register a parser factory for the SPARQL* syntax
    	final SPARQLParserFactory myParserFactory = new SPARQLParserFactory() {
    		@Override
            public boolean accept( Syntax syntax ) { return syntax.equals(syntax); } 
            @Override
            public SPARQLParser create( Syntax syntax ) { return new ParserSPARQLStar(); }
        };

    	SPARQLParserRegistry.addFactory(syntax, myParserFactory);

    	// create and register a serializer factory for the SPARQL* syntax
        //QuerySerializerFactory mySerializerFactory = ...
    }

	static public final Syntax syntax;

	// we have to introduce this class because the constructors of Jena's Syntax class are protected 
	static public class MySyntax extends Syntax {    
		protected MySyntax(String s) { super(s); }
	}

}
