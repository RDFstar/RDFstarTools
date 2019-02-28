# SPARQL* Parser
The SPARQL* parser is implemented on top of Apache Jena and can also be registered in Jena's query parser framework. The main class of this parser is `ParserSPARQLStar` and there exist two options to use it: First, the simplest option is to call the `parse` method of the class as demonstrated in the following code snippet.
```
String queryString = ...

ParserSPARQLStar parser = new ParserSPARQLStar();
Query query = parser.parse(new Query(), queryString);
```
The second option is to use the parser via Jena's query parser framework. To this end, the parser has to be registered in this framework, which should happen automatically when you use the symbol `ParserSPARQLStar.syntaxSPARQLstar` as demonstrated in the following.
```
String queryString = ...

Query query = QueryFactory.create(queryString, ParserSPARQLStar.syntaxSPARQLstar);
```
An advantage of the second option (i.e., using the SPARQL* parser via Jena's query parser framework) is that the query can be loaded directly from a file:
```
String filename = ...

Query query = QueryFactory.read("file://" + filename, ParserSPARQLStar.syntaxSPARQLstar);
```
In all cases, the result of parsing is a regular Jena `Query` object. However, the `ElementGroup` that can be obtained by `query.getQueryPattern()` (and that represents the WHERE clause of the query) may contain subelements of type `TriplePath` that, when represented as `Triple` objects, have a `Node_Triple` object as their subject or their object. These `Triple` objects are the result of nested triple patterns in the given SPARQL* query. A minimal example is given as follows.
```
String queryString = "SELECT * WHERE { <<?s ?p ?o>> ?p2 ?o2 }";
Query query = QueryFactory.create(queryString, ParserSPARQLStar.syntaxSPARQLstar);

ElementGroup eg = (ElementGroup) query.getQueryPattern();
ElementPathBlock epb = (ElementPathBlock) eg.get(0);
Triple tp = epb.getPattern().get(0).asTriple();

Node_Triple tp2 = (Node_Triple) tp.getSubject();
```
A second new type of subelements in the `ElementGroup` are `ElementBind` objects whose expression is a `NodeValue` that, when represented as a `Node`, is of type `Node_Triple`. Such `ElementBind` objects are the result of SPARQL*-specific BIND clauses in the given query. A minimal example is given as follows.
```
String queryString = "SELECT * WHERE { BIND( <<?s <p> <o>>> AS ?t) }";
Query query = QueryFactory.create(queryString, ParserSPARQLStar.syntaxSPARQLstar);

ElementGroup eg = (ElementGroup) query.getQueryPattern();
ElementBind eb = (ElementBind) eg.get(0);
NodeValue nv = (NodeValue) eb.getExpr()

Node_Triple tp = (Node_Triple) nv.getNode();
```
