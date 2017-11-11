Web-Annotator
=======

A tool to publish and validate JSON-LD [Web Annotation Data Model](https://www.w3.org/TR/annotation-model/) annotations.

The constructor module can dynamically reshape and reserialize remote JSON-LD API sourced data models using SPARQL CONSTRUCT.

The extractor module depends on two remote services:
* `pandorasystems/fuseki`
* [Image Fragment Resolver Service](https://github.com/ub-leipzig/image-fragment-resolver-service)

 
`sudo /usr/lib/jvm/jdk-9/bin/keytool -import -alias "fuseki" -file ca.crt -keystore /usr/lib/jvm/jdk-9/lib/security/cacerts` 