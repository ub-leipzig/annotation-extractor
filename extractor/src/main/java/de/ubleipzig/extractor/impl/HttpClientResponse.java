package de.ubleipzig.extractor.impl;

import de.ubleipzig.extractor.HttpClient9;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import static org.apache.jena.riot.WebContent.contentTypeNTriples;

public class HttpClientResponse {
    public static String getAsyncJDK9ClientResponse(String fragmentQuery, String REQUEST_URI) throws IOException, InterruptedException,
            ExecutionException, URISyntaxException {
        return HttpClient9.asyncPostQuery(fragmentQuery, REQUEST_URI, contentTypeNTriples);
    }

    public static String getSyncJDK9ClientResponse(String fragmentQuery, String REQUEST_URI) throws IOException, InterruptedException,
            ExecutionException,
            URISyntaxException {
        return HttpClient9.syncPostQuery(fragmentQuery, REQUEST_URI, contentTypeNTriples);
    }

}
