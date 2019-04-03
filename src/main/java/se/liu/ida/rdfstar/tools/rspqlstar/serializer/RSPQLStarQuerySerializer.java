package se.liu.ida.rdfstar.tools.rspqlstar.serializer;

import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.query.SortCondition;
import org.apache.jena.sparql.core.Prologue;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.core.VarExprList;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.serializer.*;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.Template;
import se.liu.ida.rdfstar.tools.rspqlstar.lang.NamedWindow;
import se.liu.ida.rdfstar.tools.rspqlstar.lang.RSPQLStarQuery;
import se.liu.ida.rdfstar.tools.rspqlstar.util.MyFmtUtils;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * This class is based on the QuerySerializer class but substitutes the use of FmtUtil for MyFmtUtil.
 * Since the constructor of QuerySerializer are protected the full code has to be included here.
 */

public class RSPQLStarQuerySerializer implements RSPQLStarQueryVisitor {

    static final int BLOCK_INDENT = 2;
    protected FormatterTemplate fmtTemplate;
    protected MyFormatterElement fmtElement;
    protected FmtExprSPARQL fmtExpr;
    protected IndentedWriter out = null;
    protected Prologue prologue = null;

    public RSPQLStarQuerySerializer(OutputStream _out, MyFormatterElement formatterElement,
                             FmtExprSPARQL formatterExpr,
                             FormatterTemplate formatterTemplate) {
        this(new IndentedWriter(_out),
                formatterElement, formatterExpr, formatterTemplate);
    }

    public RSPQLStarQuerySerializer(IndentedWriter iwriter,
                             MyFormatterElement formatterElement,
                             FmtExprSPARQL formatterExpr,
                             FormatterTemplate formatterTemplate) {
        out = iwriter;
        fmtTemplate = formatterTemplate;
        fmtElement = formatterElement;
        fmtExpr = formatterExpr;
    }

    @Override
    public void startVisit(Query query) {
    }

    @Override
    public void visitResultForm(Query query) {
    }

    @Override
    public void visitPrologue(Prologue prologue) {
        this.prologue = prologue;
        int row1 = out.getRow();
        PrologueSerializer.output(out, prologue);
        int row2 = out.getRow();
        if (row1 != row2)
            out.newline();
    }

    @Override
    public void visitSelectResultForm(Query query) {
        out.print("SELECT ");
        if (query.isDistinct())
            out.print("DISTINCT ");
        if (query.isReduced())
            out.print("REDUCED ");
        out.print(" "); //Padding

        if (query.isQueryResultStar())
            out.print("*");
        else
            appendNamedExprList(query, out, query.getProject());
        out.newline();
    }

    @Override
    public void visitConstructResultForm(Query query) {
        out.print("CONSTRUCT ");
//        if ( query.isQueryResultStar() )
//        {
//            out.print("*") ;
//            out.newline() ;
//        }
//        else
        {
            out.incIndent(BLOCK_INDENT);
            out.newline();
            Template t = query.getConstructTemplate();
            fmtTemplate.format(t);
            out.decIndent(BLOCK_INDENT);
        }
    }

    @Override
    public void visitDescribeResultForm(Query query) {
        out.print("DESCRIBE ");

        if (query.isQueryResultStar())
            out.print("*");
        else {
            appendVarList(query, out, query.getResultVars());
            if (query.getResultVars().size() > 0 &&
                    query.getResultURIs().size() > 0)
                out.print(" ");
            appendURIList(query, out, query.getResultURIs());
        }
        out.newline();
    }

    @Override
    public void visitAskResultForm(Query query) {
        out.print("ASK");
        out.newline();
    }

    @Override
    public void visitJsonResultForm(Query query) {
        out.println("JSON {");
        out.incIndent(BLOCK_INDENT);
        out.incIndent(BLOCK_INDENT);
        boolean first = true;
        for (Map.Entry<String, Node> entry : query.getJsonMapping().entrySet()) {
            String field = entry.getKey();
            Node value = entry.getValue();
            if (!first)
                out.println(" ,");
            first = false;
            out.print('"');
            out.print(field);
            out.print('"');
            out.print(" : ");
            out.pad(15);
            out.print(MyFmtUtils.stringForNode(value, prologue));
        }
        out.decIndent(BLOCK_INDENT);
        out.decIndent(BLOCK_INDENT);
        out.print(" }");
        out.newline();
    }

    @Override
    public void visitDatasetDecl(Query q) {
        RSPQLStarQuery query = (RSPQLStarQuery) q;

        if (query.getGraphURIs() != null && query.getGraphURIs().size() != 0) {
            for (String uri : query.getGraphURIs()) {
                out.print("FROM ");
                out.print(MyFmtUtils.stringForURI(uri, query));
                out.newline();
            }
        }
        if (query.getNamedGraphURIs() != null && query.getNamedGraphURIs().size() != 0) {
            for (String uri : query.getNamedGraphURIs()) {
                // One per line
                out.print("FROM NAMED ");
                out.print(MyFmtUtils.stringForURI(uri, query));
                out.newline();
            }
        }
        for(NamedWindow namedWindow : query.getNamedWindows().values()) {
            // One per line
            out.print("FROM NAMED WINDOW ");
            out.print(MyFmtUtils.stringForURI(namedWindow.getWindowName(), query));
            out.print(" ON ");
            out.print(MyFmtUtils.stringForURI(namedWindow.getStreamName(), query));
            out.print(" [");
            out.print("RANGE ");
            out.print(namedWindow.getRange().toString());
            out.print(" ");
            out.print("STEP ");
            out.print(namedWindow.getStep().toString());
            out.print("]");
            out.newline();

        }
    }

    @Override
    public void visitQueryPattern(Query query) {
        if (query.getQueryPattern() != null) {
            out.print("WHERE");
            out.incIndent(BLOCK_INDENT);
            out.newline();

            Element el = query.getQueryPattern();
            fmtElement.visitAsGroup(el);
            //el.visit(fmtElement) ;
            out.decIndent(BLOCK_INDENT);
            out.newline();
        }
    }

    @Override
    public void visitGroupBy(Query query) {
        if (query.hasGroupBy()) {
            // Can have an empty GROUP BY list if the groupin gis implicit
            // by use of an aggregate in the SELECT clause.
            if (!query.getGroupBy().isEmpty()) {
                out.print("GROUP BY ");
                appendNamedExprList(query, out, query.getGroupBy());
                out.println();
            }
        }
    }

    @Override
    public void visitHaving(Query query) {
        if (query.hasHaving()) {
            out.print("HAVING");
            for (Expr expr : query.getHavingExprs()) {
                out.print(" ");
                fmtExpr.format(expr);
            }
            out.println();
        }
    }

    @Override
    public void visitOrderBy(Query query) {
        if (query.hasOrderBy()) {
            out.print("ORDER BY ");
            boolean first = true;
            for (SortCondition sc : query.getOrderBy()) {
                if (!first)
                    out.print(" ");
                sc.format(fmtExpr, out);
                first = false;
            }
            out.println();
        }
    }

    @Override
    public void visitLimit(Query query) {
        if (query.hasLimit()) {
            out.print("LIMIT   " + query.getLimit());
            out.newline();
        }
    }

    @Override
    public void visitOffset(Query query) {
        if (query.hasOffset()) {
            out.print("OFFSET  " + query.getOffset());
            out.newline();
        }
    }

    @Override
    public void visitValues(Query query) {
        if (query.hasValues()) {
            outputDataBlock(out, query.getValuesVariables(), query.getValuesData(), fmtElement.getContext());
            out.newline();
        }
    }

    public static void outputDataBlock(IndentedWriter out, List<Var> variables, List<Binding> values, SerializationContext cxt) {
        out.print("VALUES ");
        if (variables.size() == 1) {
            // Short form.
            out.print("?");
            out.print(variables.get(0).getVarName());
            out.print(" {");
            out.incIndent();
            for (Binding valueRow : values)
                outputValuesOneRow(out, variables, valueRow, cxt);
            out.decIndent();
            out.print(" }");
            return;
        }
        // Long form.
        out.print("(");
        for (Var v : variables) {
            out.print(" ");
            out.print(v.toString());
        }
        out.print(" )");
        out.print(" {");
        out.incIndent();
        for (Binding valueRow : values) {
            out.println();
            out.print("(");
            outputValuesOneRow(out, variables, valueRow, cxt);
            out.print(" )");
        }
        out.decIndent();
        out.ensureStartOfLine();
        out.print("}");
    }

    private static void outputValuesOneRow(IndentedWriter out, List<Var> variables, Binding row, SerializationContext cxt) {
        // A value may be null for UNDEF
        for (Var var : variables) {
            out.print(" ");
            Node value = row.get(var);
            if (value == null)
                out.print("UNDEF");
            else {
                // Context for bnodes.
                // Bnodes don't occur in legal syntax but a rewritten query may
                // have them.  The output will not be legal SPARQL.
                // ARQ (SPARQL with extensions) does parse blankd nodes in VALUES.
                out.print(MyFmtUtils.stringForNode(value, cxt));
            }
        }
    }

    @Override
    public void finishVisit(Query query) {
        out.flush();
    }

    // ----

    void appendVarList(Query query, IndentedWriter sb, List<String> vars) {
        boolean first = true;
        for (String varName : vars) {
            Var var = Var.alloc(varName);
            if (!first)
                sb.print(" ");
            sb.print(var.toString());
            first = false;
        }

    }

    void appendNamedExprList(Query query, IndentedWriter sb, VarExprList namedExprs) {
        boolean first = true;
        for (Var var : namedExprs.getVars()) {
            Expr expr = namedExprs.getExpr(var);
            if (!first)
                sb.print(" ");

            if (expr != null) {
                // The following are safe to write without ()
                // Compare/merge with fmtExpr.format
                boolean needParens = true;

                if (expr.isFunction())
                    needParens = false;
//                else if ( expr instanceof E_Aggregator )
//                    // Aggregators are variables (the function maps to an internal variable
//                    // that is accesses by the E_Aggregator
//                    needParens = false ;
                else if (expr.isVariable())
                    needParens = false;

                if (!Var.isAllocVar(var))
                    // AS ==> need parens
                    needParens = true;

                if (needParens)
                    out.print("(");
                fmtExpr.format(expr);
                if (!Var.isAllocVar(var)) {
                    sb.print(" AS ");
                    sb.print(var.toString());
                }
                if (needParens)
                    out.print(")");
            } else {
                sb.print(var.toString());
            }
            first = false;
        }
    }

    static void appendURIList(Query query, IndentedWriter sb, List<Node> vars) {
        SerializationContext cxt = new SerializationContext(query);
        boolean first = true;
        for (Node node : vars) {
            if (!first)
                sb.print(" ");
            sb.print(MyFmtUtils.stringForNode(node, cxt));
            first = false;
        }
    }

    @Override
    public void visitRegisterForm(RSPQLStarQuery query) {
        out.print("REGISTER STREAM ");
        out.print(MyFmtUtils.stringForURI(query.getOutputStream(), query));
    }

    @Override
    public void visitComputedEveryForm(RSPQLStarQuery query) {
        out.print(" COMPUTED EVERY ");
        out.print(query.getComputedEvery().toString());
        out.print(" AS");
        out.newline();
    }
}

