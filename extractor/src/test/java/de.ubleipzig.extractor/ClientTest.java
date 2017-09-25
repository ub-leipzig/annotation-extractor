package de.ubleipzig.extractor;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.jena.JenaRDF;
import org.apache.commons.rdf.api.Triple;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.RepeatedTest;
import trellisldp.io.JenaIOService;
import org.trellisldp.spi.IOService;
import org.glassfish.jersey.test.JerseyTest;

import java.io.*;
import java.net.URISyntaxException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.rdf.api.RDFSyntax.NTRIPLES;
import static org.apache.commons.rdf.api.RDFSyntax.RDFA_HTML;

import javax.ws.rs.core.Application;

public class ClientTest extends JerseyTest {
    private static final String FRAGMENT_QUERY = "de/ubleipzig/extractor/fragment.annotation.construct.rq";
    private static final String FILTER = "iiif.ub.uni-leipzig.de";
    private static final String REQUEST_URI = "http://localhost:3030/fuseki/fragments";
    private static final String N3 = "application/n-triples";
    private static final JenaRDF rdf = new JenaRDF();
    private static final IOService ioService = new JenaIOService(null);


    @Override
    public Application configure() {

        final Map<String, String> partitions = new HashMap<>();
        partitions.put("repo1", "http://example.org/");
        partitions.put("repo2", "http://example.org/");
        partitions.put("repo3", "http://example.org/");
        partitions.put("repo4", "http://example.org/");

        final Properties properties = new Properties();
        properties.setProperty("title", "The title");
        properties.setProperty("seeAlso", "http://www.trellisldp.org");
        properties.setProperty("publisher", "https://example.org");

        final ResourceConfig config = new ResourceConfig();
        //config.register(new RootResource(ioService, partitions, properties));
        return config;
    }

    @RepeatedTest(20)
    void asyncJDK9() throws Exception {
        String fragmentQuery = QueryUtil.getQuery(FRAGMENT_QUERY, FILTER);
        String in = getAsyncJDK9ClientResponse(fragmentQuery);
        getHtmlSerialization(in);
    }

    @RepeatedTest(20)
    void syncJDK9() throws Exception {
        String fragmentQuery = QueryUtil.getQuery(FRAGMENT_QUERY, FILTER);
        String in = getSyncJDK9ClientResponse(fragmentQuery);
        getHtmlSerialization(in);
    }

    private String getAsyncJDK9ClientResponse(String fragmentQuery) throws IOException, InterruptedException,
            ExecutionException,
            URISyntaxException {
        return HttpClient9.asyncPostQuery(fragmentQuery, REQUEST_URI, N3);
    }

    private String getSyncJDK9ClientResponse(String fragmentQuery) throws IOException, InterruptedException,
            ExecutionException,
            URISyntaxException {
        return HttpClient9.syncPostQuery(fragmentQuery, REQUEST_URI, N3);
    }

    private void getHtmlSerialization(String in) throws Exception {
        try (final Graph graph = getGraph(in)) {
            ioService.write(graph.stream().map(x -> (Triple) x), System.out, RDFA_HTML);
        }
    }

    private Graph getGraph(String in) {
        InputStream stream = new ByteArrayInputStream(in.getBytes(UTF_8));
        final Graph graph = rdf.createGraph();
        ioService.read(stream, null, NTRIPLES).forEach(graph::add);
        return graph;
    }

}
