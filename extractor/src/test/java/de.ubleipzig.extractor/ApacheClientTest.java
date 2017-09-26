package de.ubleipzig.extractor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import static de.ubleipzig.extractor.impl.HtmlIOService.getHtmlSerialization;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.jena.riot.WebContent.contentTypeNTriples;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
