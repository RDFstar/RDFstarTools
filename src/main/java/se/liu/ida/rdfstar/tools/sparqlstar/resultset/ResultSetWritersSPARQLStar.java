package se.liu.ida.rdfstar.tools.sparqlstar.resultset;

import static org.apache.jena.riot.resultset.ResultSetLang.SPARQLResultSetText;

import java.io.OutputStream;
import java.io.Writer;

import org.apache.jena.atlas.lib.NotImplemented;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.query.ResultSet;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RiotException;
import org.apache.jena.riot.resultset.ResultSetWriter;
import org.apache.jena.riot.resultset.ResultSetWriterFactory;
import org.apache.jena.riot.resultset.ResultSetWriterRegistry;
import org.apache.jena.sparql.core.Prologue;
import org.apache.jena.sparql.resultset.TextOutput;
import org.apache.jena.sparql.serializer.SerializationContext;
import org.apache.jena.sparql.util.Context;

/**
 * Can be used to initialize and register {@link ResultSetWriter}s for results
 * of SPARQL* queries. In contrast to standard SPARQL query results, SPARQL*
 * query results may map variables not only to RDF terms but also to triples
 * (represented by {@link Node_Triple} objects).
 * 
 * @author Olaf Hartig http://olafhartig.de/
 */
public class ResultSetWritersSPARQLStar
{
    public static void init() {}
    static {
    	init$();
    }

    private static synchronized void init$() {
        ResultSetWriterRegistry.register(SPARQLResultSetText, new MyFactory());
    }

    protected static class MyFactory implements ResultSetWriterFactory {
		@Override
		public ResultSetWriter create(Lang lang) {
			if ( lang.equals(SPARQLResultSetText) )     return writerText;
            throw new RiotException( "Lang not registered (ResultSet writer)" );
		}
    } 

    protected static ResultSetWriter writerText = new ResultSetWriter() {

        @Override
        public void write(OutputStream out, ResultSet resultSet, Context context) {
        	createTextOutput().format(out, resultSet);
        }

        @Override
        public void write(Writer out, ResultSet resultSet, Context context) {
        	throw new NotImplemented("Writer");
        }

        @Override
        public void write(OutputStream out, boolean result, Context context) {
        	createTextOutput().format(out, result);
        }

        protected TextOutput createTextOutput() {
        	final SerializationContext sc = new SerializationContext( (Prologue)null );
            return new TextOutputStar( sc );
        }
	
    };

}
