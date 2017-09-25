package de.ubleipzig.extractor;

import org.junit.jupiter.api.RepeatedTest;
import static de.ubleipzig.extractor.impl.HttpClientResponse.getAsyncJDK9ClientResponse;
import static de.ubleipzig.extractor.impl.HttpClientResponse.getSyncJDK9ClientResponse;
import static de.ubleipzig.extractor.impl.HtmlIOService.getHtmlSerialization;


class JDK9ClientTest {
    private static final String FRAGMENT_QUERY = "de/ubleipzig/extractor/fragment.annotation.construct.rq";
    private static final String FILTER = "iiif.ub.uni-leipzig.de";
    private static final String REQUEST_URI = "http://localhost:3030/fuseki/fragments";

    @RepeatedTest(20)
    void asyncJDK9() throws Exception {
        String fragmentQuery = QueryUtil.getQuery(FRAGMENT_QUERY, FILTER);
        String in = getAsyncJDK9ClientResponse(fragmentQuery, REQUEST_URI);
        getHtmlSerialization(in);
    }

    @RepeatedTest(20)
    void syncJDK9() throws Exception {
        String fragmentQuery = QueryUtil.getQuery(FRAGMENT_QUERY, FILTER);
        String in = getSyncJDK9ClientResponse(fragmentQuery, REQUEST_URI);
        getHtmlSerialization(in);
    }
}
