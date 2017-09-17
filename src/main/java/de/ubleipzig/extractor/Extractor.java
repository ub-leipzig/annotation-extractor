package de.ubleipzig.extractor;

import static org.slf4j.LoggerFactory.getLogger;

import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.utils.JsonUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.slf4j.Logger;

public class Extractor {
    private static final Logger log = getLogger(Extractor.class);
    private static final String RESOURCE_DIR = "de/ubleipzig/extractor";
    private static final String BASE = "http://ub.uni-leipzig.de";


    public static void main(String[] args) throws IOException, JsonLdError {
        Extractor app = new Extractor();
        List<String> graphs = app.selectAnnotations();
        app.saveFile(graphs);
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
        String sql = "SELECT data, id FROM annotations";
        List<String> graphs = new ArrayList<>();
        Model m = ModelFactory.createDefaultModel();
        StringWriter writer = new StringWriter();
        try (Connection conn = this.connect(); Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String json = StringEscapeUtils.unescapeJson(rs.getString("data"));
                log.info("getting JSON-LD annotation from database");
                json = json.substring(1, json.length() - 1);
                InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8.name()));
                m.read(is, BASE, "JSON-LD");
            }
            log.info("reading annotations from model");
            RDFDataMgr.write(writer, m, Lang.NQUADS);
            log.info("writing annotations as N3 from model into output");
            graphs.add(writer.toString());
            return graphs;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


    private void saveFile(List<String> graphs) throws IOException {
        Path out = Paths.get(RESOURCE_DIR + "/" + "annotations.n3");
        Files.write(out,graphs, Charset.defaultCharset());
        log.info("saving annotations to file");
    }
}
