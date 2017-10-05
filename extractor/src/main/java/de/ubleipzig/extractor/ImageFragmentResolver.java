/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.ubleipzig.extractor;

import static org.apache.http.HttpHeaders.USER_AGENT;
import static org.apache.jena.riot.WebContent.contentTypeNTriples;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.slf4j.Logger;

/**
 * ImageFragmentResolver.
 *
 * @author christopher-johnson
 */
public class ImageFragmentResolver {
    private static final Logger log = getLogger(ImageFragmentResolver.class);

    private static final String BASE = "http://ub.uni-leipzig.de";
    private static final String FILTER = "iiif.ub.uni-leipzig.de";
    private static final String REQUEST_URI = "https://localhost:8443/fuseki/annotations?query=";
    private static final String CONSTRUCT = "de/ubleipzig/extractor/annotation.g.construct.rq";
    private static final String SELECT = "de/ubleipzig/extractor/annotation.select.rq";
    private static final String SERVICE = "http://localhost:3000/resolve?id=";
    private static final String CANVAS_FRAGMENT = "http://www.w3.org/ns/oa#hasBody";
    private static final String FRAGMENT_SVC = "http://rdfs.org/sioc/services#has_fragment_service";
    private static final String IMPLEMENTS = "http://usefulinc.com/ns/doap#implements";
    private static final String PROFILE = "http://iiif.io/api/image/2/level1.json";
    private static final String CHARS = "http://www.w3.org/2011/content#chars";
    private static final String WITHIN = "http://purl.org/dc/terms/isPartOf";
    private static final String TO_URI = "https://localhost:8443/fuseki/fragments/data";

    public static void main(String[] args) throws Exception {
        ImageFragmentResolver app = new ImageFragmentResolver();
        app.buildImageFragmentModel();
    }

    private void buildImageFragmentModel()
            throws IOException, ClassNotFoundException, InterruptedException, ExecutionException,
            URISyntaxException {
        final String constructQuery = QueryUtil.getQuery(CONSTRUCT, FILTER, true);
        final String selectQuery = QueryUtil.getQuery(SELECT, FILTER);

        Property p1 = ResourceFactory.createProperty(FRAGMENT_SVC);
        Property p2 = ResourceFactory.createProperty(IMPLEMENTS);
        Property p3 = ResourceFactory.createProperty(CHARS);
        Property p4 = ResourceFactory.createProperty(CANVAS_FRAGMENT);
        Property p5 = ResourceFactory.createProperty(WITHIN);
        Resource o2 = ResourceFactory.createResource(PROFILE);
        Model m = ModelFactory.createDefaultModel();
        Model m2 = ModelFactory.createDefaultModel();

        String graph = HttpClient9.syncGetQuery(REQUEST_URI + constructQuery, contentTypeNTriples);
        log.info("constructing graph from triplestore as N3");

        m.read(new ByteArrayInputStream(graph.getBytes()), BASE, "N3");
        log.info("reading graph into model");
        Query query = QueryFactory.create(selectQuery);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, m)) {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution qs = results.next();
                Resource source = qs.getResource("source").asResource();
                Resource anno = qs.getResource("anno").asResource();
                Resource manifest = qs.getResource("manifest").asResource();
                Resource annoid =
                        ResourceFactory.createResource(source.toString() + "#" + anno.toString());
                Literal fragment = qs.getLiteral("fragment").asLiteral();
                Literal chars = qs.getLiteral("chars").asLiteral();
                String fragmentServiceUri = getServiceUri(source.toString(), fragment.toString());
                log.info("getting serviceUri using Canvas identifier from API");
                Resource o = ResourceFactory.createResource(fragmentServiceUri);
                Statement s1 = ResourceFactory.createStatement(annoid, p1, o);
                Statement s2 = ResourceFactory.createStatement(o, p2, o2);
                Statement s3 = ResourceFactory.createStatement(annoid, p3, chars);
                Statement s4 = ResourceFactory.createStatement(source, p4, annoid);
                Statement s5 = ResourceFactory.createStatement(annoid, p5, manifest);
                m2.add(s1);
                m2.add(s2);
                m2.add(s3);
                m2.add(s4);
                m2.add(s5);
                log.info("adding statements to new model");
            }
            writeToDataset(m2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getServiceUri(String source, String fragment) throws Exception {
        String request;
        if (fragment != null) {
            request = SERVICE + source + "&" + fragment;
        } else {
            request = SERVICE + source;
        }
        URI req = new URI(request);
        return resolveServiceUri(req);
    }

    private void writeToDataset(Model m2)
            throws InterruptedException, ExecutionException, IOException, URISyntaxException {
        final ByteArrayOutputStream rdfOut;
        rdfOut = new ByteArrayOutputStream();
        RDFDataMgr.write(rdfOut, m2, Lang.NTRIPLES);
        //String updateQuery = buildUpdateQuery(rdfOut.toString());
        HttpClient9.asyncPut(rdfOut.toString(), TO_URI);
        log.info("writing to triplestore");
    }

    private String buildUpdateQuery(String m) {
        return "INSERT DATA {" + m + "};";
    }

    private String resolveServiceUri(URI req) throws Exception {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(req);
        request.addHeader("User-Agent", USER_AGENT);
        HttpResponse response = client.execute(request);
        System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
        BufferedReader rd =
                new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }
}
