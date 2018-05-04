package se.liu.ida.rdfstar.tools.serializer;

import java.io.StringWriter;

import org.apache.jena.atlas.io.AWriter;
import org.apache.jena.atlas.io.Writer2;
import org.apache.jena.graph.Node;

/**
 * 
 * @author Olaf Hartig http://olafhartig.de/
 */
public abstract class AbstractNodeFormatterTurtleStarTest
{
	final protected NodeFormatterTurtleStar formatter;

	public AbstractNodeFormatterTurtleStarTest( NodeFormatterTurtleStar formatter ) {
		this.formatter = formatter;
	}

	protected String serialize( Node n ) {
		final StringWriter w = new StringWriter();
		final AWriter aw = Writer2.wrap(w);
		formatter.format(aw, n);
		aw.close();
		return w.toString();
	}

}
