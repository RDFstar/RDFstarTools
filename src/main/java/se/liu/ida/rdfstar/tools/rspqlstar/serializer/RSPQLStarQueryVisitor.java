package se.liu.ida.rdfstar.tools.rspqlstar.serializer;

import org.apache.jena.query.QueryVisitor;
import se.liu.ida.rdfstar.tools.rspqlstar.lang.RSPQLStarQuery;

public interface RSPQLStarQueryVisitor extends QueryVisitor {
    void visitRegisterForm(RSPQLStarQuery query);
    void visitComputedEveryForm(RSPQLStarQuery query);
}
