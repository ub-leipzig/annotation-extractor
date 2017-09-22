package de.ubleipzig.extractor;

import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import static org.slf4j.LoggerFactory.getLogger;

public class HttpClient9 {
    private static final Logger log = getLogger(Extractor.class);


    public static void syncPut(final String is, String toURI) throws ExecutionException, InterruptedException,
            URISyntaxException, IOException {
        HttpClient testClient;
        testClient = HttpClient.newHttpClient();
        HttpResponse<String> response = testClient.send(
                HttpRequest
                        .newBuilder(new URI(toURI))
                        .headers("Content-Type", "text/n3; charset=UTF-8")
                        .PUT(HttpRequest.BodyProcessor.fromString(is))
                        .build(),
                HttpResponse.BodyHandler.asString()
        );
        int statusCode = response.statusCode();
        log.info(String.valueOf(statusCode));
    }

    public static String syncPostQuery(final String query, String requestURI, String accept) throws ExecutionException, InterruptedException,
            URISyntaxException, IOException {
        HttpClient testClient;
        testClient = HttpClient.newHttpClient();
        String formdata = "query=" + query;
        HttpResponse<String> response = testClient.send(
                HttpRequest
                        .newBuilder(new URI(requestURI))
                        .headers("Accept", accept, "Content-Type",
                                "application/x-www-form-urlencoded; charset=utf-8")
                        .POST(HttpRequest.BodyProcessor.fromString(formdata))
                        .build(),
                HttpResponse.BodyHandler.asString()
        );
        int statusCode = response.statusCode();
        log.info(String.valueOf(statusCode));
        return response.body();
    }

    public static String syncUpdate(final String query) throws ExecutionException, InterruptedException,
            URISyntaxException, IOException {
        HttpClient testClient;
        testClient = HttpClient.newHttpClient();
        String formdata = "update=" + query;
        HttpResponse<String> response = testClient.send(
                HttpRequest
                        .newBuilder(new URI("http://localhost:3030/fuseki/annotations"))
                        .headers("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
                        .POST(HttpRequest.BodyProcessor.fromString(formdata))
                        .build(),
                HttpResponse.BodyHandler.asString()
        );
        int statusCode = response.statusCode();
        log.info(String.valueOf(statusCode));
        return response.body();
    }
}