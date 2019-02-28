# ConverterSPARQLStar2SPARQL

This tool can be used to rewrite SPARQL* queries into SPARQL queries that represent SPARQL* features (i.e., nested triple patterns and the SPARQL* version of BIND) using the standard RDF reification vocabulary. For a definition of the mapping that is the formal basis of the rewriting performed by this tool refer to Section 4 of ["Foundations of RDF* and SPARQL* - An Alternative Approach to Statement-Level Metadata in RDF"](http://olafhartig.de/files/Hartig_AMW2017_RDFStar.pdf) by Olaf Hartig (AMW 2017).

```
se.liu.ida.rdfstar.tools.ConverterSPARQLStar2SPARQL [--out syntax]  "query" | --query <file>
  Output
      --out, --format        Output syntax
      --num                  Print line numbers
  Query
      --query, --file        File containing a query
      --base                 Base URI for the query
  General
      -v   --verbose         Verbose
      -q   --quiet           Run with minimal output
      --debug                Output information for debugging
      --help
      --version              Version information
```