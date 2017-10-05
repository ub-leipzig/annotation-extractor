Web-Annotator
=======

A tool to publish and validate JSON-LD [Web Annotation Data Model](https://www.w3.org/TR/annotation-model/) annotations.

The tool can converts these annotations into N3 so that the graphs can put into a triplestore for SPARQL queries.

This depends on two remote services:
* `pandorasystems/fuseki`
* [Image Fragment Resolver Service](https://github.com/ub-leipzig/image-fragment-resolver-service)

 
`sudo /usr/lib/jvm/jdk-9/bin/keytool -import -alias "fuseki" -file ca.crt -keystore /usr/lib/jvm/jdk-9/lib/security/cacerts` 