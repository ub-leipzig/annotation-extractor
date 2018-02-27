import static de.ubleipzig.compliance.Client.putApacheClientResponse;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.rdf.api.RDFSyntax.NTRIPLES;

import com.github.jsonldjava.core.JsonLdConsts;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import de.ubleipzig.extractor.QueryUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.UUID;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.jena.JenaRDF;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.trellisldp.api.IOService;
import org.trellisldp.vocabulary.RDF;

public class HarvesterTest {
    static final JenaRDF rdf = new JenaRDF();
    private static final IOService ioService = new JenaIOService();
    private static String trellisBase = "http://localhost:8080/repository";
    static Graph graph;
    static Model model;
    static String testResource = "https://graph.global/static/data/universes/iiif/e-codices.json";

    static String useragent = "LDP Bot/0.1.0 (christopher_hanna.johnson@uni-leipzig.de)";

    @BeforeClass
    public static void setUp() throws IOException, JsonLdError {
        URL uri = new URL(testResource);
        graph = getGraph(expandDocumentToN3(uri));
        org.apache.jena.graph.Graph jenaGraph = rdf.asJenaGraph(graph);
        model = ModelFactory.createModelForGraph(jenaGraph);
    }

    @Test
    public void getDomainCollection() throws IOException, JsonLdError {
        final String testQuery = "query/collection.rq";
        String q = QueryUtil.getQuery(testQuery, testResource);
        Query query = QueryFactory.create(q);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        Graph graph = rdf.asGraph(qexec.execConstruct());
        graph.stream(null, RDF.first, null).forEach(t -> {
            String uri = ((IRI) t.getObject()).getIRIString();
            InputStream is = null;
            try {
                is = getApacheClientResponse(uri, "application/ld+json");
            } catch (IOException e) {
                e.printStackTrace();
            }
            UUID uuid = UUID.randomUUID();
            String resourceUri = trellisBase + "/manifest/" + uuid;
            HttpResponse res = null;
            try {
                res = putApacheClientResponse(resourceUri, is, "application/ld+json");
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(res.getStatusLine().toString());
        });
    }

    private static InputStream getApacheClientResponse(String uri, String accept)
            throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(uri);
        get.setHeader("Accept", accept);
        get.setHeader("User-Agent", useragent);
        HttpResponse response = client.execute(get);
        HttpEntity out = response.getEntity();
        return out.getContent();
    }

    private static InputStream expandDocumentToN3(final URL testUri)
            throws IOException, JsonLdError {
        JsonLdOptions options = new JsonLdOptions();
        options.format = JsonLdConsts.APPLICATION_NQUADS;
        Object expanded = JsonLdProcessor.toRDF(JsonUtils.fromInputStream(
                getApacheClientResponse(testUri.toString(), "application/ld+json")), options);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Writer writer = new OutputStreamWriter(out, UTF_8);
        writer.write(String.valueOf(expanded));
        writer.flush();
        return new ByteArrayInputStream(out.toByteArray());
    }

    private static Graph getGraph(InputStream stream) {
        final Graph graph = rdf.createGraph();
        ioService.read(stream, null, NTRIPLES).forEachOrdered(graph::add);
        return graph;
    }
}
