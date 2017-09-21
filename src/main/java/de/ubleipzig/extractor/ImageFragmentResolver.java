package de.ubleipzig.extractor;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import static org.apache.http.HttpHeaders.USER_AGENT;

public class ImageFragmentResolver {
    private static final String BASE = "http://ub.uni-leipzig.de";
    private static final String CONSTRUCT = "CONSTRUCT {?s <http://www.w3.org/ns/oa#hasTarget> ?anno .\n" +
            "?s <http://www.w3.org/ns/oa#hasBody> ?body .\n" +
            "?body <http://www.w3.org/2011/content#chars> ?chars .\n" +
            "?anno <http://purl.org/dc/terms/isPartOf> ?manifest .\n" +
            "?anno <http://www.w3.org/ns/oa#hasSource> ?source .\n" +
            "?anno <http://www.w3.org/ns/oa#hasSelector> ?selector .\n" +
            "?selector <http://www.w3.org/ns/oa#default> ?default .\n" +
            "  ?default <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> ?fragment .}\n" +
            "WHERE {?s <http://www.w3.org/ns/oa#hasTarget> ?anno .\n" +
            "?s <http://www.w3.org/ns/oa#hasBody> ?body .\n" +
            "?body <http://www.w3.org/2011/content#chars> ?chars .\n" +
            "?anno <http://purl.org/dc/terms/isPartOf> ?manifest .\n" +
            "?anno <http://www.w3.org/ns/oa#hasSource> ?source .\n" +
            "FILTER(regex(str(?source), \"iiif.ub.uni-leipzig.de\" )) .\n" +
            "?anno <http://www.w3.org/ns/oa#hasSelector> ?selector .\n" +
            "?selector <http://www.w3.org/ns/oa#default> ?default .\n" +
            "?default <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> ?fragment .\n" +
            "}";
    private static final String SELECT = "SELECT ?anno ?chars ?source ?fragment\n" +
            "WHERE {?s <http://www.w3.org/ns/oa#hasTarget> ?anno .\n" +
            "?s <http://www.w3.org/ns/oa#hasBody> ?body .\n" +
            "?body <http://www.w3.org/2011/content#chars> ?chars .\n" +
            "?anno <http://www.w3.org/ns/oa#hasSource> ?source .\n" +
            "?anno <http://www.w3.org/ns/oa#hasSelector> ?selector .\n" +
            "?selector <http://www.w3.org/ns/oa#default> ?default .\n" +
            "?default <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> ?fragment .\n" +
            "}";

    private static final String SERVICE = "http://localhost:3000/resolve?id=";
    private static final String CANVAS_FRAGMENT = "http://www.w3.org/ns/oa#hasBody";
    private static final String FRAGMENT_SVC = "http://rdfs.org/sioc/services#has_fragment_service";
    private static final String IMPLEMENTS = "http://usefulinc.com/ns/doap#implements";
    private static final String PROFILE = "http://iiif.io/api/image/2/level1.json";
    private static final String CHARS = "http://www.w3.org/2011/content#chars";
    private static final String destinationGraph = "http://localhost:3030/fuseki/fragments";

    public static void main(String[] args) throws Exception {
        ImageFragmentResolver app = new ImageFragmentResolver();

        app.buildImageFragmentModel();
    }

    private void buildImageFragmentModel() throws IOException, ClassNotFoundException, InterruptedException,
            ExecutionException,URISyntaxException {
        String graph = HttpClient9.syncPostQuery(CONSTRUCT);
        Property p1 = ResourceFactory.createProperty(FRAGMENT_SVC);
        Property p2 = ResourceFactory.createProperty(IMPLEMENTS);
        Property p3 = ResourceFactory.createProperty(CHARS);
        Property p4 = ResourceFactory.createProperty(CANVAS_FRAGMENT);
        Resource o2 = ResourceFactory.createResource(PROFILE);
        Model m = ModelFactory.createDefaultModel();
        Model m2 = ModelFactory.createDefaultModel();
        m.read(new ByteArrayInputStream(graph.getBytes()), BASE, "N3");
        Query query = QueryFactory.create(SELECT);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, m)) {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution qs = results.next();
                Resource source = qs.getResource("source").asResource();
                Resource anno = qs.getResource("anno").asResource();
                Resource annoid = ResourceFactory.createResource(source.toString() + "#" + anno.toString());
                Literal fragment = qs.getLiteral("fragment").asLiteral();
                Literal chars = qs.getLiteral("chars").asLiteral();
                String fragmentServiceUri = buildServiceUri(source.toString(), fragment.toString());
                Resource o = ResourceFactory.createResource(fragmentServiceUri);
                Statement s1 = ResourceFactory.createStatement(annoid, p1, o);
                Statement s2 = ResourceFactory.createStatement(o, p2, o2);
                Statement s3 = ResourceFactory.createStatement(annoid, p3, chars);
                Statement s4 = ResourceFactory.createStatement(source, p4,annoid);
                m2.add(s1);
                m2.add(s2);
                m2.add(s3);
                m2.add(s4);
            }
            writeToDataset(m2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String buildServiceUri(String source, String fragment) throws Exception {
        String request;
        if (fragment != null) {
            request = SERVICE + source + "&" + fragment;
        } else {
            request = SERVICE + source;
        }
        URI req = new URI(request);
        return resolveServiceUri(req);
    }

    private void writeToDataset(Model m2) throws InterruptedException, ExecutionException, IOException,
            URISyntaxException {
        final ByteArrayOutputStream rdfOut;
        rdfOut = new ByteArrayOutputStream();
        RDFDataMgr.write(rdfOut, m2, Lang.NTRIPLES);
        //String updateQuery = buildUpdateQuery(rdfOut.toString());
        HttpClient9.syncPut(rdfOut.toString(), destinationGraph);
    }

    private String buildUpdateQuery(String m) {
        return "INSERT DATA {" + m + "};";
    }

    private String resolveServiceUri(URI req) throws Exception {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(req);
        request.addHeader("User-Agent", USER_AGENT);
        HttpResponse response = client.execute(request);
        System.out.println("Response Code : "
                + response.getStatusLine().getStatusCode());
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }
}
