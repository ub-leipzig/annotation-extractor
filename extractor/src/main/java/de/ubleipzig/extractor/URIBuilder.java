package de.ubleipzig.extractor;

import com.github.jsonldjava.core.JsonLdError;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class URIBuilder {
    private static final String REQUEST_URI = "http://localhost:3030/fuseki/annotations?query=";
    private static final String TEST_QUERY = "de/ubleipzig/extractor/annotation.g.construct.rq";
    private static final String FILTER = "iiif.ub.uni-leipzig.de";

    public static void main(String[] args)
            throws IOException, JsonLdError, InterruptedException, ExecutionException, URISyntaxException {
        buildURI();
    }

    public static void buildURI() throws IOException, URISyntaxException {
        String testQuery = QueryUtil.getQuery(TEST_QUERY, FILTER, true);
        URI uri = new URI(REQUEST_URI + testQuery);
    }
}
