# ConverterRDF2RDFStar

This tool can be used to convert RDF data into RDF* data. More specifically, with this tool any type of RDF files can be converted into Turtle* files. To this end, the tool assumes that any statement-level metadata in the given RDF data is represented using the standard RDF reification vocabulary.

```
se.liu.ida.rdfstar.tools.ConverterRDF2RDFStar [--syntax=NAME] [--base=IRI] [--out=file] infile
  Output options
      --out   --outfile      Output file (optional, printing to stdout if omitted)
  Parser control
      --syntax=NAME          Set syntax (otherwise syntax guessed from file extension)
      --base=URI             Set the base URI (does not apply to N-triples and N-Quads)
      --check                Addition checking of RDF terms
      --strict               Run with in strict mode
      --stop                 Stop parsing on encountering a bad RDF term
      --stop-warnings        Stop parsing on encountering a warning
  Time
      --time                 Time the operation
  General
      -v   --verbose         Verbose
      -q   --quiet           Run with minimal output
      --debug                Output information for debugging
      --help
      --version              Version information
```