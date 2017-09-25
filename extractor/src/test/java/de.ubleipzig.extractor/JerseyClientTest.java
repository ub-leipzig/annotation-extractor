package de.ubleipzig.extractor;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.RepeatedTest;

import javax.ws.rs.client.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static de.ubleipzig.extractor.impl.HtmlIOService.getHtmlSerialization;
import static org.apache.jena.riot.WebContent.contentTypeNTriples;

public class JerseyClientTest extends JerseyTest {
    private static final String FRAGMENT_QUERY = "de/ubleipzig/extractor/fragment.annotation.construct.rq";
    private static final String FILTER = "iiif.ub.uni-leipzig.de";
    private static final String REQUEST_URI = "http://localhost:3030/fuseki/fragments";

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

    @RepeatedTest(20)
    void syncJersey() throws Exception {
        String fragmentQuery = QueryUtil.getQuery(FRAGMENT_QUERY, FILTER);
        String in = getSyncJerseyClientResponse(fragmentQuery);
        getHtmlSerialization(in);
    }

    private String getSyncJerseyClientResponse(String fragmentQuery)
            throws IOException, InterruptedException, ExecutionException, URISyntaxException {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(REQUEST_URI);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_FORM_URLENCODED);
        Form form = new Form();
        form.param("query", fragmentQuery);
        Response response = invocationBuilder.accept(contentTypeNTriples)
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));
        return response.readEntity(String.class);
    }
}
