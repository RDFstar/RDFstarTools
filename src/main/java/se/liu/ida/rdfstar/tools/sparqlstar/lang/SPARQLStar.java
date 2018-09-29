package se.liu.ida.rdfstar.tools.sparqlstar.lang;

import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.query.QueryVisitor;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.core.Prologue;
import org.apache.jena.sparql.lang.SPARQLParser;
import org.apache.jena.sparql.lang.SPARQLParserFactory;
import org.apache.jena.sparql.lang.SPARQLParserRegistry;
import org.apache.jena.sparql.serializer.QuerySerializerFactory;
import org.apache.jena.sparql.serializer.SerializationContext;
import org.apache.jena.sparql.serializer.SerializerRegistry;

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
        Syntax.querySyntaxNames.put("sparqlstar",  syntax);
        Syntax.querySyntaxNames.put("sparql*",     syntax);

    	// create and register a parser factory for the SPARQL* syntax
    	final SPARQLParserFactory myParserFactory = new SPARQLParserFactory() {
    		@Override
            public boolean accept( Syntax syntax ) { return syntax.equals(syntax); } 
            @Override
            public SPARQLParser create( Syntax syntax ) { return new ParserSPARQLStar(); }
        };

    	SPARQLParserRegistry.addFactory(syntax, myParserFactory);

    	// create and register a serializer factory for the SPARQL* syntax
    	//     For the time being, simply use the SPARQL serializer of Jena.
    	//     However, this serializer does not work correctly for nested
    	//     triple patterns SPARQL*
    	//     TODO: we need a proper serializer for SPARQL* - https://github.com/RDFstar/RDFstarTools/issues/12
    	final SerializerRegistry sReg = SerializerRegistry.get();
        final QuerySerializerFactory sFacSPARQL = sReg.getQuerySerializerFactory(Syntax.syntaxSPARQL_11);
        final QuerySerializerFactory sFacSPARQLstar = new MyQuerySerializerFactory(sFacSPARQL);
        sReg.addQuerySerializer(syntax,  sFacSPARQLstar);
    }

	static public final Syntax syntax;

	// we have to introduce this class because the constructors of Jena's Syntax class are protected 
	static public class MySyntax extends Syntax
	{    
		protected MySyntax(String s) { super(s); }
	}


	static public class MyQuerySerializerFactory implements QuerySerializerFactory
	{
		final protected QuerySerializerFactory wrappedFactory;

		public MyQuerySerializerFactory( QuerySerializerFactory wrappedFactory ) {
			this.wrappedFactory = wrappedFactory;
		}

		public boolean accept(Syntax s) {
			if (s.equals(syntax))
				return true;
			else
				return wrappedFactory.accept(s);
		}

		public QueryVisitor create(Syntax syntax, Prologue prologue, IndentedWriter writer) {
			return wrappedFactory.create(syntax, prologue, writer);
		}

		public QueryVisitor create(Syntax syntax, SerializationContext context, IndentedWriter writer) {
			return wrappedFactory.create(syntax, context, writer);
		}
	} // end of class MyQuerySerializerFactory

}
