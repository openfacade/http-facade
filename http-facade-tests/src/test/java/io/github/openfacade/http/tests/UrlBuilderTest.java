/*
 * Copyright 2024 OpenFacade Authors
 *
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

package io.github.openfacade.http.tests;

import io.github.openfacade.http.HttpSchema;
import io.github.openfacade.http.UrlBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UrlBuilderTest {

    @Test
    public void testUrlBuilder() {
        UrlBuilder urlBuilderWithoutQueryParam =
            new UrlBuilder().setHttpSchema(HttpSchema.HTTP).setHost("127.0.0.1").setPort(8080).setPath("/query");
        String urlWithOutQueryParams = urlBuilderWithoutQueryParam.build();
        String expectUrlNoQueryParam = "http://127.0.0.1:8080/query";
        Assertions.assertEquals(expectUrlNoQueryParam, urlWithOutQueryParams);

        String urlWithQueryParams =
            urlBuilderWithoutQueryParam.duplicate().addParameter("name", "alice").addParameter("age", "30").build();
        String expectUrl = "http://127.0.0.1:8080/query?name=alice&age=30";
        Assertions.assertEquals(expectUrl, urlWithQueryParams);

        String httpsUrl =
            new UrlBuilder().setHttpSchema(HttpSchema.HTTPS).setPath("/query").setHost("127.0.0.1").setPort(443)
                            .build();
        String expectHttpsUrl = "https://127.0.0.1:443/query";
        Assertions.assertEquals(expectHttpsUrl, httpsUrl);
    }

    @Test
    public void testUrlBuilderDuplicate() {
        UrlBuilder urlBuilder =
            new UrlBuilder().setHttpSchema(HttpSchema.HTTP).setHost("127.0.0.1").setPort(8080).setPath("/query")
                            .addParameter("name", "alice").addParameter("age", "30");
        UrlBuilder duplicate = urlBuilder.duplicate();
        String expectUrl = "http://127.0.0.1:8080/query?name=alice&age=30";
        Assertions.assertEquals(expectUrl, duplicate.build());
    }

    @Test
    public void testUrlBuilderNoHostException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new UrlBuilder().build(), "host is required.");
    }
}
