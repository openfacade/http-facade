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

package io.github.openfacade.http;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.regex.Pattern;

class PathUtilTest {
    @Test
    public void testPathToRegex() {
        String path = "/users/{id}/orders/{orderId}";
        String expectedRegex = "/users/([^/]+)/orders/([^/]+)";

        String actualRegex = PathUtil.pathToRegex(path);

        Assertions.assertEquals(expectedRegex, actualRegex, "The regex conversion failed");
    }

    @Test
    public void testExtractPathVariableNames() {
        String regex = PathUtil.pathToRegex("/users/{id}/orders/{orderId}");
        Pattern pattern = Pattern.compile(regex);
        String path = "/users/1/orders/2";

        String[] variableKeys = PathUtil.extractPathVariableKeys("/users/{id}/orders/{orderId}");
        Map<String, String> actualVariables = PathUtil.extractPathVariableNames(pattern, variableKeys, path);

        Assertions.assertEquals(2, actualVariables.size(), "Should extract two path variables");
        Assertions.assertEquals("1", actualVariables.get("id"), "Incorrect index for 'id'");
        Assertions.assertEquals("2", actualVariables.get("orderId"), "Incorrect index for 'orderId'");
    }

    @Test
    public void testExtractSinglePathVariable() {
        Pattern pattern = Pattern.compile(PathUtil.pathToRegex("/users/{userId}"));
        String path = "/users/1";

        String[] variableKeys = PathUtil.extractPathVariableKeys("/users/{userId}");
        Map<String, String> actualVariables = PathUtil.extractPathVariableNames(pattern, variableKeys, path);

        Assertions.assertEquals(1, actualVariables.size(), "Should extract one path variable");
        Assertions.assertEquals("1", actualVariables.get("userId"), "Incorrect index for 'userId'");
    }

    @Test
    public void testNoPathVariable() {
        Pattern pattern = Pattern.compile("/users/list");
        String path = "/users/list";

        Map<String, String> actualVariables = PathUtil.extractPathVariableNames(pattern, null, path);

        Assertions.assertEquals(0, actualVariables.size(), "Should not extract any path variables");
    }

    @Test
    public void testConvertVertxPath() {
        Assertions.assertEquals("/api/v1/hello", PathUtil.toVertxPath("/api/v1/hello"));
        Assertions.assertEquals("/api/v1/:hello", PathUtil.toVertxPath("/api/v1/{hello}"));
    }
}
