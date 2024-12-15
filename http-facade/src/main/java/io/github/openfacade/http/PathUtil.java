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

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathUtil {
    public static String[] extractPathVariableKeys(@NotNull String path) {
        Matcher matcher = Pattern.compile("\\{([^/]+)}").matcher(path);
        int count = 0;
        while (matcher.find()) {
            count++;
        }

        String[] keys = new String[count];
        matcher.reset();
        int i = 0;
        while (matcher.find()) {
            keys[i++] = matcher.group(1);
        }

        return keys;
    }

    /**
     * Converts a path like "/users/{id}" into a regex like "/users/([^/]+)".
     */
    public static String pathToRegex(@NotNull String path) {
        return path.replaceAll("\\{([^/]+)}", "([^/]+)");
    }

    /**
     * Extracts variable names like {id} from the path "/users/{id}".
     */
    @NotNull
    public static Map<String, String> extractPathVariableNames(@NotNull Pattern pattern, String[] pathVariableKeys, @NotNull String path) {
        HashMap<String, String> result = new HashMap<>();
        Matcher matcher = pattern.matcher(path);

        if (!matcher.matches()) {
            return result;
        }

        int groupCount = matcher.groupCount();
        for (int i = 1; i <= groupCount; i++) {
            String variableValue = matcher.group(i);
            result.put(pathVariableKeys[i - 1], variableValue);
        }

        return result;
    }

    /*
     * Converts a path like "/users/{id}" into a Vert.x path like "/users/:id".
     */
    public static String toVertxPath(@NotNull String path) {
        return path.replaceAll("\\{", ":").replaceAll("}", "");
    }
}
