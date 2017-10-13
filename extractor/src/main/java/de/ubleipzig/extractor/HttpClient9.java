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

import static jdk.incubator.http.HttpClient.Version.HTTP_2;
import static jdk.incubator.http.HttpResponse.BodyHandler.asByteArray;
import static jdk.incubator.http.HttpResponse.BodyHandler.asString;
import static org.apache.jena.riot.WebContent.contentTypeNTriples;
import static org.apache.jena.riot.WebContent.contentTypeSPARQLQuery;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.net.ssl.SSLContext;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;
import org.slf4j.Logger;

/**
 * HttpClient9.
 *
 * @author christopher-johnson
 */
public class HttpClient9 {
    private static final Logger log = getLogger(Extractor.class);
    static HttpClient client = null;
    static ExecutorService exec;
    static SSLContext sslContext;

    public static HttpClient getClient() {
        if (client == null) {
            exec = Executors.newCachedThreadPool();
            client = HttpClient.newBuilder().executor(exec)
                    //.sslContext(sslContext)
                    .version(HTTP_2).build();
        }
        return client;
    }

    public static void syncPut(final String is, String toURI)
            throws ExecutionException, InterruptedException, URISyntaxException, IOException {
        client = getClient();
        HttpRequest req = HttpRequest.newBuilder(new URI(toURI))
                .headers("Content-Type", "text/n3; charset=UTF-8")
                .PUT(HttpRequest.BodyProcessor.fromString(is)).build();
        HttpResponse<String> response = client.send(req, asString());
        int statusCode = response.statusCode();
        log.info(String.valueOf(statusCode));
    }

    public static void asyncPut(final String is, String toURI)
            throws ExecutionException, InterruptedException, URISyntaxException, IOException {
        client = getClient();
        HttpRequest req =
                HttpRequest.newBuilder(new URI(toURI)).headers("Content-Type", contentTypeNTriples)
                        .PUT(HttpRequest.BodyProcessor.fromString(is)).build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(req, asString());
    }

    public static byte[] syncGetQuery(final String query, String accept, boolean optimized)
            throws ExecutionException, InterruptedException, URISyntaxException, IOException {
        client = getClient();
        HttpRequest req = HttpRequest.newBuilder(new URI(query))
                .headers("Content-Type", contentTypeSPARQLQuery, "Accept", accept).GET().build();
        HttpResponse<byte[]> response = client.send(req, asByteArray());

        log.info(String.valueOf(response.version()));
        log.info(String.valueOf(response.statusCode()));
        return response.body();
    }

    public static String syncGetQuery(String query, String accept)
            throws ExecutionException, InterruptedException, URISyntaxException, IOException {
        client = getClient();
        HttpRequest req = HttpRequest.newBuilder(new URI(query))
                .headers("Content-Type", contentTypeSPARQLQuery, "Accept", accept).GET().build();
        HttpResponse<String> response = client.send(req, asString());

        log.info(String.valueOf(response.version()));
        log.info(String.valueOf(response.statusCode()));
        return response.body();
    }

    public static byte[] asyncGetQuery(final String query, String accept, boolean optimized)
            throws ExecutionException, InterruptedException, URISyntaxException, IOException {
        client = getClient();
        HttpRequest req = HttpRequest.newBuilder().uri(new URI(query))
                .headers("Content-Type", contentTypeSPARQLQuery, "Accept", accept).GET().build();
        CompletableFuture<HttpResponse<byte[]>> response = client.sendAsync(req, asByteArray());
        log.info(String.valueOf(response.get().version()));
        log.info(String.valueOf(response.get().statusCode()));
        return response.get().body();
    }

    public static String asyncGetQuery(final String query, String accept)
            throws ExecutionException, InterruptedException, URISyntaxException, IOException {
        client = getClient();
        HttpRequest request = HttpRequest.newBuilder().uri(new URI(query))
                .headers("Content-Type", contentTypeSPARQLQuery, "Accept", accept).GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request, asString());
        log.info(String.valueOf(response.get().version()));
        log.info(String.valueOf(response.get().statusCode()));
        return response.get().body();
    }

    public static String syncUpdate(final String query)
            throws ExecutionException, InterruptedException, URISyntaxException, IOException {
        client = getClient();
        String formdata = "update=" + query;
        HttpRequest req =
                HttpRequest.newBuilder(new URI("http://localhost:3030/fuseki/annotations"))
                        .headers("Content-Type", contentTypeSPARQLQuery)
                        .POST(HttpRequest.BodyProcessor.fromString(formdata)).build();
        HttpResponse<String> response = client.send(req, asString());
        log.info(String.valueOf(response.version()));
        log.info(String.valueOf(response.statusCode()));
        return response.body();
    }
}