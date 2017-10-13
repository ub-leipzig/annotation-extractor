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

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClientBuilder;

/**
 * Client.
 *
 * @author christopher-johnson
 */
public class Client {

    public static HttpResponse getApacheClientResponse(String requestUri, String accept)
            throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(requestUri);
        get.setHeader("Accept", accept);
        return client.execute(get);
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
}
