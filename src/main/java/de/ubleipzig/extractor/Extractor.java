package de.ubleipzig.extractor;

import java.io.*;
import java.sql.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFLib;
import org.apache.jena.sparql.graph.GraphFactory;

import static org.apache.jena.riot.system.StreamRDFLib.writer;

public class Extractor {
    private static final String RESOURCE_DIR = "de/ubleipzig/extractor";

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

    public void selectAnnotations() throws IOException, JsonLdError {
        String sql = "SELECT data FROM annotations";
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        final JsonLdOptions options = new JsonLdOptions();
        final InputStream contextStream = cl
                .getResourceAsStream(RESOURCE_DIR+ "/" + "context.json");
        //final Object contextJson = JsonUtils.fromInputStream(contextStream);
        Model m = ModelFactory.createDefaultModel();
        try (Connection conn = this.connect(); Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String json = StringEscapeUtils.unescapeJson(rs.getString("data"));
                json = json.substring(1, json.length() - 1);
                Object res = JsonUtils.fromString(json);
                Object out = JsonLdProcessor.toRDF(res, options);
                String graph = JsonUtils.toString(out);
                RDFDataMgr.read(m, graph, Lang.NQUADS);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException, JsonLdError {
        Extractor app = new Extractor();
        app.selectAnnotations();
    }
}
