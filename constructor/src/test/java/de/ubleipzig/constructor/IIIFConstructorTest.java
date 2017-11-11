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

import static org.apache.commons.rdf.api.RDFSyntax.JSONLD;
import static org.apache.jena.riot.RDFFormat.JSONLD_FRAME_PRETTY;
import static org.trellisldp.vocabulary.JSONLD.compacted;

import de.ubleipzig.extractor.QueryUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.rdf.api.Graph;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.JsonLDWriteContext;
import org.junit.Test;

public class IIIFConstructorTest extends IIIFTestSuite {

    @Test
    public void testStreamingConstruct() throws IOException {
        final String testQuery = "query/all.rq";
        String q = QueryUtil.getQuery(testQuery, testResource);
        Query query = QueryFactory.create(q);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        Model results = qexec.execConstruct();
        Graph newGraph = rdf.asGraph(results);
        LOG.info("input model has " + model.size() + " triples");
        LOG.info("output graph has " + newGraph.size() + " triples");
        JsonLDWriteContext jenaCtx = new JsonLDWriteContext();
        jenaCtx.setFrame(testFrame);
        String s = toString(results, JSONLD_FRAME_PRETTY, jenaCtx);
        LOG.info(s);
    }

    @Test
    public void testGetImagesAsSet() throws IOException {
        final String testQuery = "query/images.rq";
        String q = QueryUtil.getQuery(testQuery, testResource);
        Query query = QueryFactory.create(q);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        Model results = qexec.execConstruct();
        Graph newGraph = rdf.asGraph(results);
        LOG.info("input model has " + model.size() + " triples");
        LOG.info("output graph has " + newGraph.size() + " triples");
        JsonLDWriteContext jenaCtx = new JsonLDWriteContext();
        jenaCtx.setFrame(testFrame);
        String s = toString(results, JSONLD_FRAME_PRETTY, jenaCtx);
        LOG.info(s);
    }

    @Test
    public void testGetFramedMetadata() throws IOException {
        final String testQuery = "query/metadata.rq";
        String q = QueryUtil.getQuery(testQuery, testResource);
        Query query = QueryFactory.create(q);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        Model results = qexec.execConstruct();
        LOG.info("input model has " + model.size() + " triples");
        LOG.info("result set has " + results.size() + " triples");
        JsonLDWriteContext jenaCtx = new JsonLDWriteContext();
        jenaCtx.setFrame(testFrame);
        String s = toString(results, JSONLD_FRAME_PRETTY, jenaCtx);
        LOG.info(s);
    }

    @Test
    public void testGetCompactedMetadata() throws IOException {
        final String testQuery = "query/metadata.rq";
        String q = QueryUtil.getQuery(testQuery, testResource);
        Query query = QueryFactory.create(q);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        Model results = qexec.execConstruct();
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        LOG.info("input model has " + model.size() + " triples");
        LOG.info("result set has " + results.size() + " triples");
        service.write(rdf.asGraph(results).stream(), out, JSONLD, compacted);
        LOG.info(out.toString());
    }
}
