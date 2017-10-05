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

import static de.ubleipzig.extractor.impl.HtmlIOService.getHtmlSerialization;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.jena.riot.WebContent.contentTypeNTriples;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

/**
 * ApacheClientTest.
 *
 * @author christopher-johnson
 */
class ApacheClientTest extends TestSuite {
    private static final String TEST_QUERY = "de/ubleipzig/extractor/annotation.g.construct.rq";
    private static final String FILTER = "iiif.ub.uni-leipzig.de";
    private static final String REQUEST_URI = "http://localhost:3030/fuseki/annotations?query=";
    private String testQuery;
    private InputStream is;
    private String expected;

    @BeforeEach
    public void setUp() throws IOException {
        testQuery = QueryUtil.getQuery(TEST_QUERY, FILTER, true);
        InputStream is = getClass().getResourceAsStream("/expected-html.out");
        expected = TestSuite.streamToString(is);
    }

    @RepeatedTest(20)
    @DisplayName("syncApache")
    void syncApache() throws Exception {
        String in = getApacheClientResponse();
        String actual = getHtmlSerialization(in);
        assertNotNull(actual);
    }

    @RepeatedTest(20)
    @DisplayName("syncApacheInputStream")
    void syncApacheInputStream() throws Exception {
        InputStream in = getApacheClientResponse(true);
        String actual = getHtmlSerialization(in);
        assertNotNull(actual);
    }

    private String getApacheClientResponse() throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(REQUEST_URI + testQuery);
        get.setHeader("Accept", contentTypeNTriples);
        HttpResponse response = client.execute(get);
        HttpEntity out = response.getEntity();
        return EntityUtils.toString(out, UTF_8);
    }

    private InputStream getApacheClientResponse(boolean optimized) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(REQUEST_URI + testQuery);
        get.setHeader("Accept", contentTypeNTriples);
        HttpResponse response = client.execute(get);
        HttpEntity out = response.getEntity();
        return out.getContent();
    }
}
