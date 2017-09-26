package de.ubleipzig.extractor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.lang3.StringEscapeUtils;

import static de.ubleipzig.extractor.impl.HtmlIOService.getHtmlSerialization;
import static org.apache.commons.text.StringEscapeUtils.unescapeHtml4;
import static org.apache.jena.riot.WebContent.contentTypeNTriples;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
