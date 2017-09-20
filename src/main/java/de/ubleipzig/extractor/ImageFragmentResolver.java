package de.ubleipzig.extractor;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;

import static org.apache.http.HttpHeaders.USER_AGENT;

public class ImageFragmentResolver {
    private static final String TRIPLES_FILE = "annotations.n3";
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
    private static final String SELECT = "SELECT ?s ?manifest ?source ?chars ?fragment\n" +
            "WHERE {?s <http://www.w3.org/ns/oa#hasTarget> ?anno .\n" +
            "?s <http://www.w3.org/ns/oa#hasBody> ?body .\n" +
            "?body <http://www.w3.org/2011/content#chars> ?chars .\n" +
            "?anno <http://purl.org/dc/terms/isPartOf> ?manifest .\n" +
            "?anno <http://www.w3.org/ns/oa#hasSource> ?source .\n" +
            "?anno <http://www.w3.org/ns/oa#hasSelector> ?selector .\n" +
            "?selector <http://www.w3.org/ns/oa#default> ?default .\n" +
            "?default <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> ?fragment .\n" +
            "}";

    private static final String SERVICE = "http://localhost:3000/resolve?id=";
    private static final String SVCS = "http://rdfs.org/sioc/services#has_service";

    public static void main(String[] args) throws Exception {
        ImageFragmentResolver app = new ImageFragmentResolver();

        Map<String, String> results = app.queryModel();
        System.out.println(results.toString());
       // Extractor.saveFile(results);
    }

    private Map<String, String> queryModel() throws IOException, ClassNotFoundException, InterruptedException, ExecutionException, URISyntaxException {
        Map<String, String> map = new HashMap<>();
        String graph = HttpClient9.syncPost(CONSTRUCT);
        // FileInputStream fis = new FileInputStream(TRIPLES_FILE);
        //ObjectInputStream ois = new ObjectInputStream(fis);
        //List<String> graphs = (List<String>) ois.readObject();
        //ois.close();
        Model m = ModelFactory.createDefaultModel();
        m.read(new ByteArrayInputStream(graph.getBytes()), BASE, "N3");
        Query query = QueryFactory.create(SELECT);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, m)) {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution qs = results.next();
                Resource s = qs.getResource("s").asResource();
                Resource source = qs.getResource("source").asResource();
                Literal fragment = qs.getLiteral("fragment").asLiteral();
                String resolveUri = buildRequestUri(source.toString(), fragment.toString());
                final Resource o = m.createResource(resolveUri);
                map.put(s.toString(), o.toString());
            }
            //final Property p = m.createProperty(SVCS);
            //RDFDataMgr.write(writer, m, Lang.NQUADS);
            //graphs.add(writer.toString());
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String buildRequestUri(String source, String fragment) throws Exception {
        String request;
        if (fragment != null) {
            request = SERVICE + source + "&" + fragment;
        } else {
            request = SERVICE + source;
        }
        URI req = new URI(request);
        return resolveUri(req);
    }

    private String resolveUri(URI req) throws Exception {

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(req);

        request.addHeader("User-Agent", USER_AGENT);
        HttpResponse response = client.execute(request);

        System.out.println("Response Code : "
                + response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }
}
