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
import static org.apache.jena.riot.WebContent.contentTypeNTriples;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

/**
 * JDK9ClientTest.
 *
 * @author christopher-johnson
 */
class JDK9ClientTest extends TestSuite {
    private static final String TEST_QUERY = "de/ubleipzig/extractor/annotation.g.construct.rq";
    private static final String FILTER = "iiif.ub.uni-leipzig.de";
    private static final String REQUEST_URI = "https://localhost:8443/fuseki/annotations?query=";
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
    @DisplayName("asyncJDK9")
    void asyncJDK9() throws Exception {
        String in = HttpClient9.asyncGetQuery(REQUEST_URI + testQuery, contentTypeNTriples);
        System.out.println(in);
        String actual = getHtmlSerialization(in);
        assertNotNull(actual);
    }

    @RepeatedTest(20)
    @DisplayName("syncJDK9")
    void syncJDK9() throws Exception {
        String in = HttpClient9.syncGetQuery(REQUEST_URI + testQuery, contentTypeNTriples);
        System.out.println(in);
        String actual = getHtmlSerialization(in);
        assertNotNull(actual);
    }

    @RepeatedTest(20)
    @DisplayName("asyncJDK9asByteArray")
    void asyncJDK9asByteArray() throws Exception {
        byte[] in = HttpClient9.asyncGetQuery(REQUEST_URI + testQuery, contentTypeNTriples, true);
        String actual = getHtmlSerialization(in);
        assertNotNull(actual);
    }

    @RepeatedTest(20)
    @DisplayName("syncJDK9asByteArray")
    void syncJDK9asByteArray() throws Exception {
        byte[] in = HttpClient9.syncGetQuery(REQUEST_URI + testQuery, contentTypeNTriples, true);
        String actual = getHtmlSerialization(in);
        assertNotNull(actual);
    }
}
