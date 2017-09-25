package de.ubleipzig.extractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.RepeatedTest;

import static de.ubleipzig.extractor.impl.HtmlIOService.getHtmlSerialization;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.jena.riot.WebContent.contentTypeNTriples;

class ApacheClientTest {
    private static final String FRAGMENT_QUERY = "de/ubleipzig/extractor/fragment.annotation.construct.rq";
    private static final String FILTER = "iiif.ub.uni-leipzig.de";
    private static final String REQUEST_URI = "http://localhost:3030/fuseki/fragments";

    @RepeatedTest(20)
    void syncApache() throws Exception {
        String fragmentQuery = QueryUtil.getQuery(FRAGMENT_QUERY, FILTER);
        String in = getApacheClientResponse(fragmentQuery);
        if (in != null) {
            getHtmlSerialization(in);
        }
    }

    private static String getApacheClientResponse(String fragmentQuery) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(REQUEST_URI);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("query", fragmentQuery));
        post.setEntity(new UrlEncodedFormEntity(params));
        post.setHeader("Accept", contentTypeNTriples);
        HttpResponse response = client.execute(post);
        HttpEntity out = response.getEntity();
        return EntityUtils.toString(out, UTF_8);
    }
}
