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

import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.regex.Pattern;

@AllArgsConstructor
public class Route {
    final HttpMethod method;

    final String path;

    final Pattern pattern;

    final String[] pathVariableNames;

    final RequestHandler handler;

    public Map<String, String> pathVariables(String url) {
        return PathUtil.extractPathVariableNames(this.pattern, this.pathVariableNames, url);
    }
}
