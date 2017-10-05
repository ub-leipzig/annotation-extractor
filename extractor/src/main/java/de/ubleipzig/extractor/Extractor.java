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

package de.ubleipzig.extractor;

import static org.slf4j.LoggerFactory.getLogger;

import com.github.jsonldjava.core.JsonLdError;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.slf4j.Logger;

/**
 * Extractor.
 *
 * @author christopher-johnson
 */
public class Extractor {
    private static final Logger log = getLogger(Extractor.class);
    private static final String BASE = "http://ub.uni-leipzig.de";
    private static final String destinationGraph = "https://localhost:8443/fuseki/annotations";

    public static void main(String[] args)
            throws IOException, JsonLdError, InterruptedException, ExecutionException,
            URISyntaxException {
        Extractor app = new Extractor();
        List<String> graphs = app.selectAnnotations();
        String data = null;
        if (graphs != null) {
            data = graphs.get(0);
        }
        HttpClient9.syncPut(data, destinationGraph);
        //saveFile(data);
    }

    private Connection connect() {
        String url = "jdbc:sqlite:/mnt/mirador-data/development.sqlite3";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    private List<String> selectAnnotations() throws IOException, JsonLdError {
        String sql = "SELECT data FROM annotations WHERE user_id='6'";
        List<String> rowset = new ArrayList<>();
        List<String> graphs = new ArrayList<>();
        Model m = ModelFactory.createDefaultModel();
        StringWriter writer = new StringWriter();
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String json = StringEscapeUtils.unescapeJson(rs.getString("data"));
                log.info("getting JSON-LD annotation from database");
                json = json.substring(1, json.length() - 1);
                rowset.add(json);
            }
            InputStream is = new ByteArrayInputStream(
                    rowset.toString().getBytes(StandardCharsets.UTF_8.name()));
            log.info("reading annotations from model");
            m.read(is, BASE, "JSON-LD");
            RDFDataMgr.write(writer, m, Lang.NQUADS);
            log.info("writing annotations as N3 from model into output");
            graphs.add(writer.toString());
            return graphs;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


    public static void saveFile(String graphs) throws IOException {
        String p = "annotations.n3";
        //String p = this.getClass().getResource("annotations.n3").getPath();
        PrintWriter writer = new PrintWriter(p);
        writer.write(graphs);
        log.info("saving annotations to file");
    }
}