package se.liu.ida.rdfstar.tools.conversion;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.lang.LabelToNode;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedTriplesStream;
import org.apache.jena.riot.out.NodeFormatter;
import org.apache.jena.riot.out.NodeFormatterTTL;
import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.riot.system.RiotLib;
import org.apache.jena.vocabulary.RDF;
import se.liu.ida.rdfstar.tools.parser.lang.LangTurtleStar;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Conversion of Turtle* files into Turtle such that the resulting
 * RDF data is a reification-based representation of the RDF* data (prefixes,
 * meta, and reification)
 * to separate files.
 *
 * @author Robin Keskisärkkä
 */
public class RDFStar2RDFSplit {
    protected final static int BUFFER_SIZE = 16000;

    public void convert(String inputFilename, OutputStream prefixStream, OutputStream metaStream, OutputStream reifStream, String baseIRI) throws IOException {
        final FirstPass fp = new FirstPass(inputFilename);
        fp.setBaseIRI(baseIRI);
        fp.execute();

        final FileConverter fc = new FileConverter(inputFilename,
                prefixStream,
                metaStream,
                reifStream,
                fp.getBaseIRI(),
                fp.getPrefixMap());
        fc.execute();
    }

    protected class FileConverter {
        final protected String inputFilename;
        final protected OutputStream prefixStream;
        final protected OutputStream metaStream;
        final protected OutputStream reifStream;
        final protected String baseIRI;
        final protected PrefixMap pmap;

        // populated during the conversion to record for every reified
        // triple, the blank node that has been created as statement ID
        protected final Map<Triple, Node> bNodes = new HashMap<>();

        protected boolean first_lap = true;
        protected Node lastSubject = NodeFactory.createBlankNode(); //used to create turtle blocks
        protected Node lastPredicate = NodeFactory.createBlankNode(); //used to create turtle blocks

        public FileConverter(String inputFilename, OutputStream prefixStream, OutputStream metaStream, OutputStream reifStream, String baseIRI, PrefixMap pm) {
            this.inputFilename = inputFilename;
            this.prefixStream = prefixStream;
            this.metaStream = metaStream;
            this.reifStream = reifStream;
            this.baseIRI = baseIRI;
            this.pmap = pm;
        }

        public void execute() throws IOException {
            // print all prefixes and the base IRI to the output file
            final IndentedWriter prefixWriter = new IndentedWriter(prefixStream);
            RiotLib.writePrefixes(prefixWriter, pmap);
            RiotLib.writeBase(prefixWriter, baseIRI);
            prefixWriter.close();

            final IndentedWriter metaWriter = new IndentedWriter(metaStream);
            final IndentedWriter reifWriter = new IndentedWriter(reifStream);


            // second pass over the file to perform the conversion in a streaming manner
            final PipedRDFIterator<Triple> it = new PipedRDFIterator<>(BUFFER_SIZE);
            final PipedTriplesStream triplesStream = new PipedTriplesStream(it);

            // PipedRDFStream and PipedRDFIterator need to be on different threads
            new Thread(() -> RDFParser.create().labelToNode(LabelToNode.createUseLabelEncoded())
                    .source(inputFilename)
                    .checking(false)
                    .lang(LangTurtleStar.TURTLESTAR)
                    .base(baseIRI)
                    .build()
                    .parse(triplesStream)).start();

            final NodeFormatter nFmt = new NodeFormatterTTL(baseIRI, pmap);

            while (it.hasNext()) {
                printTriples(it.next(), metaWriter, reifWriter, nFmt, false);
            }

            it.close();

            metaWriter.close();
            reifWriter.close();
        }

        /**
         * Recursively flattens nested triples and prints in pretty format. One triple at the time.
         */
        protected Node printTriples(Triple triple, IndentedWriter metaWriter, IndentedWriter reifWriter, NodeFormatter nFmt, boolean hasParent) {
            Node s = triple.getSubject();
            Node p = triple.getPredicate();
            Node o = triple.getObject();

            if (s instanceof Node_Triple) {
                final Triple subjTriple = ((Node_Triple) s).get();
                s = printTriples(subjTriple, metaWriter, reifWriter, nFmt, true);
                first_lap = false;
            }

            if (o instanceof Node_Triple) {
                final Triple objTriple = ((Node_Triple) o).get();
                o = printTriples(objTriple, metaWriter, reifWriter, nFmt, true);
                first_lap = false;
            }

            //if we have reached here, we are at the deepest level of recursion, or if its not nested at all
            final Triple hashKey = Triple.create(s, p, o);
            if (hasParent) {
                Node bnode;
                if ((bnode = bNodes.get(hashKey)) != null) {
                    return bnode;
                } else {
                    bnode = NodeFactory.createBlankNode();
                    bNodes.put(hashKey, bnode);

                    nFmt.format(reifWriter, bnode);
                    reifWriter.write(" ");
                    nFmt.format(reifWriter, RDF.Nodes.type);
                    reifWriter.write(" ");
                    nFmt.format(reifWriter, RDF.Nodes.Statement);
                    reifWriter.write(" ;\n" + StringUtils.leftPad("", bnode.toString().length() + 4));
                    nFmt.format(reifWriter, RDF.Nodes.subject);
                    reifWriter.write(" ");
                    nFmt.format(reifWriter, s);
                    reifWriter.write(" ;\n" + StringUtils.leftPad("", bnode.toString().length() + 4));
                    nFmt.format(reifWriter, RDF.Nodes.predicate);
                    reifWriter.write(" ");
                    nFmt.format(reifWriter, p);
                    reifWriter.write(" ;\n" + StringUtils.leftPad("", bnode.toString().length() + 4));
                    nFmt.format(reifWriter, RDF.Nodes.object);
                    reifWriter.write(" ");
                    nFmt.format(reifWriter, o);
                    reifWriter.write(" .\n");

                    lastSubject = bnode;
                    lastPredicate = NodeFactory.createBlankNode();
                    return bnode;
                }
            } else // Enter here if not nested or top level of recursion
            {
                if (lastSubject.matches(s)) {
                    nFmt.format(metaWriter, s);
                    metaWriter.write(" ");
                    nFmt.format(metaWriter, p);
                    metaWriter.write(" ");
                    nFmt.format(metaWriter, o);
                    metaWriter.write(" .\n");
                } else {
                    if (lastSubject.matches(o) || !first_lap) {
                        metaWriter.write(" .\n");
                    } else
                        first_lap = false;

                    nFmt.format(metaWriter, s);
                    metaWriter.write(" ");
                    nFmt.format(metaWriter, p);
                    metaWriter.write(" ");
                    nFmt.format(metaWriter, o);
                }
            }

            reifWriter.flush();
            metaWriter.flush();

            lastSubject = s;
            lastPredicate = p;
            first_lap = false;

            return null; // return to the execute method
        }

    } // end of class FileConverter


    /**
     * Performs a first pass over the input file to collect all prefixes.
     */
    protected class FirstPass {
        final protected String inputFilename;

        protected PrefixMap pmap;
        protected String baseIRI;

        public FirstPass(String inputFilename) {
            this.inputFilename = inputFilename;
        }

        public PrefixMap getPrefixMap() {
            return pmap;
        }

        public String getBaseIRI() {
            return baseIRI;
        }

        public void setBaseIRI(String baseIRI) {
            this.baseIRI = baseIRI;
        }

        public void execute() {
            final PipedRDFIterator<Triple> it = new PipedRDFIterator<>(BUFFER_SIZE);
            final PipedTriplesStream triplesStream = new PipedTriplesStream(it);

            // PipedRDFStream and PipedRDFIterator need to be on different threads
            new Thread(() -> RDFParser.create().labelToNode(LabelToNode.createUseLabelEncoded())
                    .source(inputFilename)
                    .checking(false)
                    .lang(LangTurtleStar.TURTLESTAR)
                    .base(baseIRI)
                    .build()
                    .parse(triplesStream)).start();

            // consume the iterator
            while (it.hasNext()) {
                it.next();
            }

            pmap = it.getPrefixes();
            if (baseIRI == null) {
                baseIRI = it.getBaseIri();
            }

            it.close();
        }

    } // end of class FirstPass

}
