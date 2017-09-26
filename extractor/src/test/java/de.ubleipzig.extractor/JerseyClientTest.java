package de.ubleipzig.extractor;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.runner.RunWith;

import javax.ws.rs.client.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static de.ubleipzig.extractor.impl.HtmlIOService.getHtmlSerialization;
import static org.apache.jena.riot.WebContent.contentTypeNTriples;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(JUnitPlatform.class)
@IncludeEngines("junit-jupiter")
@SelectPackages("de.ubleipzig.extractor")
public class JerseyClientTest extends JerseyTest {
    private static final String TEST_QUERY = "de/ubleipzig/extractor/annotation.g.construct.rq";
    private static final String FILTER = "iiif.ub.uni-leipzig.de";
    private static final String REQUEST_URI = "http://localhost:3030/fuseki/annotations?query=";
    private String testQuery;
    private String expected;

    @Override
    public Application configure() {

        final Map<String, String> partitions = new HashMap<>();
        partitions.put("repo1", "http://example.org/");
        partitions.put("repo2", "http://example.org/");
        partitions.put("repo3", "http://example.org/");
        partitions.put("repo4", "http://example.org/");

        final Properties properties = new Properties();
        properties.setProperty("title", "The title");
        properties.setProperty("seeAlso", "http://www.trellisldp.org");
        properties.setProperty("publisher", "https://example.org");

        final ResourceConfig config = new ResourceConfig();
        //config.register(new RootResource(ioService, partitions, properties));
        return config;
    }

    @BeforeEach
    public void setUp() throws IOException {
        testQuery = QueryUtil.getQuery(TEST_QUERY, FILTER, true);
        InputStream is = getClass().getResourceAsStream("/expected-html.out");
        expected = TestSuite.streamToString(is);
    }

    @RepeatedTest(20)
    @DisplayName("syncJersey")
    void syncJersey() throws Exception {
        String in = getSyncJerseyClientResponse();
        String actual = getHtmlSerialization(in);
        assertNotNull(actual);
    }

    @RepeatedTest(20)
    @DisplayName("syncJerseyInputStream")
    void syncJerseyInputStream() throws Exception {
        InputStream in = getSyncJerseyClientResponse(true);
        String actual = getHtmlSerialization(in);
        assertNotNull(actual);
    }

    private String getSyncJerseyClientResponse()
            throws IOException, InterruptedException, ExecutionException, URISyntaxException {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(REQUEST_URI + testQuery);
        Invocation.Builder invocationBuilder = webTarget
                .request();
        Response response = invocationBuilder
                .accept(contentTypeNTriples)
                .get();
        return response.readEntity(String.class);
    }

    private InputStream getSyncJerseyClientResponse(boolean optimized)
            throws IOException, InterruptedException, ExecutionException, URISyntaxException {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(REQUEST_URI + testQuery);
        Invocation.Builder invocationBuilder = webTarget
                .request();
        Response response = invocationBuilder
                .accept(contentTypeNTriples)
                .get();
        return response.readEntity(InputStream.class);
    }
}
