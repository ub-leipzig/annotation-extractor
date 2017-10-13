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

package de.ubleipzig.compliance;

import de.ubleipzig.vocabulary.JSONReader;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

/**
 * IIIFContextsTest.
 *
 * @author christopher-johnson
 */
public class IIIFContextsTest extends AbstractApiTest {

    private static final String testGraphs = "/testApis.json";

    private static final String testContext = "/iiifPresentation2Context.json";

    private static final String testDomain = "https://iiif.ub.uni-leipzig.de";

    private static final String testResourceKey = "presentation";

    @Override
    public Map<String, String> testURI() {
        final URL res = JSONReader.class.getResource(testGraphs);
        final JSONReader svc = new JSONReader(res.getPath());
        return svc.getNamespaces();
    }

    @Override
    public InputStream context() {
        return IIIFContextsTest.class.getResourceAsStream(testContext);
    }

    @Override
    public String domain() {
        return testDomain;
    }

    @Override
    public String testResourceKey() {
        return testResourceKey;
    }
}