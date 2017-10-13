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

package de.ubleipzig.extractor.impl;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.rdf.api.RDFSyntax.NTRIPLES;
import static org.apache.commons.rdf.api.RDFSyntax.RDFA_HTML;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.jena.JenaRDF;
import org.trellisldp.api.IOService;
import org.trellisldp.io.JenaIOService;

/**
 * HTMLIOService.
 *
 * @author christopher-johnson
 */
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
