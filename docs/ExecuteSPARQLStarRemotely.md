# ExecuteSPARQLStarRemotely

This tool can be used to execute a SPARQL representation of a given SPARQL* query over RDF reification data in a SPARQL endpoint. More specifically, this tool rewrites the given SPARQL* query into a SPARQL query (see ConverterSPARQLStar2SPARQL) and then has this SPARQL query executed by the given SPARQL endpoint.

```
se.liu.ida.rdfstar.tools.ExecuteSPARQLStarRemotely --service endpointURL --query <file>
  Results
      --results=             Results format (Result set: text, XML, JSON, CSV, TSV; Graph: RDF serialization)
  Query
      --query, --file        File containing a query
      --base                 Base URI for the query
  Remote
      --service=             Service endpoint URL
      --post                 Force use of HTTP POST
  Symbol definition
      --set                  Set a configuration symbol to a value
  General
      -v   --verbose         Verbose
      -q   --quiet           Run with minimal output
      --debug                Output information for debugging
      --help
      --version              Version information
      --strict               Operate in strict SPARQL mode (no extensions of any kind)
```