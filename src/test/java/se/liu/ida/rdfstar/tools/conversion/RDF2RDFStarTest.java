package se.liu.ida.rdfstar.tools.conversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Ebba Lindström
 */
public class RDF2RDFStarTest
{	
	@Before
	public void setup() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void noReification()
	{
		final String filename = "NoReification.ttl";
		final String fullFilename = getClass().getResource("/RDF/"+filename).getFile();
		final ByteArrayOutputStream os = new ByteArrayOutputStream(); 
		new RDF2RDFStar().convert(fullFilename, os);
		os.toString();
		try {
			os.close();
		}
		catch ( IOException e ) {
			fail( "Closing the output stream failed: " + e.getMessage() );
		}
		// TODO
	}

	// TODO ...

}
