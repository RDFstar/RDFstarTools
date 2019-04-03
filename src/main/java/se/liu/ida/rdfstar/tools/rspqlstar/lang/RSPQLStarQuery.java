package se.liu.ida.rdfstar.tools.rspqlstar.lang;

import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryVisitor;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.serializer.QuerySerializerFactory;
import org.apache.jena.sparql.serializer.SerializerRegistry;
import se.liu.ida.rdfstar.tools.rspqlstar.serializer.RSPQLStarQueryVisitor;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class RSPQLStarQuery extends Query {
    private String outputStream = null;
    private Duration computedEvery = null;
    private Map<String, NamedWindow> namedWindows = new HashMap<>();


    public RSPQLStarQuery(Query query){
        super(query);
        setSyntax(RSPQLStar.syntax);
    }

    public void setOutputStream(String outputStream){
        this.outputStream = outputStream;
    }

    public void setComputedEvery(String interval){
        computedEvery = asDuration(interval);
    }

    public String getOutputStream() {
        return outputStream;
    }

    public Duration getComputedEvery() {
        return computedEvery;
    }

    public Map<String, NamedWindow> getNamedWindows(){
        return namedWindows;
    }

    public void addNamedWindow(String windowIri, String streamIri, String range, String step){
        final NamedWindow window = new NamedWindow(windowIri, streamIri, asDuration(range), asDuration(step));
        namedWindows.put(windowIri, window);
    }

    public Duration asDuration(String duration){
        return Duration.parse(duration);
    }

    public void visit(QueryVisitor v) // extend
    {
        RSPQLStarQueryVisitor visitor = (RSPQLStarQueryVisitor) v;
        visitor.startVisit(this) ;
        visitor.visitResultForm(this) ;
        visitor.visitPrologue(this) ;
        visitor.visitRegisterForm(this);
        visitor.visitComputedEveryForm(this);
        if ( this.isSelectType() )
            visitor.visitSelectResultForm(this) ;
        if ( this.isConstructType() )
            visitor.visitConstructResultForm(this) ;
        if ( this.isDescribeType() )
            visitor.visitDescribeResultForm(this) ;
        if ( this.isAskType() )
            visitor.visitAskResultForm(this) ;
        if ( this.isJsonType() )
            visitor.visitJsonResultForm(this) ;
        visitor.visitDatasetDecl(this) ;
        visitor.visitQueryPattern(this) ;
        visitor.visitGroupBy(this) ;
        visitor.visitHaving(this) ;
        visitor.visitOrderBy(this) ;
        visitor.visitOffset(this) ;
        visitor.visitLimit(this) ;
        visitor.visitValues(this) ;
        visitor.finishVisit(this) ;
    }

    public void serialize(IndentedWriter writer, Syntax outSyntax)
    {
        // Try to use a serializer factory if available
        QuerySerializerFactory factory = SerializerRegistry.get().getQuerySerializerFactory(outSyntax);
        QueryVisitor serializer = factory.create(outSyntax, this, writer);
        visit(serializer);
    }
}
