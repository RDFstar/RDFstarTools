package se.liu.ida.rdfstar.tools.rspqlstar.lang;

import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.query.QueryVisitor;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.core.Prologue;
import org.apache.jena.sparql.lang.SPARQLParser;
import org.apache.jena.sparql.lang.SPARQLParserFactory;
import org.apache.jena.sparql.lang.SPARQLParserRegistry;
import org.apache.jena.sparql.serializer.*;
import org.apache.jena.sparql.util.FmtUtils;
import org.apache.jena.sparql.util.NodeToLabelMapBNode;
import se.liu.ida.rdfstar.tools.rspqlstar.serializer.MyFormatterElement;
import se.liu.ida.rdfstar.tools.rspqlstar.serializer.RSPQLStarQuerySerializer;

/**
 * Initializes everything needed to use RSP-QL* within the Jena framework.
 * 
 * Registers the RSP-QL* parser.
 *  
 * @author Robin Keskisärkkä
 */
public class RSPQLStar
{
    public static void init() {}
    static {
    	syntax = new MySyntax("http://ida.liu.se/rdfstar/rspqlstar_11");
    	init$();
    }
    
    private static synchronized void init$()
    {
        Syntax.querySyntaxNames.put("rspqlstar", syntax);
        Syntax.querySyntaxNames.put("rspql*", syntax);

    	// create and register a parser factory for the RSP-QL* syntax
    	final SPARQLParserFactory myParserFactory = new SPARQLParserFactory() {
    		@Override
            public boolean accept( Syntax syntax ) { return syntax.equals(syntax); } 
            @Override
            public SPARQLParser create( Syntax syntax ) { return new ParserRSPQLStar(); }
        };

    	SPARQLParserRegistry.addFactory(syntax, myParserFactory);

		// Register RSP-QL* serializer
		SerializerRegistry sReg = SerializerRegistry.get();
		QuerySerializerFactory rspqlstarQuerySerializerFactory = new QuerySerializerFactory() {

			@Override
			public QueryVisitor create(Syntax syntax, Prologue prologue, IndentedWriter writer) {
				// For the query pattern
				SerializationContext cxt1 = new SerializationContext(prologue, new NodeToLabelMapBNode("b", false));
				// For the construct pattern
				SerializationContext cxt2 = new SerializationContext(prologue, new NodeToLabelMapBNode("c", false));

				return new RSPQLStarQuerySerializer(writer, new MyFormatterElement(writer, cxt1), new FmtExprSPARQL(writer, cxt1),
						new FmtTemplate(writer, cxt2));
			}

			@Override
			public QueryVisitor create(Syntax syntax, SerializationContext context, IndentedWriter writer) {
				return new RSPQLStarQuerySerializer(writer, new MyFormatterElement(writer, context), new FmtExprSPARQL(writer,
						context), new FmtTemplate(writer, context));
			}

			@Override
			public boolean accept(Syntax syntax) {
				return syntax == RSPQLStar.syntax;
			}
		};
		sReg.addQuerySerializer(syntax, rspqlstarQuerySerializerFactory);

		// create and register a serializer factory for the RSP-QL* syntax
    	// The SPARQL serializer of Jena is used currently.
    	//final SerializerRegistry sReg = SerializerRegistry.get();
        //final QuerySerializerFactory sFacSPARQL = sReg.getQuerySerializerFactory(Syntax.syntaxSPARQL_11);
        //final QuerySerializerFactory sFacSPARQLstar = new MyQuerySerializerFactory(sFacSPARQL);
        //sReg.addQuerySerializer(syntax, sFacSPARQLstar);
    }

	static public final Syntax syntax;

	// we have to introduce this class because the constructors of Jena's Syntax class are protected 
	static public class MySyntax extends Syntax
	{    
		protected MySyntax(String s) { super(s); }
	}

}
