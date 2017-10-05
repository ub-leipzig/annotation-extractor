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

import static org.apache.jena.riot.WebContent.contentTypeJSONLD;

import com.github.jsonldjava.utils.JsonUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

import org.apache.http.Header;
import org.apache.jena.graph.Factory;
import org.apache.jena.graph.Graph;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RIOT;
import org.apache.jena.sparql.util.Context;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AbstractApiTest.
 *
 * @author acoburn
 * @author christopher-johnson
 */

public abstract class AbstractApiTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractApiTest.class);

    public abstract InputStream context();

    public abstract Map<String, String> testURI();

    private org.apache.commons.rdf.api.Graph graph;

    public abstract String domain();

    public abstract String testResourceKey();

    public Boolean isStrict() {
        return true;
    }

    private Graph getTestGraph(final String testUri, String jsonldContext) throws IOException {
        final Graph graph = Factory.createDefaultGraph();
        Context ctx = new Context();
        Object jsonldContextAsObject = JsonUtils.fromInputStream(
                new ByteArrayInputStream(jsonldContext.getBytes(StandardCharsets.UTF_8)));
        ctx.set(RIOT.JSONLD_CONTEXT, jsonldContextAsObject);
        RDFParser.create().source(testUri).lang(Lang.JSONLD).context(ctx).parse(graph);
        return graph;
    }

    @Test
    public void testRequiredHeaders() throws IOException, URISyntaxException, InterruptedException {
        testURI().forEach((key, value) -> {
            org.apache.http.HttpResponse response;
            try {
                response = Client.getApacheClientResponse(value, contentTypeJSONLD);
                Header[] headers = new Header[0];
                if (response != null) {
                    headers = response.getAllHeaders();
                }
                LOGGER.info(Arrays.toString(headers));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}

