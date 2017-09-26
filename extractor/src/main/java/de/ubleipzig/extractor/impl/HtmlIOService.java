package de.ubleipzig.extractor.impl;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.jena.JenaRDF;
import org.trellisldp.spi.IOService;
import trellisldp.io.JenaIOService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.rdf.api.RDFSyntax.NTRIPLES;
import static org.apache.commons.rdf.api.RDFSyntax.RDFA_HTML;

public class HtmlIOService {
    private static final JenaRDF rdf = new JenaRDF();
    private static final IOService ioService = new JenaIOService(null);

    public static String getHtmlSerialization(String in) throws Exception {
        try (final Graph graph = getGraph(in)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ioService.write(graph.stream().map(x -> (Triple) x), System.out, RDFA_HTML);
            return out.toString();
        }
    }

    public static String getHtmlSerialization(byte[] in) throws Exception {
        try (final Graph graph = getGraph(in)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ioService.write(graph.stream().map(x -> (Triple) x), out, RDFA_HTML);
            return out.toString();
        }
    }

    public static String getHtmlSerialization(InputStream in) throws Exception {
        try (final Graph graph = getGraph(in)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ioService.write(graph.stream().map(x -> (Triple) x), System.out, RDFA_HTML);
            return out.toString();
        }
    }

    private static Graph getGraph(String in) {
        InputStream stream = new ByteArrayInputStream(in.getBytes(UTF_8));
        final Graph graph = rdf.createGraph();
        ioService.read(stream, null, NTRIPLES).forEach(graph::add);
        return graph;
    }

    private static Graph getGraph(byte[] in) {
        InputStream stream = new ByteArrayInputStream(in);
        final Graph graph = rdf.createGraph();
        ioService.read(stream, null, NTRIPLES).forEach(graph::add);
        return graph;
    }

    private static Graph getGraph(InputStream in) {
        final Graph graph = rdf.createGraph();
        ioService.read(in, null, NTRIPLES).forEach(graph::add);
        return graph;
    }
}
