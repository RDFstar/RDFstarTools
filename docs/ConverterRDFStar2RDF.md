# ConverterRDFStar2RDF

This tool can be used to convert RDF* data into RDF data. More specifically, this tool converts Turtle* files into Turtle such that the resulting RDF data is a reification-based representation of the RDF* data as defined in Section 4 of ["Foundations of RDF* and SPARQL* - An Alternative Approach to Statement-Level Metadata in RDF"](http://olafhartig.de/files/Hartig_AMW2017_RDFStar.pdf) by Olaf Hartig (AMW 2017).

```
se.liu.ida.rdfstar.tools.ConverterRDFStar2RDF [--time] [--check|--noCheck] [--sink] [--base=IRI] [--out=file] infile
  Output options
      --out                  Output file (optional, printing to stdout if omitted)
  Parser control
      --sink                 Parse but throw away output
      --syntax=NAME          Set syntax (otherwise syntax guessed from file extension)
      --base=URI             Set the base URI (does not apply to N-triples and N-Quads)
      --check                Addition checking of RDF terms
      --strict               Run with in strict mode
      --validate             Same as --sink --check --strict
      --rdfs=file            Apply some RDFS inference using the vocabulary in the file
      --nocheck              Turn off checking of RDF terms
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