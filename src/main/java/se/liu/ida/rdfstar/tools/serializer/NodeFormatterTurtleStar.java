package se.liu.ida.rdfstar.tools.serializer;

import org.apache.jena.atlas.io.AWriter;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.out.NodeFormatter;

/**
 * An extension of {@link NodeFormatter} to indicate
 * Node formatters developed to support Turtle*.
 *  
 * @author Olaf Hartig http://olafhartig.de/
 */
public interface NodeFormatterTurtleStar extends NodeFormatter
{
	static class Helper {
		static public void format(AWriter w, Node_Triple n, NodeFormatter nf) {
			final Triple t = n.get();

			w.print("<<");
			nf.format(w, t.getSubject());
			w.print(' ');
			nf.format(w, t.getPredicate());
			w.print(' ');
			nf.format(w, t.getObject());
			w.print(">>");
		}
	}

}
