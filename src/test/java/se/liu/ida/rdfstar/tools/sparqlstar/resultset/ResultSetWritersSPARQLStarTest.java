package se.liu.ida.rdfstar.tools.sparqlstar.resultset;

import static org.apache.jena.riot.resultset.ResultSetLang.SPARQLResultSetText;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.junit.Assert.assertTrue;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.impl.ModelCom;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.riot.resultset.ResultSetWriterFactory;
import org.apache.jena.riot.resultset.ResultSetWriterRegistry;
import org.apache.jena.sparql.core.ResultBinding;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.binding.BindingFactory;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.util.IteratorCollection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Olaf Hartig http://olafhartig.de/
 */
public class ResultSetWritersSPARQLStarTest
{
	@Before
	public void setup() {
		ResultSetWritersSPARQLStar.init();
	}

	@After
	public void tearDown() {
	}

	@Test
	public void registrationOK() {
		final ResultSetWriterFactory f = ResultSetWriterRegistry.getFactory(SPARQLResultSetText); 
		assertTrue( f instanceof ResultSetWritersSPARQLStar.MyFactory );

		final Node u = NodeFactory.createURI("http://example.com/i");
		final Node n = new Node_Triple(new Triple(u, u, u));
		final Binding b = BindingFactory.binding(Var.alloc("t"), n);
		final ResultSet rs = new TestResultSet(b);
		final ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		f.create(SPARQLResultSetText).write(out, rs, null);
	}


	// ----- helpers ------

	protected class TestResultSet implements ResultSet
	{
		final protected Model model = new RDFStarAwareModel();
		final protected Binding b;
		final protected QuerySolution s;
		final protected List<String> varNames;
		protected boolean reported = false;

		public TestResultSet( Binding b ) {
			this.b = b;
			this.s = new ResultBinding( model, b );
			varNames = IteratorCollection.iteratorToList(s.varNames());
		}

	    @Override public boolean hasNext() { return ! reported; }

	    @Override public QuerySolution next() { reported = true; return s; }

	    @Override public QuerySolution nextSolution() { return next(); }

	    @Override public Binding nextBinding() { reported = true; return b; }

	    @Override public int getRowNumber() { throw new UnsupportedOperationException(); }
	    
	    @Override public List<String> getResultVars() { return varNames; }

	    @Override public Model getResourceModel() { return model; }
	}

	protected class RDFStarAwareModel extends ModelCom
	{
		public RDFStarAwareModel( Graph base ) { super(base); }
		public RDFStarAwareModel() { this( GraphFactory.createDefaultGraph() ); }

		@Override
		public RDFNode asRDFNode( Node n ) {
			if ( n instanceof Node_Triple ) {
				return new ResourceImpl(n, this);
			}
			else
				return super.asRDFNode(n);
		}
	}

}
