package de.ubleipzig.extractor;

import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.apache.jena.riot.WebContent.contentTypeNTriples;
import static org.apache.jena.riot.WebContent.contentTypeSPARQLQuery;

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

    public static void asyncPut(final String is, String toURI) throws ExecutionException, InterruptedException,
            URISyntaxException, IOException {
        HttpClient client;
        client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder(new URI(toURI))
                .headers("Content-Type", contentTypeNTriples)
                .PUT(HttpRequest.BodyProcessor.fromString(is))
                .build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request, HttpResponse.BodyHandler.discard
                (null));
    }

    public static byte[] syncGetQuery(final String query, String accept, boolean optimized) throws
            ExecutionException, InterruptedException, URISyntaxException, IOException {
        HttpClient testClient;
        testClient = HttpClient.newHttpClient();
        HttpResponse<byte[]> response = testClient.send(
                HttpRequest
                        .newBuilder(new URI(query))
                        .headers("Content-Type", contentTypeSPARQLQuery, "Accept", accept)
                        .GET()
                        .build(),
                HttpResponse.BodyHandler.asByteArray()
        );

        log.info(String.valueOf(response.version()));
        log.info(String.valueOf(response.statusCode()));
        return response.body();
    }

    public static String syncGetQuery(String query, String accept) throws
            ExecutionException, InterruptedException, URISyntaxException, IOException {
        HttpClient testClient;
        testClient = HttpClient.newHttpClient();
        HttpResponse<String> response = testClient.send(
                HttpRequest
                        .newBuilder(new URI(query))
                        .headers("Content-Type", contentTypeSPARQLQuery, "Accept", accept)
                        .GET()
                        .build(),
                HttpResponse.BodyHandler.asString()
        );

        log.info(String.valueOf(response.version()));
        log.info(String.valueOf(response.statusCode()));
        return response.body();
    }

    public static byte[] asyncGetQuery(final String query, String accept, boolean optimized) throws
            ExecutionException, InterruptedException, URISyntaxException, IOException {
        HttpClient client;
        client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(query))
                .headers("Content-Type", contentTypeSPARQLQuery, "Accept", accept)
                .GET()
                .build();
        CompletableFuture<HttpResponse<byte[]>> response = client.sendAsync(request, HttpResponse.BodyHandler
                .asByteArray());
        log.info(String.valueOf(response.get().version()));
        log.info(String.valueOf(response.get().statusCode()));
        return response.get().body();
    }

    public static String asyncGetQuery(final String query, String accept) throws
            ExecutionException, InterruptedException, URISyntaxException, IOException {
        HttpClient client;
        client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(query))
                .headers("Content-Type", contentTypeSPARQLQuery, "Accept", accept)
                .GET()
                .build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request, HttpResponse.BodyHandler
                .asString());
        log.info(String.valueOf(response.get().version()));
        log.info(String.valueOf(response.get().statusCode()));
        return response.get().body();
    }

    public static String syncUpdate(final String query) throws ExecutionException, InterruptedException,
            URISyntaxException, IOException {
        HttpClient testClient;
        testClient = HttpClient.newHttpClient();
        String formdata = "update=" + query;
        HttpResponse<String> response = testClient.send(
                HttpRequest
                        .newBuilder(new URI("http://localhost:3030/fuseki/annotations"))
                        .headers("Content-Type", contentTypeSPARQLQuery)
                        .POST(HttpRequest.BodyProcessor.fromString(formdata))
                        .build(),
                HttpResponse.BodyHandler.asString()
        );
        log.info(String.valueOf(response.version()));
        log.info(String.valueOf(response.statusCode()));
        return response.body();
    }
}