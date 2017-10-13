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

import com.github.jsonldjava.core.JsonLdError;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

/**
 * URIBuilder.
 *
 * @author christopher-johnson
 */
public class URIBuilder {
    private static final String REQUEST_URI = "http://localhost:3030/fuseki/annotations?query=";
    private static final String TEST_QUERY = "de/ubleipzig/extractor/annotation.g.construct.rq";
    private static final String FILTER = "iiif.ub.uni-leipzig.de";

    public static void main(String[] args)
            throws IOException, JsonLdError, InterruptedException, ExecutionException,
            URISyntaxException {
        buildURI();
    }

    public static void buildURI() throws IOException, URISyntaxException {
        String testQuery = QueryUtil.getQuery(TEST_QUERY, FILTER, true);
        URI uri = new URI(REQUEST_URI + testQuery);
    }
}
