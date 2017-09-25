package de.ubleipzig.extractor;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.jena.JenaRDF;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.trellisldp.spi.IOService;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Stream.of;
import static org.apache.jena.graph.Factory.createDefaultGraph;
import static org.apache.jena.graph.NodeFactory.createBlankNode;
import static org.apache.jena.graph.NodeFactory.createLiteral;

public class HtmlSerializer {
    private static final String FRAGMENT_QUERY = "de/ubleipzig/extractor/fragment.annotation.construct.rq";
    private static final String FILTER = "iiif.ub.uni-leipzig.de";
    private static final String REQUEST_URI = "http://localhost:3030/fuseki/fragments";
    private static final String N3 = "application/n-triples";
    private IOService service;
    private static final JenaRDF rdf = new JenaRDF();

    public static void main(String[] args) throws Exception {
        HtmlSerializer app = new HtmlSerializer();
        String in = app.getFragmentAnnotationGraph();
        app.getHtmlSerialization(in);
    }

    public String getFragmentAnnotationGraph() throws IOException, InterruptedException, ExecutionException,
            URISyntaxException {
        String fragmentQuery = QueryUtil.getQuery(FRAGMENT_QUERY, FILTER);
        return HttpClient9.syncPostQuery(fragmentQuery,REQUEST_URI, N3);
    }

    public void getHtmlSerialization(String in) throws UnsupportedEncodingException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        Graph graph = getGraph(in);
        //final Stream<Triple> stream = graph.stream().map();
        //service.write(stream, out, RDFA_HTML);
        final String html = new String(out.toByteArray(), UTF_8);
    }

    private Graph getGraph(String in) throws UnsupportedEncodingException {
        InputStream stream = new ByteArrayInputStream(in.getBytes(StandardCharsets.UTF_8.name()));
        final org.apache.jena.graph.Graph graph = createDefaultGraph();
        RDFDataMgr.read(graph, stream, Lang.NTRIPLES);
        return rdf.asGraph(graph);
    }

}
