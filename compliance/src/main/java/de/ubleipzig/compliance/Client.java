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

package de.ubleipzig.compliance;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

/**
 * Client.
 *
 * @author christopher-johnson
 */
public class Client {
    private static final Logger LOGGER = getLogger(Client.class);

    public static HttpResponse getApacheClientResponse(String requestUri, String accept)
            throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(requestUri);
        get.setHeader("Accept", accept);
        return client.execute(get);
    }

    public static HttpResponse putApacheClientResponse(String requestUri, InputStream is,
                                                       String contentType) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPut put = new HttpPut(requestUri);
        put.setEntity(new InputStreamEntity(is));
        put.setHeader("Content-Type", contentType);
        return client.execute(put);
    }


    public static HttpResponse headApacheClientResponse(String requestUri, String accept)
            throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpHead head = new HttpHead(requestUri);
        head.setHeader("Accept", accept);
        return client.execute(head);
    }

    public static CloseableHttpClient getCachingClient() {
        CacheConfig cacheConfig =
                CacheConfig.custom().setMaxCacheEntries(1000).setMaxObjectSize(1024 * 128).build();
        return CachingHttpClientBuilder.create().setCacheConfig(cacheConfig).build();
    }

    /**
     * Retrieve all header values
     *
     * @param response response from request
     * @return Map of all values for all response headers
     */
    private static Map<String, List<String>> getHeaders(final HttpResponse response) {
        final Map<String, List<String>> headers = new HashMap<>();

        for (Header header : response.getAllHeaders()) {
            List<String> values;
            if (headers.containsKey(header.getName())) {
                values = headers.get(header.getName());
            } else {
                values = new ArrayList<>();
                headers.put(header.getName(), values);
            }
            values.add(header.getValue());
        }
        return headers;
    }

    private static void free(final CloseableHttpResponse response) {
        // Free resources associated with the response.
        try {
            response.close();
        } catch (IOException e) {
            LOGGER.warn("Unable to close HTTP response.", e);
        }
    }
}
