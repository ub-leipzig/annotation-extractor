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

package de.ubleipzig.constructor;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static org.apache.commons.rdf.api.RDFSyntax.NTRIPLES;
import static org.apache.jena.rdf.model.ModelFactory.createDefaultModel;
import static org.apache.jena.riot.Lang.N3;
import static org.apache.jena.riot.RDFDataMgr.read;
import static org.slf4j.LoggerFactory.getLogger;

import com.github.jsonldjava.core.JsonLdConsts;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import de.ubleipzig.vocabulary.JSONReader;
import de.ubleipzig.vocabulary.SC;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.jena.JenaRDF;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.WriterDatasetRIOT;
import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.riot.system.RiotLib;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.util.Context;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.trellisldp.api.IOService;
import org.trellisldp.api.NamespaceService;
import org.trellisldp.io.JenaIOService;

/**
 * TestSuite.
 *
 * @author christopher-johnson
 */

public abstract class IIIFTestSuite {
    Logger LOG = getLogger(IIIFTestSuite.class.getName());

    static InputStream context = DomainProperties.context();

    static Graph graph;

    static Model model;

    static NamespaceService namespaceService;

    static IOService service;

    final List<IRI> models = asList(SC.Manifest);

    static List<String> testResources = new ArrayList<>(getTestResourcesFromJson().values());

    static String testResource;

    static Object testFrame;

    private static String testCLIResource = System.getProperty("test.resource");

    static final JenaRDF rdf = new JenaRDF();

    private static final IOService ioService = new JenaIOService(null);

    @BeforeClass
    public static void setUp() throws IOException, JsonLdError {
        //         testResource = testCLIResource;
        testResource = getTestResourcesFromJson().get(DomainProperties.testResourceKey());
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream fs = classloader.getResourceAsStream("cache/frame.json");
        testFrame = JsonUtils.fromInputStream(fs);
        service = new JenaIOService(namespaceService);
        URL uri = new URL(testResource);
        graph = getGraph(expandDocumentToN3(uri));
        org.apache.jena.graph.Graph jenaGraph = rdf.asJenaGraph(graph);
        model = ModelFactory.createModelForGraph(jenaGraph);
    }

    private static Map<String, String> getTestResourcesFromJson() {
        final URL res = JSONReader.class.getResource(DomainProperties.testResources);
        final JSONReader svc = new JSONReader(res.getPath());
        return svc.getNamespaces();
    }

    private static InputStream expandDocumentToN3(final URL testUri)
            throws IOException, JsonLdError {
        JsonLdOptions options = new JsonLdOptions();
        options.format = JsonLdConsts.APPLICATION_NQUADS;
        Object expanded = JsonLdProcessor
                .toRDF(JsonUtils.fromInputStream(getApacheClientResponse(testUri.toString())),
                        options);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Writer writer = new OutputStreamWriter(out, UTF_8);
        writer.write(String.valueOf(expanded));
        writer.flush();
        return new ByteArrayInputStream(out.toByteArray());
    }

    static Optional<? extends Triple> closeableFindAny(Stream<? extends Triple> stream) {
        try (Stream<? extends Triple> s = stream) {
            return s.findAny();
        }
    }

    private static InputStream getApacheClientResponse(String uri) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(uri);
        HttpResponse response = client.execute(get);
        HttpEntity out = response.getEntity();
        return out.getContent();
    }

    public static HttpResponse headApacheClientResponse(String requestUri, String accept)
            throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpHead head = new HttpHead(requestUri);
        head.setHeader("Accept", accept);
        return client.execute(head);
    }

    private static Graph getGraph(InputStream stream) {
        final Graph graph = rdf.createGraph();
        ioService.read(stream, null, NTRIPLES).forEach(graph::add);
        return graph;
    }

    private Graph asGraphfromFile(final String resource, final String context) {
        final Model model = createDefaultModel();
        read(model, getClass().getResourceAsStream(resource), context, N3);
        return rdf.asGraph(model);
    }

    private Model asJenaModelfromFile(final String resource, final String context) {
        final Model model = createDefaultModel();
        read(model, getClass().getResourceAsStream(resource), context, N3);
        return model;
    }

    public static void saveFile(String graphs) throws IOException {
        String p = "expanded.n3";
        //String p = this.getClass().getResource("annotations.n3").getPath();
        PrintWriter writer = new PrintWriter(p);
        writer.write(graphs);
    }

    public String toString(Model m, RDFFormat f, Context jenaContext) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            WriterDatasetRIOT w = RDFDataMgr.createDatasetWriter(f);
            DatasetGraph g = DatasetFactory.create(m).asDatasetGraph();
            PrefixMap pm = RiotLib.prefixMap(g);
            String base = null;
            w.write(out, g, pm, base, jenaContext);
            out.flush();
            return out.toString("UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

