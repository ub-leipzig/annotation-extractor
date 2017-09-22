Annotation-Extractor
=======

A tool to extract JSON-LD OA annotations stored in a SQLite database produced by [Mirador-SUL](https://github.com/ub-leipzig/mirador_sul).

The tool also converts these annotations into N3 so that the graphs can put into a triplestore for SPARQL queries.

This depends on two remote services:
* `pandorasystems/fuseki`
* [Image Fragment Resolver Service](https://github.com/ub-leipzig/image-fragment-resolver-service)

 